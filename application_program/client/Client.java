package client;

import java.io.*;
import java.net.*;
import java.util.*;

import server.Server;

/**
 * 客户端
 */
public class Client {

	public static void main(String[] args) {
		Socket socket = null;
		BufferedReader br = null;
		PrintWriter pw = null;
		Scanner scanner = new Scanner(System.in);// 从键盘读取
		try {
			// 创建客户端socket
			socket = new Socket(Server.HOST, Server.PORT);
			// 读取从客户端发来的消息
			br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			// 写入信息到服务器端
			pw = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())));
			new ReadServerMessage(socket, br);// 从服务器读取消息
			
			selfTest(pw);
//			while (true) {
//				String temp = scanner.nextLine();// 从键盘读取一行
//				pw.println(temp);// 写到服务器
//				pw.flush();
//				if (temp.equals("q")) {
//					break;
//				}
//			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				System.out.println("close......");
				br.close();         br = null;
				pw.close();         pw = null;
				socket.close();     socket = null;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 自测程序，两个测试之间要有停顿，否则出错，我这里的停顿较长
	 */
	public static void selfTest(PrintWriter pw) {
//		loginSelfTest(pw);
//		
//		try {
//			Thread.sleep(10000);
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}
//		
//		quitSelfTest(pw);
		
//		encryptSelfTest(pw);
		decryptSelfTest(pw);
	}

	/**
	 * 退出命令自测程序
	 */
	public static void quitSelfTest(PrintWriter pw) {
		String temp = "q";
		pw.println(temp);// 写到服务器
		pw.flush();
	}
	
	/**
	 * 登录命令自测程序
	 */
	public static void loginSelfTest(PrintWriter pw) {
		String temp = "login jd 金胖子死了";
		pw.println(temp);// 写到服务器
		pw.flush();
	}
	
	/**
	 * 加密独立测试程序
	 */
	public static void encryptSelfTest(PrintWriter pw) {
		String temp = "encrypt worker_table card_number";
		pw.println(temp);// 写到服务器
		pw.flush();
	}
	
	/**
	 * 解密独立测试程序
	 */
	public static void decryptSelfTest(PrintWriter pw) {
		String temp = "decrypt worker_table card_number";
		pw.println(temp);// 写到服务器
		pw.flush();
	}
}