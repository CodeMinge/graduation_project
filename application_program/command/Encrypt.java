package command;

import java.sql.*;

import message_center.ServerMessage;
import server.DatabaseConnection;
import server.Server;

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
	public String process(String para1, String para2, DatabaseConnection dbc, String name) {
		// String res = ServerMessage.NULL;
		// 确保表和列存在//这个步骤可以交给数据库来做
		// res = tbExists(para1, dbc);
		// if (res.equals(ServerMessage.NOTABLE))
		// return res;
		//
		// ServerMessage.ServerMessageOutput(ServerMessage.EXISTTABLE); // 定位信息
		//
		// res = propertyExists(para1, para2, dbc);
		// if (res.equals(ServerMessage.NOPROPERTY))
		// return res;

		// ServerMessage.ServerMessageOutput(ServerMessage.EXISTPROPERTY); //
		// 定位信息

		String res = ServerMessage.ENCRYPTSUCCESS;

		int type = -1;
		String key = null;
		String vt = null;
		// 首先查表确定自己的用户类型（超级用户或者普通用户）
		for (int i = 0; i < Server.userList.size(); i++) {
			if (Server.userList.get(i).getName().equals(name)) {
				type = Server.userList.get(i).getType();
				if (type == 1) { // 超级用户
					// 再次遍历所有用户，查看这个表是在哪个用户的管理结构中取得这个用户的密钥和向量

				} else { // 普通用户
					key = Server.userList.get(i).getKey();
					vt = Server.userList.get(i).getVector();
//					System.out.println(key + " " + vt);
				}
			}
		}

		// 取得要加密的数据的数据类型，来选择加密算法
		String sql = "select a.name 表名,b.name 字段名,c.name 字段类型,c.length 字段长度 "
				+ " from sysobjects a,syscolumns b,systypes c" + " where a.id=b.id and a.name='" + para1
				+ "' and a.xtype='U' and b.xtype=c.xtype";

		PreparedStatement pstmt = null;
		try {

			boolean autoCommit = dbc.dbConn.getAutoCommit();
			// 关闭自动提交功能
			dbc.dbConn.setAutoCommit(false);

			String dataType = null;
			pstmt = (PreparedStatement) dbc.dbConn.prepareStatement(sql);
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				if (rs.getString(2).equals(para2)) {
					dataType = rs.getString(3);
					break;
				}
			}

			// 将所有数据取出加密
			sql = "SELECT * from [graduation_project].[dbo].[" + para1 + "]";
			pstmt = (PreparedStatement) dbc.dbConn.prepareStatement(sql);
			rs = pstmt.executeQuery();
			while (rs.next()) { // 不停地进行加密
				// 取得加密数据
//				String temp = rs.getString(1); // 这里，我的表必须是以第一列为主键，而且不能对第一列进行加密
				String target = rs.getString(para2); // 这是将要加密的数据
//				System.out.println(temp + " " + target);

				String tempString = "";
				int tempInt = 0;
				// 执行加密操作,temp1是加密后的结果
				if (dataType.equals("char") || dataType.equals("varchar") || dataType.equals("nchar")
						|| dataType.equals("nvarchar")) {
					sql = "SELECT [graduation_project].[dbo].[Des_Encrypt]('" + target + "', '" + key + "', '" + vt + "')";
					pstmt = (PreparedStatement) dbc.dbConn.prepareStatement(sql);
					ResultSet rs2 = pstmt.executeQuery();
					while (rs2.next()) {
						tempString = rs2.getString(1);
						break;
					}
//					System.out.println(target + " " + tempString);
					
					// 更新表，将加密后的内容更新到表中
					sql = "UPDATE [graduation_project].[dbo].[" + para1 + "] SET " + para2 + " = '" + tempString + "' WHERE "
							+ para2 + " = '" + target + "'";
					pstmt = (PreparedStatement) dbc.dbConn.prepareStatement(sql);
					int i = pstmt.executeUpdate();
					if (i == 0) { // 检测加密情况
						res = ServerMessage.ENCRYPTFAIL;
					}
				} else if(dataType.equals("int")) {
					sql = "SELECT [graduation_project].[dbo].[INTEncrypt](" + target + "," + key + ")";
					pstmt = (PreparedStatement) dbc.dbConn.prepareStatement(sql);
					ResultSet rs2 = pstmt.executeQuery();
					while (rs2.next()) {
						tempInt = rs2.getInt(1);
						break;
					}
					System.out.println(target + " " + tempInt);
					
					// 更新表，将加密后的内容更新到表中
					sql = "UPDATE [graduation_project].[dbo].[" + para1 + "] SET " + para2 + " = " + tempInt + " WHERE "
							+ para2 + " = " + target + "";
					pstmt = (PreparedStatement) dbc.dbConn.prepareStatement(sql);
					int i = pstmt.executeUpdate();
					if (i == 0) { // 检测加密情况
						res = ServerMessage.ENCRYPTFAIL;
					}
				}
			}
			// 记录加密信息
			sql = "insert into message_tb(tb_name,property) VALUES(?,?)";
			pstmt = (PreparedStatement) dbc.dbConn.prepareStatement(sql);
			pstmt.setString(1, para1);
			pstmt.setString(2, para2);
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

		return res;
	}
}