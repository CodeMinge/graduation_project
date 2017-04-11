package server;

import java.io.*;
import java.net.*;

import message_center.ServerMessage;

public class ThreadedServer extends Thread {
	private Socket socket = null;
	private BufferedReader br = null;
	private PrintWriter pw = null;

	private DatabaseConnection dbc = null;

	private int loginCount = 0;
	private String name = null;

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

		// 先连接数据库，这个步骤执行时间较长
		// 一定得先连接上才能进行下一步操作
		String res = dbc.connect_ten();
		ServerMessage.ServerMessageOutput(res);
		pw.println(res); // 向客户端发送数据
		pw.flush();
		if (res.equals(ServerMessage.DBFAIL))
			stop();

		while (true) {
			try {
				// 读取客户端数据
				str = br.readLine();
				System.out.println("Client Message:" + str);

				// 无信息
				if (str.equals("") || str == null)
					continue;

				// 退出信息
				if (str.equals("q")) {
					Server.userList.remove(name);                        
					pw.println(ServerMessage.QIUT);
					pw.flush();
					br.close();
					pw.close();
					socket.close();
					break;
				}

				// 如果是登录命令需要，记录用户名
				if (loginCount == 0) {
					String[] commandArr = str.split(" ");
					if (commandArr[0].equals(CommandProcess.COMMAND_LOGIN)) {
						name = commandArr[1];
					}
					loginCount++;
				}

				// 下面是命令处理，采取新的做法，对于每条命令，我们都新建一个线程去处理，这样可以做到命令的同时处理，提高处理速度
				// 以前必须是一条一条地处理（这是我个人的看法，不一定正确，不正确请大家指出）
				new CommandProcess(str, pw, dbc, name).start();

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