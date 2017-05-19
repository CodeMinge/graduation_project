package command;

import java.io.*;

import DBConnect.DBEncryptConnection;
import DBConnect.KeyDBConnection;
import key_manage.KeyManager;
import message_center.ServerMessage;
import sqlProcess.SqlParserUtil;

/**
 * �����
 */
public class CmdProcess extends Thread {
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
	
	public static final String COMMAND_GENKEK = "generate"; // generate KeyEncryptKey-----������Կ������Կ
	public static final String COMMAND_CHANGE = "change";
	public static final String COMMAND_ESTABLISHKEY = "establish"; // ������Կ

	// ��sql������Ǵ�Сд�����Եģ�������
	public static final String COMMAND_SELECT = "select";
	public static final String COMMAND_DELETE = "delete from";
	public static final String COMMAND_UPDATE = "update";
	public static final String COMMAND_INSERT = "insert into";

	private String command = null;
	private PrintWriter pw = null;
	private DBEncryptConnection dbec = null;
	private KeyDBConnection kdbc = null;
	private KeyManager km = null;
	private String result = null;
	private Command com = null; // �������ÿһ��CommandProcess�̴߳���һ������
	private String userName = null;

	public CmdProcess(String target, PrintWriter pw, DBEncryptConnection dbec, KeyDBConnection kdbc, KeyManager km, String userName) {
		command = target;
		this.pw = pw;
		this.dbec = dbec;
		this.kdbc = kdbc;
		this.km = km;
		this.userName = userName;
	}

	public void run() {
		result = process(dbec, kdbc, km);
		if(result.contains("@")) {
			String [] temp = result.split("@");
			ServerMessage.ServerMessageOutput(temp[0]);
		} else {
			ServerMessage.ServerMessageOutput(result);
		}
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
	public String process(DBEncryptConnection dbec, KeyDBConnection kdbc, KeyManager km) {
		String res = ServerMessage.NULL;
		String[] commandArr = command.split(" ");

		if (commandArr[0].equals(COMMAND_QIUT)) { // �˳������ǰ������������߲���
			com = new Quit(command);
			res = com.process(null, null, dbec, kdbc, null);
		} else if (commandArr[0].equals(COMMAND_LOGIN)) { //��¼
			com = new Login(command);
			res = com.process(commandArr[1], commandArr[2], dbec, kdbc, null);
		} else if (commandArr[0].equals(COMMAND_REGISTER)) { //ע��
			com = new Register(command);
			res = com.process(commandArr[1], commandArr[2], dbec, kdbc, null);
		} else if (commandArr[0].equals(COMMAND_CREATE)) { // ������
			com = new Create_Table(command);
			res = com.process(commandArr[1], commandArr[2], dbec, kdbc, userName);
		} else if (commandArr[0].equals(COMMAND_DROP)) { // ɾ����
			com = new Drop_Table(command);
			res = com.process(commandArr[1], commandArr[2], dbec, kdbc, userName);
		} else if (commandArr[0].equals(COMMAND_ENCRYPT)) { //������������
			com = new Encrypt(command);
			res = com.process(commandArr[1], commandArr[2], dbec, kdbc, userName);
		} else if (commandArr[0].equals(COMMAND_DECRYPT)) { //�����������
			com = new Decrypt(command);
			res = com.process(commandArr[1], commandArr[2], dbec, kdbc, userName);
		} else if(commandArr[0].equals(COMMAND_GENKEK) || commandArr[0].equals(COMMAND_CHANGE)) {
			km.generate_KeyEncryptKey(dbec, kdbc);
			res = ServerMessage.GENKEKSUCCESS;
		} else if(commandArr[0].equals(COMMAND_ESTABLISHKEY)) {
			km.generate_Key(commandArr[2], dbec, kdbc);
			res = ServerMessage.EKEYSUCCESS;
		}
		else {
			SqlParserUtil test = new SqlParserUtil();
			test.getParsedSql(command); // �Ƚ���sql
			
			System.out.println(test.mystr.get(0));
			System.out.println(test.mystr.get(1));
			System.out.println(test.mystr.get(2));

			if (test.mystr.get(0).equals(COMMAND_SELECT)) {
				com = new Select(command);
				res = com.process(test.mystr.get(1), test.mystr.get(2), dbec, kdbc, userName);
			} else if (test.mystr.get(0).equals(COMMAND_DELETE)) {
				com = new Delete(command);
				res = com.process(test.mystr.get(1), test.mystr.get(2), dbec, kdbc, userName);
			} else if (test.mystr.get(0).equals(COMMAND_UPDATE)) {
				com = new Update(command);
				res = com.process(test.mystr.get(1), test.mystr.get(2), dbec, kdbc, userName);
			} else if (test.mystr.get(0).equals(COMMAND_INSERT)) {
				com = new Insert(command);
				res = com.process(test.mystr.get(1), test.mystr.get(2), dbec, kdbc, userName);
			} else {
				System.out.println("��Ч��Ϣ");
			}
		}

		return res;
	}
}