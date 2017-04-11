package command;

import java.sql.*;

import message_center.ServerMessage;
import server.DatabaseConnection;

public class Decrypt extends Command {

	public Decrypt(String command) {
		super(command);
	}

	/**
	 * 解密信息解析，对某表的某列进行解密，某表的某列的前提是已经过加密
	 * 
	 * @param para1
	 *            表名
	 * @param para2
	 *            列名
	 * @param dbc
	 * @return 信息id
	 */
	public String process(String para1, String para2, DatabaseConnection dbc, String name) {
		String res = ServerMessage.NULL;
		// 确保表和列存在
		res = tbExists(para1, dbc);
		if (res.equals(ServerMessage.NOTABLE))
			return res;

		ServerMessage.ServerMessageOutput(ServerMessage.EXISTTABLE); // 定位信息

		res = propertyExists(para1, para2, dbc);
		if (res.equals(ServerMessage.NOPROPERTY))
			return res;

		ServerMessage.ServerMessageOutput(ServerMessage.EXISTPROPERTY); // 定位信息

		res = ServerMessage.DECRYPTSUCCESS;
		String key = "";
		String vt = "";

		String sql = "SELECT * from [graduation_project].[dbo].[message_tb] where tb_name = '" + para1
				+ "' and property = '" + para2 + "'";

		PreparedStatement pstmt = null;
		try {

			boolean autoCommit = dbc.dbConn.getAutoCommit();
			// 关闭自动提交功能
			dbc.dbConn.setAutoCommit(false);

			pstmt = (PreparedStatement) dbc.dbConn.prepareStatement(sql);
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) { // 取得解密的密钥和向量
				key = rs.getString("secret_key");
				vt = rs.getString("vector");
				break; // 这里只能有一行
			}
			System.out.println(key + " " + vt);
			// if(key.equals("") || vt.equals(""))
			// throw SQLException;
			// 当这个key和vt不存在的时候，解密是一定会失败的，这个时候应该会回滚

			sql = "SELECT * from [graduation_project].[dbo].[" + para1 + "]";
			pstmt = (PreparedStatement) dbc.dbConn.prepareStatement(sql);
			rs = pstmt.executeQuery();
			while (rs.next()) { // 不停地进行解密
				// 取得加密数据
				String temp = rs.getString(1); // 这里，我的表必须是以第一列为主键，而且不能对第一列进行加密
				String target = rs.getString(para2); // 这是将要解密的数据
				System.out.println(temp + " " + target);

				// 执行解密操作,temp1是解密后的结果
				String temp1 = "";
				sql = "SELECT [graduation_project].[dbo].[Des_Decrypt]('" + target + "', '" + key + "', '" + vt + "')";
				pstmt = (PreparedStatement) dbc.dbConn.prepareStatement(sql);
				ResultSet rs2 = pstmt.executeQuery();
				while (rs2.next()) {
					temp1 = rs2.getString(1);
					break;
				}
				System.out.println(target + " " + temp1);

				// 更新表，将解密后的内容更新到表中
				sql = "UPDATE [graduation_project].[dbo].[" + para1 + "] SET " + para2 + " = '" + temp1 + "' WHERE "
						+ para2 + " = '" + target + "'";
				pstmt = (PreparedStatement) dbc.dbConn.prepareStatement(sql);
				int i = pstmt.executeUpdate();
				if (i == 0) { // 检测解密情况
					res = ServerMessage.DECRYPTFAIL;
				}
			}
			// 记录解密信息，实质是删除原来的的加密信息
			sql = "DELETE FROM message_tb WHERE tb_name = '" + para1 + "' and property = '" + para2 + "'";
			pstmt = (PreparedStatement) dbc.dbConn.prepareStatement(sql);
			pstmt.executeUpdate();

			// 提交事务
			dbc.dbConn.commit();
			// 恢复原来的提交模式
			dbc.dbConn.setAutoCommit(autoCommit);
		} catch (SQLException e) {
			res = ServerMessage.DECRYPTFAIL;
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
