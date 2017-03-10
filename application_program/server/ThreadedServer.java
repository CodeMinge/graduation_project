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
				
				if(str.equals("") || str == null)
					continue;
				
				if (str.equals("q")) {
					br.close();
					pw.close();
					socket.close();
					break;
				}

				// 下面是命令处理，采取新的做法，对于每条命令，我们都新建一个线程去处理，这样可以做到命令的同时处理，提高处理速度
				// 以前必须是一条一条地处理（这是我个人的看法，不一定正确，不正确请大家指出）
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