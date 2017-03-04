package server;

import java.io.*;
import java.net.*;

import message_center.ServerMessage;

public class ThreadedServer extends Thread {

	private Socket socket = null;
	private BufferedReader br = null;
	private PrintWriter pw = null;
	private Command comm = null;
	private DatabaseConnection dbc = null;

	public ThreadedServer(Socket s) {
		socket = s;
		try {
			br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			pw = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
			comm = new Command();
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
		
		// 先连接数据库，这个步骤执行时间较长
		// 一定得先连接上才能进行下一步操作
		while (!dbc.connect().equals(ServerMessage.DBSUCCESS)) {
			res = ServerMessage.DBFAIL;
			ServerMessage.ServerMessageOutput(res);
			
			count ++;
			if(count > 10) {
				break;
			}
			
			//连不上数据库的话，先等待一会
			try {
				sleep(10000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		// 一直连不上数据库,停机	
		if(count > 10) {
			stop();
		}
				
		while (true)
		{
			try {
				// 读取客户端数据
				str = br.readLine();
				System.out.println("Client Message:" + str);
				// 命令处理，res为命令处理结果
				res = comm.process(str, dbc);
				ServerMessage.ServerMessageOutput(res);
				// 向客户端发送数据
				pw.println(res);
				pw.flush();
				if (res == ServerMessage.QIUT) {
					br.close();
					pw.close();
					socket.close();
					break;
				}
				
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