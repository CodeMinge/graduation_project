package server;

import java.io.*;
import java.net.*;

import message_center.ServerMessage;

public class ThreadedServer extends Thread {

	private Socket socket = null;
	private BufferedReader br = null;
	private PrintWriter pw = null;

	private DatabaseConnection dbc = null;

	public ThreadedServer(Socket s) {
		socket = s;
		try {
			br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			pw = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
			dbc = new DatabaseConnection();
			start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void run() {
		String str = "";
		String res = ServerMessage.NULL;
		int count = 0;
		
		// ���������ݿ⣬�������ִ��ʱ��ϳ�
		// һ�����������ϲ��ܽ�����һ������
		while (!dbc.connect().equals(ServerMessage.DBSUCCESS)) {
			res = ServerMessage.DBFAIL;
			ServerMessage.ServerMessageOutput(res);
			
			count ++;
			if(count > 10) {
				break;
			}
			
			//���������ݿ�Ļ����ȵȴ�һ��
			try {
				sleep(10000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		// һֱ���������ݿ�,ͣ��	
		if(count > 10) {
			stop();
		}
				
		while (true)
		{
			try {
				// ��ȡ�ͻ�������
				str = br.readLine();
				System.out.println("Client Message:" + str);
				
				if(str.equals("") || str == null)
					continue;
				
				if (str.equals("q")) {
					br.close();
					pw.close();
					socket.close();
					break;
				}

				// �������������ȡ�µ�����������ÿ��������Ƕ��½�һ���߳�ȥ���������������������ͬʱ������ߴ����ٶ�
				// ��ǰ������һ��һ���ش��������Ҹ��˵Ŀ�������һ����ȷ������ȷ����ָ����
				new Command(str, pw, dbc).start();
				
			} catch (Exception e) {
				try {
					br.close();
					pw.close();
					socket.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		}
	}
}