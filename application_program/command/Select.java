package command;

import java.sql.*;
import java.util.LinkedList;

import message_center.ServerMessage;
import server.DatabaseConnection;
import server.Server;

public class Select extends Command {

	LinkedList<Messsge_of_Select> message_sel = new LinkedList<Messsge_of_Select>();

	public Select(String command) {
		super(command);
	}

	/*
	 * para1 -----列 para2 -----表
	 */
	public String process(String para1, String para2, DatabaseConnection dbc, String name) {
		String res = ServerMessage.SELECTSUCCESS;
		String str = "";

		int type = -1;
		String b_key = null;
		String b_vt = null;
		boolean exist = false;

		String sql = command;

		// 对表和列进行解析（不一定只有一个表、列） //先支持单表查询
		String[] col = para1.trim().split(",");
		String[] tb = para2.trim().split(",");

		PreparedStatement pstmt = null;
		ResultSet rs = null;
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
						if (!Server.userList.get(i).contain(para1)) {
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
			LinkedList<String> list = new LinkedList<String>();
			if (col.length == 1 && col[0].equals("*")) {
				for (int i = 0; i < tb.length; i++) { // 先支持单表查询
					String sql1 = "Select name from syscolumns where id =object_id('" + tb[i] + "')";

					pstmt = (PreparedStatement) dbc.dbConn.prepareStatement(sql1);
					rs = pstmt.executeQuery();
					while (rs.next()) {
						String temp = rs.getString(1);
						list.addFirst(temp);
						str += temp + " ";
					}
				}
				str += "&&";
			}

			col = new String[list.size()];
			for (int i = 0; i < col.length; i++)
				col[i] = list.get(i);

			for (int i = 0; i < tb.length; i++) { // 先支持单表查询
				for (int j = 0; j < col.length; j++) {
					exist = false;
					String sql1 = null;
					// 利用表和列查找加密信息
					if (type == 1) {
						// 超级用户自己加密的
						String sql2 = "select * from message_tb_super where tb_name = '" + tb[i] + "' and property = '"
								+ col[j] + "'";
						pstmt = (PreparedStatement) dbc.dbConn.prepareStatement(sql2);
						rs = pstmt.executeQuery();
						while (rs.next()) {
							exist = true;
						}
						if (exist) { // 是是超级用户加的密
							key = b_key;
							vt = b_vt;
						} else {// 不是是超级用户加的密
							// 查看是否是普通用户加的密
							sql1 = "SELECT * from [graduation_project].[dbo].[message_tb] where tb_name = '" + tb[i]
									+ "' and property = '" + col[j] + "'";
							pstmt = (PreparedStatement) dbc.dbConn.prepareStatement(sql1);
							ResultSet rs1 = pstmt.executeQuery();
							exist = false;
							while (rs1.next()) { // 确定其是否是敏感数据
								exist = true;
								break; // 这里只能有一行
							}

							if (!exist) {
								Messsge_of_Select mos = new Messsge_of_Select(tb[i], col[j], key, vt, exist);
								message_sel.addLast(mos);
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
									if (rs2.getString(1).equals(tb[i])) { // 该表与要解密的表相同
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
						sql1 = "SELECT * from [graduation_project].[dbo].[message_tb] where tb_name = '" + tb[i]
								+ "' and property = '" + col[j] + "'";
						pstmt = (PreparedStatement) dbc.dbConn.prepareStatement(sql1);
						rs = pstmt.executeQuery();
						exist = false;
						while (rs.next()) { // 确定其是否是敏感数据
							exist = true;
							break; // 这里只能有一行
						}
					}

					Messsge_of_Select mos = new Messsge_of_Select(tb[i], col[j], key, vt, exist);
					String sql3 = "select a.name 表名,b.name 字段名,c.name 字段类型,c.length 字段长度 "
							+ " from sysobjects a,syscolumns b,systypes c" + " where a.id=b.id and a.name='" + tb[i]
							+ "' and a.xtype='U' and b.xtype=c.xtype";

					String dataType = null;
					pstmt = (PreparedStatement) dbc.dbConn.prepareStatement(sql3);
					ResultSet rs3 = pstmt.executeQuery();
					while (rs3.next()) {
						if (rs3.getString(2).equals(col[j])) {
							dataType = rs3.getString(3);
							break;
						}
					}
					mos.dataType = dataType;
					message_sel.addLast(mos);
				}
			}

			pstmt = (PreparedStatement) dbc.dbConn.prepareStatement(sql);
			rs = pstmt.executeQuery();
			while (rs.next()) { // 确定其是否是敏感数据
				for (int i = 0; i < message_sel.size(); i++) {
					if (message_sel.get(i).isEncrypt) {
						String tempString = "";
						int tempInt = 0;
						String dataType = message_sel.get(i).dataType;
						// 执行加密操作,temp1是加密后的结果
						if (dataType.equals("char") || dataType.equals("varchar") || dataType.equals("nchar")
								|| dataType.equals("nvarchar")) {
							System.out.println(rs.getString(message_sel.get(i).col));
							String sql1 = "SELECT [graduation_project].[dbo].[Des_Decrypt]('"
									+ rs.getString(message_sel.get(i).col) + "', '" + message_sel.get(i).key + "', '"
									+ message_sel.get(i).vector + "')";
							pstmt = (PreparedStatement) dbc.dbConn.prepareStatement(sql1);
							ResultSet rs2 = pstmt.executeQuery();
							while (rs2.next()) {
								tempString = rs2.getString(1);
								break;
							}
							str += tempString + " ";
						} else if (dataType.equals("int")) {
							System.out.println(rs.getString(message_sel.get(i).col));
							String sql1 = "SELECT [graduation_project].[dbo].[INTDecrypt]("
									+ rs.getString(message_sel.get(i).col) + "," + message_sel.get(i).key + ")";
							pstmt = (PreparedStatement) dbc.dbConn.prepareStatement(sql1);
							ResultSet rs2 = pstmt.executeQuery();
							while (rs2.next()) {
								tempInt = rs2.getInt(1);
								break;
							}
							str += tempInt + " ";
						}
					} else {
						str += rs.getString(message_sel.get(i).col) + " ";
					}
				}
				str += "&&";
			}
			System.out.println(str);
			
			res += "||" + str;

			// 提交事务
			dbc.dbConn.commit();
			// 恢复原来的提交模式
			dbc.dbConn.setAutoCommit(autoCommit);
		} catch (SQLException e) {
			res = ServerMessage.SELECTFAIL;
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

	class Messsge_of_Select {
		String table;
		String col;
		String key;
		String vector;
		String dataType;
		boolean isEncrypt;

		Messsge_of_Select(String table, String col, String key, String vector, boolean isEncrypt) {
			this.table = table;
			this.col = col;
			this.key = key;
			this.vector = vector;
			this.isEncrypt = isEncrypt;
		}
	}
}