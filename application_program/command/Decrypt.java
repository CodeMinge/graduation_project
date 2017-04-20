package command;

import java.sql.*;

import message_center.ServerMessage;
import server.DatabaseConnection;
import server.Server;

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
		String res = ServerMessage.DECRYPTSUCCESS;

		int type = -1;
		String key = null;
		String vt = null;
		boolean exist = false;
		boolean sup_en = false;

		// 取得要加密的数据的数据类型，来选择解密算法
		String sql = "select a.name 表名,b.name 字段名,c.name 字段类型,c.length 字段长度 "
				+ " from sysobjects a,syscolumns b,systypes c" + " where a.id=b.id and a.name='" + para1
				+ "' and a.xtype='U' and b.xtype=c.xtype";

		PreparedStatement pstmt = null;
		try {
			boolean autoCommit = dbc.dbConn.getAutoCommit();
			// 关闭自动提交功能
			dbc.dbConn.setAutoCommit(false);
			
			// 首先查表确定自己的用户类型（超级用户或者普通用户）
			// 确定这个将要解密的表是否在自己的管理列表中
			for (int i = 0; i < Server.userList.size(); i++) {
				if (Server.userList.get(i).getName().equals(name)) {
					type = Server.userList.get(i).getType();
					if (type == 1) { // 超级用户
						//该列可能是超级用户自己加密的
						String sql2 = "select * from message_tb_super where tb_name = '" + para1 + "' and property = '" + para2 + "'";
						pstmt = (PreparedStatement) dbc.dbConn.prepareStatement(sql2);
						ResultSet rs = pstmt.executeQuery();
						while (rs.next()) {
							sup_en = true;
							exist = true;
						}
						if(exist) { // 是超级用户加的密
							key = Server.userList.get(i).getKey();
							vt = Server.userList.get(i).getVector();
						} else {
							sql2 = "select * from user_kv"; //查看所有用户
							pstmt = (PreparedStatement) dbc.dbConn.prepareStatement(sql2);
							rs = pstmt.executeQuery();
							while (rs.next()) {
								sql2 = "select table_name from " + rs.getString(1); // 查看用户所管理的表
								pstmt = (PreparedStatement) dbc.dbConn.prepareStatement(sql2);
								ResultSet rs2 = pstmt.executeQuery();
								while(rs2.next()) {
									if(rs2.getString(1).equals(para1)) { //该表与要解密的表相同
										int k1 = rs.getInt(2);
										int k2 = rs.getInt(3);
										int v1 = rs.getInt(4);
										int v2 = rs.getInt(5);

										key = k1 + "" + k2;
										vt = v1 + "" + v2;
										break;
									}
								}
								
								if(key != null && vt != null) {
									break;
								}
							}
						}
					} else { // 普通用户
						if (!Server.userList.get(i).contain(para1)) {
							return ServerMessage.DECRYPTFAIL;
						}
						//该列是普通用户加密的
						String sql2 = "select * from message_tb where tb_name = '" + para1 + "' and property = '" + para2 + "'";
						pstmt = (PreparedStatement) dbc.dbConn.prepareStatement(sql2);
						ResultSet rs = pstmt.executeQuery();
						while (rs.next()) {
							exist = true;
						}
						if(exist) {
							key = Server.userList.get(i).getKey();
							vt = Server.userList.get(i).getVector();
						} else {
							return ServerMessage.DECRYPTFAIL;
						}
//						System.out.println(key + " " + vt);
					}
					break;
				}
			}

			String dataType = null;
			pstmt = (PreparedStatement) dbc.dbConn.prepareStatement(sql);
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				if (rs.getString(2).equals(para2)) {
					dataType = rs.getString(3);
					break;
				}
			}

			// 将所有数据取出解密
			sql = "SELECT * from [graduation_project].[dbo].[" + para1 + "]";
			pstmt = (PreparedStatement) dbc.dbConn.prepareStatement(sql);
			rs = pstmt.executeQuery();
			while (rs.next()) { // 不停地进行解密
				// 取得解密数据
				// String temp = rs.getString(1); //
				// 这里，我的表必须是以第一列为主键，而且不能对第一列进行解密
				String target = rs.getString(para2); // 这是将要解密的数据
				// System.out.println(temp + " " + target);

				String tempString = "";
				int tempInt = 0;
				// 执行解密操作,temp是解密后的结果
				if (dataType.equals("char") || dataType.equals("varchar") || dataType.equals("nchar")
						|| dataType.equals("nvarchar")) {
					sql = "SELECT [graduation_project].[dbo].[Des_Decrypt]('" + target + "', '" + key + "', '" + vt
							+ "')";
					pstmt = (PreparedStatement) dbc.dbConn.prepareStatement(sql);
					ResultSet rs2 = pstmt.executeQuery();
					while (rs2.next()) {
						tempString = rs2.getString(1);
						break;
					}
//					System.out.println(target + " " + tempString);

					// 更新表，将解密后的内容更新到表中
					sql = "UPDATE [graduation_project].[dbo].[" + para1 + "] SET " + para2 + " = '" + tempString
							+ "' WHERE " + para2 + " = '" + target + "'";
					pstmt = (PreparedStatement) dbc.dbConn.prepareStatement(sql);
					int i = pstmt.executeUpdate();
					if (i == 0) { // 检测加密情况
						res = ServerMessage.DECRYPTFAIL;
					}
				} else if (dataType.equals("int")) {
					sql = "SELECT [graduation_project].[dbo].[INTDecrypt](" + target + "," + key + ")";
					pstmt = (PreparedStatement) dbc.dbConn.prepareStatement(sql);
					ResultSet rs2 = pstmt.executeQuery();
					while (rs2.next()) {
						tempInt = rs2.getInt(1);
						break;
					}
//					System.out.println(target + " " + tempInt);

					// 更新表，将解密后的内容更新到表中
					sql = "UPDATE [graduation_project].[dbo].[" + para1 + "] SET " + para2 + " = " + tempInt + " WHERE "
							+ para2 + " = " + target + "";
					pstmt = (PreparedStatement) dbc.dbConn.prepareStatement(sql);
					int i = pstmt.executeUpdate();
					if (i == 0) { // 检测加密情况
						res = ServerMessage.DECRYPTFAIL;
					}
				}
			}

			// 记录解密信息，实质是删除原来的的加密信息
			if(sup_en)
				sql = "DELETE FROM message_tb_super WHERE tb_name = '" + para1 + "' and property = '" + para2 + "'";
			else
				sql = "DELETE FROM message_tb WHERE tb_name = '" + para1 + "' and property = '" + para2 + "'";
			pstmt = (PreparedStatement) dbc.dbConn.prepareStatement(sql);
			int i = pstmt.executeUpdate();
			if (i == 0) {
				res = ServerMessage.DECRYPTFAIL;
			}
			
			// 提交事务
			dbc.dbConn.commit();
			// 恢复原来的提交模式
			dbc.dbConn.setAutoCommit(autoCommit);
		} catch (SQLException e) { // 只要其中有一个sql执行错误，就应该回滚
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