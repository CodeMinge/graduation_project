package server;

import java.io.*;

import command.*;
import message_center.ServerMessage;
import sqlProcess.SqlParserUtil;

/**
 * 命令处理
 */
public class CommandProcess extends Thread {

	// 这些命令语句是自己定义的，规定只能是小写的
	public static final String COMMAND_QIUT = "q";            //退出
	public static final String COMMAND_LOGIN = "login";       //login 用户名 密码 --------登录
	public static final String COMMAND_ENCRYPT = "encrypt";   //encrypt table（表名） property（列名）---设置敏感属性
	public static final String COMMAND_DECRYPT = "decrypt";   //decrypt table（表名） property（列名）---消除敏感属性

	// 而sql语句则是大小写都可以的，需留意
	public static final String COMMAND_SELECT = "select"; 
	public static final String COMMAND_DELETE = "delete from";
	public static final String COMMAND_UPDATE = "update";
	public static final String COMMAND_INSERT = "insert";
	
	private String command = null;
	private PrintWriter pw = null;
	private DatabaseConnection dbc = null;
	private String result = null;
	private Command com = null;  // 这个变量每一个CommandProcess线程存在一个即可
	
	public CommandProcess(String target, PrintWriter pw, DatabaseConnection dbc) {
		command = target;
		this.pw = pw;
		this.dbc = dbc;
	}
		
	public void run() {
		result = process(dbc);
		ServerMessage.ServerMessageOutput(result);
		// 向客户端发送数据
		pw.println(result);
		pw.flush();
	}

	/**
	 * 对用户发送过来的命令做解析处理
	 * 
	 * @param target
	 *            命令
	 * @param dbc
	 * @return 信息id
	 */
	public String process(DatabaseConnection dbc) {
		String res = ServerMessage.NULL;
		String[] commandArr = command.split(" ");

		if (commandArr[0].equals(COMMAND_QIUT)) {   //退出命令被提前处理，这个步骤走不到
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
	        test.getParsedSql(command);  // 先解析sql
	        
//	        System.out.println(test.mystr[0] + "----" + test.mystr[1] + "----" + test.mystr[2]);  // 定位信息
	        
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
//	        	System.out.println("无效信息");
//	        }
		}

		return res;
	}
}