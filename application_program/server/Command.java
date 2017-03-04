package server;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import message_center.ServerMessage;
/**
 * 先支持几个简单的命令行
 *  1、q --------退出
 *  2、login 用户名 密码 --------登录
 *
 */

public class Command {
	
	public static final String COMMAND_QIUT = "q";
	public static final String COMMAND_LOGIN = "login";
	
	private String command = null;

	public void setCommand(String target) {
		command = target;
	}

	/**
	 * 对用户发送过来的命令做解析处理
	 * @param target  命令
	 * @param dbc
	 * @return 信息id
	 */
	public String process(String target, DatabaseConnection dbc) {
		// 处理命令行
		setCommand(target);
		String[] commandArr = command.split(" ");

		if (commandArr[0].equals(COMMAND_QIUT)) {
			return ServerMessage.QIUT;
		} else if (commandArr[0].equals("login")) {
			return loginProcess(commandArr[1], commandArr[2], dbc);
		}
		
		return ServerMessage.NULL;
	}

	/**
	 * 登录命令解析
	 * @param userName 用户名
	 * @param password 密码
	 * @param dbc
	 * @return   信息id
	 */
	private String loginProcess(String userName, String password, DatabaseConnection dbc) {
		String res = ServerMessage.NULL;
		String sql = "SELECT password from [graduation_project].[dbo].[user_tb] where username = ?";

		PreparedStatement pstmt = null;
		try {
			pstmt = (PreparedStatement) dbc.dbConn.prepareStatement(sql);
			pstmt.setString(1, userName);
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				/*
				 * 这里有个问题，从数据取出来的数据后面有许多空格
				 * 本来我的想法是暴力砍掉后面所有的空格，因为我认为字符串后面有许多空格解密出来的东西应该是不同的
				 * 后经过验证发现，解密后也是原来的东西，所以先不砍掉后面所有的空格
				 */
				String temp = rs.getString(1);
				System.out.println(password + " t");
				System.out.println(temp + " t");
				
				sql = "SELECT [graduation_project].[dbo].[DESDecrypt]('"+ temp +"', '20111219', '12345678');";
				pstmt = (PreparedStatement) dbc.dbConn.prepareStatement(sql);
				ResultSet rs2 = pstmt.executeQuery();
				
				while(rs2.next()) {
					temp = rs2.getString(1);
					break;
				}
				
				System.out.println(password + " t");
				System.out.println(temp + " t");
				
				if(temp.equals(password)) {
					res = ServerMessage.LOGINSUCCESS;
					break;
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return res;
	}
}