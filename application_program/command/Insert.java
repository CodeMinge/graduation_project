package command;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import message_center.ServerMessage;
import server.DatabaseConnection;
import server.Server;

public class Insert extends Command {

	// 存储列值对
	Map<String, String> col_value = new HashMap<String, String>();

	public Insert(String command) {
		super(command);
	}

	/**
	 * para1 -----表信息 para2 -----无作为
	 */
	public String process(String para1, String para2, DatabaseConnection dbc, String name) {
		String res = ServerMessage.INSERTSUCCESS;

		String tb = getTb(para1);
		getVal(dbc, tb, command);

		int type = -1;
		String b_key = null;
		String b_vt = null;
		boolean exist = false;

		String sql = command;

		PreparedStatement pstmt = null;
		try {
			boolean autoCommit = dbc.dbConn.getAutoCommit();
			// 关闭自动提交功能
			dbc.dbConn.setAutoCommit(false);

			// 首先查表确定自己的用户类型（超级用户或者普通用户）
			for (int i = 0; i < Server.userList.size(); i++) {
				if (Server.userList.get(i).getName().equals(name)) {
					type = Server.userList.get(i).getType();
					if (type == 1) { // 超级用户
						b_key = Server.userList.get(i).getKey();
						b_vt = Server.userList.get(i).getVector();
					} else { // 普通用户
						if (!Server.userList.get(i).contain(tb)) {
							return ServerMessage.UPDATEFAIL;
						}
						b_key = Server.userList.get(i).getKey();
						b_vt = Server.userList.get(i).getVector();
					}
					break;
				}
			}

			String key = b_key;
			String vt = b_vt;

			// 查看将修改的列是否为敏感属性
			// 采用Iterator遍历HashMap
			Iterator<String> it = col_value.keySet().iterator();
			while (it.hasNext()) {
				exist = false;
				String col = (String) it.next(); // 列
				String value = col_value.get(col); // 值

				System.out.println("col:" + col); // 等位信息
				System.out.println("value:" + value); // 等位信息

				String sql1 = null;
				// 利用表和列查找加密信息
				if (type == 1) {
					// 超级用户自己加密的
					String sql2 = "select * from message_tb_super where tb_name = '" + tb + "' and property = '"
							+ col + "'";
					pstmt = (PreparedStatement) dbc.dbConn.prepareStatement(sql2);
					ResultSet rs = pstmt.executeQuery();
					while (rs.next()) {
						exist = true;
					}
					if (exist) { // 是是超级用户加的密
						key = b_key;
						vt = b_vt;
					} else {// 不是是超级用户加的密
						//查看是否是普通用户加的密
						sql1 = "SELECT * from [graduation_project].[dbo].[message_tb] where tb_name = '" + tb
								+ "' and property = '" + col + "'";
						pstmt = (PreparedStatement) dbc.dbConn.prepareStatement(sql1);
						ResultSet rs1 = pstmt.executeQuery();
						exist = false;
						while (rs1.next()) { // 确定其是否是敏感数据
							exist = true;
							break; // 这里只能有一行
						}
						
						if(!exist) {
							continue;
						}
						
						key = null;
						vt = null;
						sql2 = "select * from user_kv"; // 查看所有用户
						pstmt = (PreparedStatement) dbc.dbConn.prepareStatement(sql2);
						rs = pstmt.executeQuery();
						while (rs.next()) {
							sql2 = "select table_name from " + rs.getString(1); // 查看用户所管理的表
							pstmt = (PreparedStatement) dbc.dbConn.prepareStatement(sql2);
							ResultSet rs2 = pstmt.executeQuery();
							while (rs2.next()) {
								if (rs2.getString(1).equals(tb)) { // 该表与要解密的表相同
									int k1 = rs.getInt(2);
									int k2 = rs.getInt(3);
									int v1 = rs.getInt(4);
									int v2 = rs.getInt(5);

									key = k1 + "" + k2;
									vt = v1 + "" + v2;
									break;
								}
							}

							if (key != null && vt != null) {
								break;
							}
						}
					}
				} else {
					sql1 = "SELECT * from [graduation_project].[dbo].[message_tb] where tb_name = '" + tb
							+ "' and property = '" + col + "'";
					pstmt = (PreparedStatement) dbc.dbConn.prepareStatement(sql1);
					ResultSet rs = pstmt.executeQuery();
					exist = false;
					while (rs.next()) { // 确定其是否是敏感数据
						exist = true;
						break; // 这里只能有一行
					}
				}

				if (exist) { // 是敏感属性
					sql1 = "select a.name 表名,b.name 字段名,c.name 字段类型,c.length 字段长度 "
							+ " from sysobjects a,syscolumns b,systypes c" + " where a.id=b.id and a.name='" + tb
							+ "' and a.xtype='U' and b.xtype=c.xtype";

					String dataType = null;
					pstmt = (PreparedStatement) dbc.dbConn.prepareStatement(sql1);
					ResultSet rs = pstmt.executeQuery();
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
						String target = value.substring(1, value.length() - 1);
						sql1 = "SELECT [graduation_project].[dbo].[Des_Encrypt]('" + target + "', '" + key + "', '" + vt
								+ "')";
						pstmt = (PreparedStatement) dbc.dbConn.prepareStatement(sql1);
						ResultSet rs2 = pstmt.executeQuery();
						while (rs2.next()) {
							tempString = rs2.getString(1);
							break;
						}
						tempString = "'" + tempString + "'";
						// 将更新的内容换到字符串里面
						sql = sql.replace(col_value.get(col), tempString);
					} else if (dataType.equals("int")) {
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
				res = ServerMessage.INSERTFAIL;

			// 提交事务
			dbc.dbConn.commit();
			// 恢复原来的提交模式
			dbc.dbConn.setAutoCommit(autoCommit);
		} catch (SQLException e) {
			res = ServerMessage.INSERTFAIL;
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

	// 获得表
	private String getTb(String target) {
		String[] res = target.split(" ");
		return res[0].trim();
	}

	// 获得值
	private void getVal(DatabaseConnection dbc, String table, String target) {
		int valEnd = 0;
		int valStart = 0;
		int colEnd = 0; // 不一定存在
		int colStart = 0; // 不一定存在

		valEnd = target.lastIndexOf(")");
		valStart = target.lastIndexOf("(");

		colStart = target.indexOf("(");

		if (colStart == valStart) { // INSERT INTO 表名称 VALUES (值1,值2,....)
			String[] val = target.substring(valStart + 1, valEnd).split(",");
			int i = val.length - 1;

			// 取得所有的列，插入到hashMap中
			// 这个sql命令的结果只有一列name，而列名从上到下排列
			String sql = "select name from syscolumns where id = object_id('graduation_project.dbo." + table + "')"; // 查询所有的列

			PreparedStatement pstmt = null;
			try {
				pstmt = (PreparedStatement) dbc.dbConn.prepareStatement(sql);
				ResultSet rs = pstmt.executeQuery();
				while (rs.next()) {
					String temp = rs.getString(1); // 列名
					col_value.put(temp, val[i].trim());
					i--;
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}

		} else { // INSERT INTO table_name (列1, 列2,...) VALUES (值1, 值2,....)
			colEnd = target.indexOf(")");

			// 取得对应的列，插入到hashMap中
			String[] col = target.substring(colStart, colEnd).split(",");
			String[] val = target.substring(valStart, valEnd).split(",");

			for (int i = 0; i < col.length; i++) {
				col_value.put(col[i], val[i].trim());
			}
		}
	}
}
