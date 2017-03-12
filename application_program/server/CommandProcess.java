package server;

import java.io.*;

import command.*;
import message_center.ServerMessage;

/**
 * 先支持几个简单的命令行 1、q --------退出 2、login 用户名 密码 --------登录 3、encrypt table（表名）
 * property（属性名） --------加密，实质是设置敏感属性 4、decrypt table（表名） property（属性名）
 * -------解密
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