package client;

import java.io.*;
import java.net.*;
import java.util.*;

import server.Server;
import ui.LoginUI;

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

			rsm = new ReadServerMessage(this, socket, br);// 从服务器读取消息
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		Client client = new Client();
		new LoginUI(client);
//		TestProgram.selfTest();
//		TestProgram.interactTest();
//		TestProgram.encrypt_decrypt_mix_test();
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
}