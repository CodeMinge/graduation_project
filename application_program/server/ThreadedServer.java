package server;

import java.io.*;
import java.net.*;
import java.sql.SQLException;

import DBConnect.*;
import command.*;
import key_manage.KeyManager;
import message_center.ServerMessage;

public class ThreadedServer extends Thread {
	private Socket socket = null;
	private BufferedReader br = null;
	private PrintWriter pw = null;

	private DBEncryptConnection dbec = null;
	private KeyDBConnection kdbc = null;

	private int loginCount = 0;
	private String name = null; 
	
	private KeyManager km = null;

	public ThreadedServer(Socket s) {
		socket = s;
		try {
			br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			pw = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
			dbec = new DBEncryptConnection();
			kdbc = new KeyDBConnection();
			start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void run() {
		String str = "";

		initialize();

		while (true) {
			try {
				// ��ȡ�ͻ�������
				str = br.readLine();
				System.out.println("Client Message:" + str);

				if (str.equals("") || str == null) // ����Ϣ
					continue;
				else if (str.equals("q")) { // �˳���Ϣ
					Server.userList.remove(name);                        
					pw.println(ServerMessage.QIUT);
					pw.flush();
					br.close();
					pw.close();
					socket.close();
					break;
				} else if(str.equals("manager")) { // ��Կ����Ա
					km = new KeyManager();
					continue;
				}

				// ����ǵ�¼������Ҫ����¼�û���
				if (loginCount == 0) {
					String[] commandArr = str.split(" ");
					if (commandArr[0].equals(CmdProcess.COMMAND_LOGIN)) {
						name = commandArr[1];
						loginCount++;
					}
				}

				// �������������ȡ�µ�����������ÿ��������Ƕ��½�һ���߳�ȥ���������������������ͬʱ������ߴ����ٶ�
				// ��ǰ������һ��һ���ش��������Ҹ��˵Ŀ�������һ����ȷ������ȷ����ָ����
				new CmdProcess(str, pw, dbec, kdbc, km, name).start();

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
	
	// ÿ���û����Ӻ󶼵�����ϵͳ��ʼ��
	private void initialize() {
		// ���������ݿ⣬�������ִ��ʱ��ϳ�
		// һ�����������ϲ��ܽ�����һ������
		String res = dbec.connectTen();
//		ServerMessage.ServerMessageOutput(res);
		res = kdbc.connectTen();
//		ServerMessage.ServerMessageOutput(res);
		
		// �Ȳ���ͻ��˷���Ϣ��
//		pw.println(res); // ��ͻ��˷�������
//		pw.flush();
		if (res.equals(ServerMessage.DBFAIL))
			stop();
		
		try {
			Server.kek.initialize(kdbc);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}