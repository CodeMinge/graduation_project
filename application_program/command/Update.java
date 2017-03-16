package command;

import java.sql.*;
import java.util.*;

import message_center.ServerMessage;
import server.DatabaseConnection;

public class Update extends Command {
	// 存储列值对
	Map<String, String> col_value = new HashMap<String, String>();

	public Update(String command) {
		super(command);
	}

	/*
	 * para1 -----表 para2 -----列
	 */
	public String process(String para1, String para2, DatabaseConnection dbc) {
		String res = ServerMessage.UPDATESUCCESS;

		// 表只是一个，不需解析
		// 先对列进行解析，并将结果存储在Map中
		analysisCol(para2);

		synchronized (workerTbLock) {
			String sql = command;

			PreparedStatement pstmt = null;
			try {
				boolean autoCommit = dbc.dbConn.getAutoCommit();
				// 关闭自动提交功能
				dbc.dbConn.setAutoCommit(false);

				// 查看将修改的列是否为敏感属性
				// 采用Iterator遍历HashMap
				Iterator<String> it = col_value.keySet().iterator();
				while (it.hasNext()) {
					String col = (String) it.next(); // 列
					String value = col_value.get(col); // 值

					System.out.println("col:" + col); // 等位信息
					System.out.println("value:" + value); // 等位信息

					// 利用表和列在message_tb上查找加密信息
					String sql1 = "SELECT * from [graduation_project].[dbo].[message_tb] where tb_name = '" + para1
							+ "' and property = '" + col + "'";

					String key = ""; // 密码
					String vt = ""; // 向量

					pstmt = (PreparedStatement) dbc.dbConn.prepareStatement(sql1);
					ResultSet rs = pstmt.executeQuery();
					while (rs.next()) { // 取得解密的密钥和向量
						key = rs.getString("secret_key");
						vt = rs.getString("vector");
						break; // 这里只能有一行
					}

					if (!key.equals("") && !vt.equals("")) { // 是敏感属性
						sql1 = "SELECT [graduation_project].[dbo].[Des_Encrypt]('" + value + "', '" + key + "', '" + vt
								+ "')";
						pstmt = (PreparedStatement) dbc.dbConn.prepareStatement(sql1);
						rs = pstmt.executeQuery();
						while (rs.next()) {
							value = rs.getString(1);
							break;
						}
					}

					// 将更新的内容换到字符串里面
					sql = sql.replace(col_value.get(col), value);
				}

				pstmt = (PreparedStatement) dbc.dbConn.prepareStatement(sql);
				int i = pstmt.executeUpdate();
				if (i == 0)
					res = ServerMessage.UPDATEFAIL;

				// 提交事务
				dbc.dbConn.commit();
				// 恢复原来的提交模式
				dbc.dbConn.setAutoCommit(autoCommit);
			} catch (SQLException e) {
				res = ServerMessage.UPDATEFAIL;
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

	private void analysisCol(String para2) {
		// 先进行逗号分割
		String[] str_by_comma = para2.split(",");
		for (int i = 0; i < str_by_comma.length; i++) {
			// 再进行等号分割
			String[] str_by_equal = str_by_comma[i].split("=");
			col_value.put(str_by_equal[0].trim(), str_by_equal[1].trim().substring(1, str_by_equal[1].length() - 2));
		}
	}
}