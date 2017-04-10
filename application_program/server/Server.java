package server;

import java.io.*;
import java.net.*;
import java.util.*;

/**
 * 服务器
 */
public class Server {
	public static int PORT = 8000;
	public static String HOST = "localhost";

	// 记录在server登录的所有用户
	public static LinkedList<User> userList = new LinkedList<User>();

	public static void main(String[] args) {
		ServerSocket serverSocket = null;
		Socket socket = null;
		try {
			serverSocket = new ServerSocket(PORT);
			// 等待请求,无请求一直等待
			while (true) {
				System.out.println("Waiting Client");
				socket = serverSocket.accept();// 接受请求
				// System.out.println(socket);
				System.out.println("Client Conect!");
				new ThreadedServer(socket);
			}
		} catch (Exception e) {
			try {
				socket.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		} finally {
			try {
				serverSocket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
