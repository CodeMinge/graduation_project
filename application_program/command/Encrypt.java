package command;

import java.sql.*;

import message_center.ServerMessage;
import server.DatabaseConnection;

public class Encrypt extends Command {

	public Encrypt(String command) {
		super(command);
	}

	/**
	 * 加密信息解析，对某表的某列进行加密，某表的某列的前提是未经过加密
	 * 
	 * @param para1
	 *            表名
	 * @param para2
	 *            列名
	 * @param dbc
	 * @return 信息id
	 */
	public String process(String para1, String para2, DatabaseConnection dbc) {
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

		// 加锁，保证加密是流程的完整性
		synchronized (workerTbLock) {
			res = ServerMessage.ENCRYPTSUCCESS;
			// 首先取得密钥和向量
			String key = kav.getKey();
			String vt = kav.getVector();
			System.out.println(key + " " + vt);

			String sql = "SELECT * from [graduation_project].[dbo].[" + para1 + "]";

			PreparedStatement pstmt = null;
			try {

				boolean autoCommit = dbc.dbConn.getAutoCommit();
				// 关闭自动提交功能
				dbc.dbConn.setAutoCommit(false);

				pstmt = (PreparedStatement) dbc.dbConn.prepareStatement(sql);
				ResultSet rs = pstmt.executeQuery();
				while (rs.next()) { // 不停地进行加密
					// 取得加密数据
					String temp = rs.getString(1); // 这里，我的表必须是以第一列为主键，而且不能对第一列进行加密
					String target = rs.getString(para2); // 这是将要加密的数据
					System.out.println(temp + " " + target);

					// 执行加密操作,temp1是加密后的结果
					String temp1 = "";
					sql = "SELECT [graduation_project].[dbo].[Des_Encrypt]('" + target + "', '" + key + "', '" + vt
							+ "')";
					pstmt = (PreparedStatement) dbc.dbConn.prepareStatement(sql);
					ResultSet rs2 = pstmt.executeQuery();
					while (rs2.next()) {
						temp1 = rs2.getString(1);
						break;
					}
					System.out.println(target + " " + temp1);

					// 更新表，将加密后的内容更新到表中
					sql = "UPDATE [graduation_project].[dbo].[" + para1 + "] SET " + para2 + " = '" + temp1
							+ "' WHERE " + para2 + " = '" + target + "'";
					pstmt = (PreparedStatement) dbc.dbConn.prepareStatement(sql);
					int i = pstmt.executeUpdate();
					if (i == 0) { // 检测加密情况
						res = ServerMessage.ENCRYPTFAIL;
					}
				}
				// 记录加密信息
				sql = "insert into message_tb(tb_name,property,algorithm,secret_key,vector) VALUES(?,?,?,?,?)";
				pstmt = (PreparedStatement) dbc.dbConn.prepareStatement(sql);
				pstmt.setString(1, para1);
				pstmt.setString(2, para2);
				pstmt.setString(3, "des"); // 现在只有des算法
				pstmt.setString(4, key);
				pstmt.setString(5, vt);
				pstmt.executeUpdate();

				// 提交事务
				dbc.dbConn.commit();
				// 恢复原来的提交模式
				dbc.dbConn.setAutoCommit(autoCommit);
			} catch (SQLException e) { // 只要其中有一个sql执行错误，就应该回滚
				res = ServerMessage.ENCRYPTFAIL;
				try {
					// 回滚、取消前述操作
					dbc.dbConn.rollback();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
				e.printStackTrace();
			}
		}

		return res;
	}
}