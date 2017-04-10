package server;

import java.io.*;
import java.net.*;
import java.util.*;

/**
 * ������
 */
public class Server {
	public static int PORT = 8000;
	public static String HOST = "localhost";

	// ��¼��server��¼�������û�
	public static LinkedList<User> userList = new LinkedList<User>();

	public static void main(String[] args) {
		ServerSocket serverSocket = null;
		Socket socket = null;
		try {
			serverSocket = new ServerSocket(PORT);
			// �ȴ�����,������һֱ�ȴ�
			while (true) {
				System.out.println("Waiting Client");
				socket = serverSocket.accept();// ��������
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
