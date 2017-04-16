package command;

import java.sql.*;
import java.util.*;

import message_center.ServerMessage;
import server.DatabaseConnection;
import server.Server;

public class Update extends Command {
	// 存储列值对
	Map<String, String> col_value = new HashMap<String, String>();

	public Update(String command) {
		super(command);
	}

	/*
	 * para1 -----表 para2 -----列
	 */
	public String process(String para1, String para2, DatabaseConnection dbc, String name) {
		String res = ServerMessage.UPDATESUCCESS;

		int type = -1;
		String key = null;
		String vt = null;
		// 首先查表确定自己的用户类型（超级用户或者普通用户）
		// 确定这个将要加密的表是否在自己的管理列表中
		for (int i = 0; i < Server.userList.size(); i++) {
			if (Server.userList.get(i).getName().equals(name)) {
				type = Server.userList.get(i).getType();
				if (type == 1) { // 超级用户
					// 再次遍历所有用户，查看这个表是在哪个用户的管理结构中取得这个用户的密钥和向量

				} else { // 普通用户
					key = Server.userList.get(i).getKey();
					vt = Server.userList.get(i).getVector();
					// System.out.println(key + " " + vt);
					if (!Server.userList.get(i).contain(para1)) {
						return ServerMessage.UPDATEFAIL;
					}
				}
			}
		}

		// 表只是一个，不需解析
		// 先对列进行解析，并将结果存储在Map中
		analysisCol(para2);

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

//				System.out.println("col:" + col); // 等位信息
//				System.out.println("value:" + value); // 等位信息

				// 利用表和列在message_tb上查找加密信息
				String sql1 = "SELECT * from [graduation_project].[dbo].[message_tb] where tb_name = '" + para1
						+ "' and property = '" + col + "'";

				pstmt = (PreparedStatement) dbc.dbConn.prepareStatement(sql1);
				ResultSet rs = pstmt.executeQuery();
				boolean exist = false;
				while (rs.next()) { //确定其是否是敏感数据
					exist = true;
					break; // 这里只能有一行
				}

				if (exist) { // 是敏感属性
					sql1 = "select a.name 表名,b.name 字段名,c.name 字段类型,c.length 字段长度 "
							+ " from sysobjects a,syscolumns b,systypes c" + " where a.id=b.id and a.name='" + para1
							+ "' and a.xtype='U' and b.xtype=c.xtype";
					
					String dataType = null;
					pstmt = (PreparedStatement) dbc.dbConn.prepareStatement(sql1);
					rs = pstmt.executeQuery();
					while (rs.next()) {
						if (rs.getString(2).equals(col)) {
							dataType = rs.getString(3);
							break;
						}
					}
					
					String tempString = "";
					int tempInt = 0;
					// 执行加密操作,temp1是加密后的结果
					if (dataType.equals("char") || dataType.equals("varchar") || dataType.equals("nchar")
							|| dataType.equals("nvarchar")) {
						sql1 = "SELECT [graduation_project].[dbo].[Des_Encrypt]('" + value + "', '" + key + "', '" + vt + "')";
						pstmt = (PreparedStatement) dbc.dbConn.prepareStatement(sql1);
						ResultSet rs2 = pstmt.executeQuery();
						while (rs2.next()) {
							tempString = rs2.getString(1);
							break;
						}
						// 将更新的内容换到字符串里面
						sql = sql.replace(col_value.get(col), tempString);
					} else if(dataType.equals("int")) {
						sql1 = "SELECT [graduation_project].[dbo].[INTEncrypt](" + value + "," + key + ")";
						pstmt = (PreparedStatement) dbc.dbConn.prepareStatement(sql1);
						ResultSet rs2 = pstmt.executeQuery();
						while (rs2.next()) {
							tempInt = rs2.getInt(1);
							break;
						}
						// 将更新的内容换到字符串里面
						sql = sql.replace(col_value.get(col), tempInt + "");
					}
				}
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

		return res;
	}

	private void analysisCol(String para2) {
		// 先进行逗号分割
		String[] str_by_comma = para2.split(",");
		for (int i = 0; i < str_by_comma.length; i++) {
			// 再进行等号分割
			String[] str_by_equal = str_by_comma[i].split("=");
			if(str_by_equal[1].contains("'")) {
				col_value.put(str_by_equal[0].trim(), str_by_equal[1].trim().substring(1, str_by_equal[1].length() - 2));
			}
			else {
				col_value.put(str_by_equal[0].trim(), str_by_equal[1].trim());
			}
		}
	}
}