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
				// 读取客户端数据
				str = br.readLine();
				System.out.println("Client Message:" + str);

				if (str.equals("") || str == null) // 无信息
					continue;
				else if (str.equals("q")) { // 退出信息
					Server.userList.remove(name);                        
					pw.println(ServerMessage.QIUT);
					pw.flush();
					br.close();
					pw.close();
					socket.close();
					break;
				} else if(str.equals("manager")) { // 密钥管理员
					km = new KeyManager();
					continue;
				}

				// 如果是登录命令需要，记录用户名
				if (loginCount == 0) {
					String[] commandArr = str.split(" ");
					if (commandArr[0].equals(CmdProcess.COMMAND_LOGIN)) {
						name = commandArr[1];
						loginCount++;
					}
				}

				// 下面是命令处理，采取新的做法，对于每条命令，我们都新建一个线程去处理，这样可以做到命令的同时处理，提高处理速度
				// 以前必须是一条一条地处理（这是我个人的看法，不一定正确，不正确请大家指出）
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
	
	// 每个用户连接后都得做的系统初始化
	private void initialize() {
		// 先连接数据库，这个步骤执行时间较长
		// 一定得先连接上才能进行下一步操作
		String res = dbec.connectTen();
//		ServerMessage.ServerMessageOutput(res);
		res = kdbc.connectTen();
//		ServerMessage.ServerMessageOutput(res);
		
		// 先不向客户端发信息了
//		pw.println(res); // 向客户端发送数据
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