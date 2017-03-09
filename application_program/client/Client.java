package client;

import java.io.*;
import java.net.*;
import java.util.*;

import server.Server;

/**
 * 客户端
 */
public class Client {

	public Socket socket = null;
	public BufferedReader br = null;
	public PrintWriter pw = null;
	public Scanner scanner = new Scanner(System.in);// 从键盘读取
	public ReadServerMessage rsm = null;

	public Client() {
		try {
			// 创建客户端socket
			socket = new Socket(Server.HOST, Server.PORT);
			// 读取从客户端发来的消息
			br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			// 写入信息到服务器端
			pw = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())));

			rsm = new ReadServerMessage(socket, br);// 从服务器读取消息
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
//		selfTest();
//		interactTest();
		encrypt_decrypt_mix_test();
	}

	public void close() {
		try {
			System.out.println("close......");
			br.close();
			br = null;
			pw.close();
			pw = null;
			socket.close();
			socket = null;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 交互测试
	 */
	public static void interactTest() {
		Client client = new Client();
		
		while (true) {
			String temp = client.scanner.nextLine();// 从键盘读取一行
			client.pw.println(temp);// 写到服务器
			client.pw.flush();
			if (temp.equals("q")) {
				break;
			}
		}
	}

	/**
	 * 自测程序
	 */
	public static void selfTest() {
		Client client = new Client();

//		loginSelfTest(client);
//
//		try {
//			Thread.sleep(10000); // 两个测试之间要有停顿，否则出错，我这里的停顿较长
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}
//
//		quitSelfTest(client);
//
//		encryptSelfTest(client);
		decryptSelfTest(client);

		client.close();
	}

	/**
	 * 退出命令自测程序
	 */
	public static void quitSelfTest(Client client) {
		String temp = "q";
		client.pw.println(temp);// 写到服务器
		client.pw.flush();
	}

	/**
	 * 登录命令自测程序
	 */
	public static void loginSelfTest(Client client) {
		String temp = "login jd 金胖子死了";
		client.pw.println(temp);// 写到服务器
		client.pw.flush();
	}

	/**
	 * 加密独立测试程序
	 */
	public static void encryptSelfTest(Client client) {
		String temp = "encrypt worker_table card_number";
		client.pw.println(temp);// 写到服务器
		client.pw.flush();
	}

	/**
	 * 解密独立测试程序
	 */
	public static void decryptSelfTest(Client client) {
		String temp = "decrypt worker_table card_number";
		client.pw.println(temp);// 写到服务器
		client.pw.flush();
	}

	/**
	 * 加密解密混合测试程序 这个测试主要是测试锁和事务的
	 */
	public static void encrypt_decrypt_mix_test() {
		Client c1 = new Client();
		Client c2 = new Client();
		String temp1 = "encrypt worker_table card_number";
		c1.pw.println(temp1);// 写到服务器
		c1.pw.flush();
		
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		String temp2 = "decrypt worker_table card_number";
		c2.pw.println(temp2);// 写到服务器
		c2.pw.flush();
	}
}