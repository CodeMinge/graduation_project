package command;

import java.sql.*;

import key_manage.KeyManager;
import message_center.ServerMessage;
import server.DatabaseConnection;

public class Register extends Command {

	public Register(String command) {
		super(command);
	}

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
		String res = ServerMessage.REGISTERSUCCESS;
		String sql = "SELECT password from [graduation_project].[dbo].[user_tb] where username = ?";

		PreparedStatement pstmt = null;
		try {
			boolean autoCommit = dbc.dbConn.getAutoCommit();
			// 关闭自动提交功能
			dbc.dbConn.setAutoCommit(false);

			pstmt = (PreparedStatement) dbc.dbConn.prepareStatement(sql);
			pstmt.setString(1, para1);
			ResultSet rs = pstmt.executeQuery();
			int count = 0;
			while (rs.next()) {
				count++;
			}

			if (count != 0) { // 存在相同的用户名
				res = ServerMessage.REGISTERFAIL;
			} else {

				// 分配密钥，并将密钥插入到密钥表中
				KeyManager km = new KeyManager();
				km.save_KeyAndVector(para1, dbc);

				// 利用分配的密钥执行加密操作,temp1是加密后的结果
				String temp1 = "";
				sql = "SELECT [graduation_project].[dbo].[Des_Encrypt]('" + para2 + "', '" + km.key + "', '" + km.vector
						+ "')";
				pstmt = (PreparedStatement) dbc.dbConn.prepareStatement(sql);
				ResultSet rs2 = pstmt.executeQuery();
				while (rs2.next()) {
					temp1 = rs2.getString(1);
					break;
				}
				System.out.println(para2 + " " + temp1);

				// 将加密的密码加入到密码表中
				sql = "insert into [graduation_project].[dbo].[user_tb] values (?,?,?)";
				pstmt = (PreparedStatement) dbc.dbConn.prepareStatement(sql);
				pstmt.setString(1, para1);
				pstmt.setString(2, temp1);
				pstmt.setInt(3, 0);
				int i = pstmt.executeUpdate();
				if (i == 0) {
					res = ServerMessage.REGISTERFAIL;
				}

				// 创建一个与用户名同名的表，这个表存储了该用户所能管理的表，如果这个表存在则不创建
				boolean b = false;
				sql = "select name from sysobjects where xtype='u'";
				pstmt = (PreparedStatement) dbc.dbConn.prepareStatement(sql);
				rs2 = pstmt.executeQuery();
				while (rs2.next()) {
					temp1 = rs2.getString(1);
					if (temp1.equals(para1)) {
						b = true;
					}
				}
				if (!b) {
					sql = "create table " + para1 + "(table_name varchar(20))";
					pstmt = (PreparedStatement) dbc.dbConn.prepareStatement(sql);
					pstmt.executeUpdate();
				}
			}

			// 提交事务
			dbc.dbConn.commit();
			// 恢复原来的提交模式
			dbc.dbConn.setAutoCommit(autoCommit);
		} catch (SQLException e) {
			res = ServerMessage.REGISTERFAIL;
			try {
				// 回滚、取消前述操作
				dbc.dbConn.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}

			e.printStackTrace();
		}

		return res;
	}
}