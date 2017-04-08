package client;

import java.io.*;
import java.net.*;
import java.util.*;

import server.Server;
import ui.LoginUI;

/**
 * �ͻ���
 */
public class Client {

	public Socket socket = null;
	public BufferedReader br = null;
	public PrintWriter pw = null;
	public Scanner scanner = new Scanner(System.in);// �Ӽ��̶�ȡ
	public ReadServerMessage rsm = null;

	public Client() {
		try {
			// �����ͻ���socket
			socket = new Socket(Server.HOST, Server.PORT);
			// ��ȡ�ӿͻ��˷�������Ϣ
			br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			// д����Ϣ����������
			pw = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())));

			rsm = new ReadServerMessage(this, socket, br);// �ӷ�������ȡ��Ϣ
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