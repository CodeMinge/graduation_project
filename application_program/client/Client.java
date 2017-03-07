package client;

import java.io.*;
import java.net.*;
import java.util.*;

import server.Server;

/**
 * �ͻ���
 */
public class Client {

	public static void main(String[] args) {
		Socket socket = null;
		BufferedReader br = null;
		PrintWriter pw = null;
		Scanner scanner = new Scanner(System.in);// �Ӽ��̶�ȡ
		try {
			// �����ͻ���socket
			socket = new Socket(Server.HOST, Server.PORT);
			// ��ȡ�ӿͻ��˷�������Ϣ
			br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			// д����Ϣ����������
			pw = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())));
			new ReadServerMessage(socket, br);// �ӷ�������ȡ��Ϣ
			
			selfTest(pw);
//			while (true) {
//				String temp = scanner.nextLine();// �Ӽ��̶�ȡһ��
//				pw.println(temp);// д��������
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
	 * �Բ������������֮��Ҫ��ͣ�٣���������������ͣ�ٽϳ�
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
	 * �˳������Բ����
	 */
	public static void quitSelfTest(PrintWriter pw) {
		String temp = "q";
		pw.println(temp);// д��������
		pw.flush();
	}
	
	/**
	 * ��¼�����Բ����
	 */
	public static void loginSelfTest(PrintWriter pw) {
		String temp = "login jd ����������";
		pw.println(temp);// д��������
		pw.flush();
	}
	
	/**
	 * ���ܶ������Գ���
	 */
	public static void encryptSelfTest(PrintWriter pw) {
		String temp = "encrypt worker_table card_number";
		pw.println(temp);// д��������
		pw.flush();
	}
	
	/**
	 * ���ܶ������Գ���
	 */
	public static void decryptSelfTest(PrintWriter pw) {
		String temp = "decrypt worker_table card_number";
		pw.println(temp);// д��������
		pw.flush();
	}
}