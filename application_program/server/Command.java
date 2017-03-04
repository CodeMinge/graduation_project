package server;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import message_center.ServerMessage;
/**
 * ��֧�ּ����򵥵�������
 *  1��q --------�˳�
 *  2��login �û��� ���� --------��¼
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
	 * ���û����͹�������������������
	 * @param target  ����
	 * @param dbc
	 * @return ��Ϣid
	 */
	public String process(String target, DatabaseConnection dbc) {
		// ����������
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
	 * ��¼�������
	 * @param userName �û���
	 * @param password ����
	 * @param dbc
	 * @return   ��Ϣid
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
				 * �����и����⣬������ȡ���������ݺ��������ո�
				 * �����ҵ��뷨�Ǳ��������������еĿո���Ϊ����Ϊ�ַ������������ո���ܳ����Ķ���Ӧ���ǲ�ͬ��
				 * �󾭹���֤���֣����ܺ�Ҳ��ԭ���Ķ����������Ȳ������������еĿո�
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