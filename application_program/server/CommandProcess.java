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
	public static final String COMMAND_QIUT = "q";            //�˳�
	public static final String COMMAND_LOGIN = "login";       //login �û��� ���� --------��¼
	public static final String COMMAND_ENCRYPT = "encrypt";   //encrypt table�������� property��������---������������
	public static final String COMMAND_DECRYPT = "decrypt";   //decrypt table�������� property��������---������������

	// ��sql������Ǵ�Сд�����Եģ�������
	public static final String COMMAND_SELECT = "select"; 
	public static final String COMMAND_DELETE = "delete from";
	public static final String COMMAND_UPDATE = "update";
	public static final String COMMAND_INSERT = "insert";
	
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
			com = new Quit(command);
			res = com.process(null, null, dbc);
		} else if (commandArr[0].equals(COMMAND_LOGIN)) {
			com = new Login(command);
			res = com.process(commandArr[1], commandArr[2], dbc);
		} else if (commandArr[0].equals(COMMAND_ENCRYPT)) {
			com = new Encrypt(command);
			res = com.process(commandArr[1], commandArr[2], dbc);			
		} else if (commandArr[0].equals(COMMAND_DECRYPT)) {
			com = new Decrypt(command);
			res = com.process(commandArr[1], commandArr[2], dbc);
		} else {
			SqlParserUtil test=new SqlParserUtil();
	        test.getParsedSql(command);  // �Ƚ���sql
	        
//	        System.out.println(test.mystr[0] + "----" + test.mystr[1] + "----" + test.mystr[2]);  // ��λ��Ϣ
	        
//	        if(test.mystr[0].equals(COMMAND_SELECT)) {
//	        	
//	        } else if(test.mystr[0].equals(COMMAND_DELETE)) {
//	        	com = new Delete(command);
//				res = com.process(test.mystr[1], test.mystr[2], dbc);
//	        } else if(test.mystr[0].equals(COMMAND_UPDATE)) {
//	        	
//	        } else if(test.mystr[0].equals(COMMAND_INSERT)) {
//	        	
//	        } else {
//	        	System.out.println("��Ч��Ϣ");
//	        }
		}

		return res;
	}
}