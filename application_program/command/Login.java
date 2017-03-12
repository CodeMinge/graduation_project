package command;

import java.sql.*;

import message_center.ServerMessage;
import server.DatabaseConnection;

public class Login extends Command {

	/**
	 * ��¼�������
	 * 
	 * @param para1
	 *            �û���
	 * @param para2
	 *            ����
	 * @param dbc
	 * @return ��Ϣid
	 */
	public String process(String para1, String para2, DatabaseConnection dbc) {
		String res = ServerMessage.LOGINFAIL;
		String sql = "SELECT password from [graduation_project].[dbo].[user_tb] where username = ?";

		PreparedStatement pstmt = null;
		try {
			pstmt = (PreparedStatement) dbc.dbConn.prepareStatement(sql);
			pstmt.setString(1, para1);
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				/*
				 * �����и����⣬������ȡ���������ݺ��������ո�
				 * �����ҵ��뷨�Ǳ��������������еĿո���Ϊ����Ϊ�ַ������������ո���ܳ����Ķ���Ӧ���ǲ�ͬ��
				 * �󾭹���֤���֣����ܺ�Ҳ��ԭ���Ķ����������Ȳ������������еĿո�
				 */
				String temp = rs.getString(1);
				System.out.println(para2 + " t");
				System.out.println(temp + " t");

				//����
				sql = "SELECT [graduation_project].[dbo].[Des_Decrypt]('" + temp + "', '20111219', '12345678');";
				pstmt = (PreparedStatement) dbc.dbConn.prepareStatement(sql);
				ResultSet rs2 = pstmt.executeQuery();

				while (rs2.next()) {
					temp = rs2.getString(1);
					break;
				}

				System.out.println(para2 + " t");
				System.out.println(temp + " t");

				if (temp.equals(para2)) {
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
