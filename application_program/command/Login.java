package command;

import java.sql.*;

import message_center.ServerMessage;
import server.DatabaseConnection;

public class Login extends Command {

	/**
	 * 登录命令解析
	 * 
	 * @param para1
	 *            用户名
	 * @param para2
	 *            密码
	 * @param dbc
	 * @return 信息id
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
				 * 这里有个问题，从数据取出来的数据后面有许多空格
				 * 本来我的想法是暴力砍掉后面所有的空格，因为我认为字符串后面有许多空格解密出来的东西应该是不同的
				 * 后经过验证发现，解密后也是原来的东西，所以先不砍掉后面所有的空格
				 */
				String temp = rs.getString(1);
				System.out.println(para2 + " t");
				System.out.println(temp + " t");

				//解密
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
