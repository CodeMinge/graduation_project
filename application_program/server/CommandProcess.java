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
	public static final String COMMAND_QIUT = "q"; // 退出
	public static final String COMMAND_REGISTER = "register"; // register 用户名 密码
																// --------注册
	public static final String COMMAND_LOGIN = "login"; // login 用户名 密码
														// --------登录
	public static final String COMMAND_CREATE = "create";
	public static final String COMMAND_DROP = "drop";
	public static final String COMMAND_ENCRYPT = "encrypt"; // encrypt table（表名）
															// property（列名）---设置敏感属性
	public static final String COMMAND_DECRYPT = "decrypt"; // decrypt table（表名）
															// property（列名）---消除敏感属性

	// 而sql语句则是大小写都可以的，需留意
	public static final String COMMAND_SELECT = "select";
	public static final String COMMAND_DELETE = "delete from";
	public static final String COMMAND_UPDATE = "update";
	public static final String COMMAND_INSERT = "insert into";

	private String command = null;
	private PrintWriter pw = null;
	private DatabaseConnection dbc = null;
	private String result = null;
	private Command com = null; // 这个变量每一个CommandProcess线程存在一个即可
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

		if (commandArr[0].equals(COMMAND_QIUT)) { // 退出命令被提前处理，这个步骤走不到
			com = new Quit(command);
			res = com.process(null, null, dbc, null);
		} else if (commandArr[0].equals(COMMAND_LOGIN)) { //登录
			com = new Login(command);
			res = com.process(commandArr[1], commandArr[2], dbc, null);
		} else if (commandArr[0].equals(COMMAND_REGISTER)) { //注册
			com = new Register(command);
			res = com.process(commandArr[1], commandArr[2], dbc, null);
		} else if (commandArr[0].equals(COMMAND_CREATE)) { // 创建表
			com = new Create_Table(command);
			res = com.process(commandArr[1], commandArr[2], dbc, userName);
		} else if (commandArr[0].equals(COMMAND_DROP)) { // 删除表
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
			test.getParsedSql(command); // 先解析sql
			
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
				System.out.println("无效信息");
			}
		}

		return res;
	}
}