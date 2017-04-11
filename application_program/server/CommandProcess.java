package server;

import java.io.*;

import command.*;
import message_center.ServerMessage;
import sqlProcess.SqlParserUtil;

/**
 * �����
 */
public class CommandProcess extends Thread {
	// ��Щ����������Լ�����ģ��涨ֻ����Сд��
	public static final String COMMAND_QIUT = "q"; // �˳�
	public static final String COMMAND_REGISTER = "register"; // register �û��� ����
																// --------ע��
	public static final String COMMAND_LOGIN = "login"; // login �û��� ����
														// --------��¼
	public static final String COMMAND_CREATE = "create";
	public static final String COMMAND_DROP = "drop";
	public static final String COMMAND_ENCRYPT = "encrypt"; // encrypt table��������
															// property��������---������������
	public static final String COMMAND_DECRYPT = "decrypt"; // decrypt table��������
															// property��������---������������

	// ��sql������Ǵ�Сд�����Եģ�������
	public static final String COMMAND_SELECT = "select";
	public static final String COMMAND_DELETE = "delete from";
	public static final String COMMAND_UPDATE = "update";
	public static final String COMMAND_INSERT = "insert into";

	private String command = null;
	private PrintWriter pw = null;
	private DatabaseConnection dbc = null;
	private String result = null;
	private Command com = null; // �������ÿһ��CommandProcess�̴߳���һ������
	private String userName = null;

	public CommandProcess(String target, PrintWriter pw, DatabaseConnection dbc, String userName) {
		command = target;
		this.pw = pw;
		this.dbc = dbc;
		this.userName = userName;
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

		if (commandArr[0].equals(COMMAND_QIUT)) { // �˳������ǰ������������߲���
			com = new Quit(command);
			res = com.process(null, null, dbc, null);
		} else if (commandArr[0].equals(COMMAND_LOGIN)) { //��¼
			com = new Login(command);
			res = com.process(commandArr[1], commandArr[2], dbc, null);
		} else if (commandArr[0].equals(COMMAND_REGISTER)) { //ע��
			com = new Register(command);
			res = com.process(commandArr[1], commandArr[2], dbc, null);
		} else if (commandArr[0].equals(COMMAND_CREATE)) { // ������
			com = new Create_Table(command);
			res = com.process(commandArr[1], commandArr[2], dbc, userName);
		} else if (commandArr[0].equals(COMMAND_DROP)) { // ɾ����
			com = new Drop_Table(command);
			res = com.process(commandArr[1], commandArr[2], dbc, null);
		} else if (commandArr[0].equals(COMMAND_ENCRYPT)) {
			com = new Encrypt(command);
			res = com.process(commandArr[1], commandArr[2], dbc, null);
		} else if (commandArr[0].equals(COMMAND_DECRYPT)) {
			com = new Decrypt(command);
			res = com.process(commandArr[1], commandArr[2], dbc, null);
		} else {
			SqlParserUtil test = new SqlParserUtil();
			test.getParsedSql(command); // �Ƚ���sql
			
			System.out.println(test.mystr.get(0));
			System.out.println(test.mystr.get(1));
			System.out.println(test.mystr.get(2));

			if (test.mystr.get(0).equals(COMMAND_SELECT)) {
				com = new Select(command);
				res = com.process(test.mystr.get(1), test.mystr.get(2), dbc, null);
			} else if (test.mystr.get(0).equals(COMMAND_DELETE)) {
				com = new Delete(command);
				res = com.process(null, null, dbc, null);
			} else if (test.mystr.get(0).equals(COMMAND_UPDATE)) {
				com = new Update(command);
				res = com.process(test.mystr.get(1), test.mystr.get(2), dbc, null);
			} else if (test.mystr.get(0).equals(COMMAND_INSERT)) {
				com = new Insert(command);
				res = com.process(test.mystr.get(1), test.mystr.get(2), dbc, null);
			} else {
				System.out.println("��Ч��Ϣ");
			}
		}

		return res;
	}
}