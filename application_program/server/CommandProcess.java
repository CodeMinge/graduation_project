package server;

import java.io.*;

import command.*;
import message_center.ServerMessage;

/**
 * ��֧�ּ����򵥵������� 1��q --------�˳� 2��login �û��� ���� --------��¼ 3��encrypt table��������
 * property���������� --------���ܣ�ʵ���������������� 4��decrypt table�������� property����������
 * -------����
 */

public class CommandProcess extends Thread {

	public static final String COMMAND_QIUT = "q";
	public static final String COMMAND_LOGIN = "login";
	public static final String COMMAND_ENCRYPT = "encrypt";
	public static final String COMMAND_DECRYPT = "decrypt";

	private String command = null;
	private PrintWriter pw = null;
	private DatabaseConnection dbc = null;
	private String result = null;
	private Command com = null;  // �������ÿһ��CommandProcess�̴߳���һ������
	
	public CommandProcess(String target, PrintWriter pw, DatabaseConnection dbc) {
		command = target;
		this.pw = pw;
		this.dbc = dbc;
	}
		
	public void run() {
		result = process(dbc);
		ServerMessage.ServerMessageOutput(result);
		// ��ͻ��˷�������
		pw.println(result);
		pw.flush();
	}

	/**
	 * ���û����͹�������������������
	 * 
	 * @param target
	 *            ����
	 * @param dbc
	 * @return ��Ϣid
	 */
	public String process(DatabaseConnection dbc) {
		String res = ServerMessage.NULL;
		String[] commandArr = command.split(" ");

		if (commandArr[0].equals(COMMAND_QIUT)) {   //�˳������ǰ������������߲���
			com = new Quit();
			res = com.process(null, null, dbc);
		} else if (commandArr[0].equals(COMMAND_LOGIN)) {
			com = new Login();
			res = com.process(commandArr[1], commandArr[2], dbc);
		} else if (commandArr[0].equals(COMMAND_ENCRYPT)) {
			com = new Encrypt();
			res = com.process(commandArr[1], commandArr[2], dbc);			
		} else if (commandArr[0].equals(COMMAND_DECRYPT)) {
			com = new Decrypt();
			res = com.process(commandArr[1], commandArr[2], dbc);
		}

		return res;
	}
}