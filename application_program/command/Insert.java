package command;

import java.sql.*;
import java.util.*;

import DBConnect.DBEncryptConnection;
import DBConnect.KeyDBConnection;
import key_manage.Key;
import key_manage.KeyManager;
import message_center.ServerMessage;

public class Insert extends Command {

	// 存储列值对
	Map<String, String> col_value = new HashMap<String, String>();

	public Insert(String command) {
		super(command);
	}

	public String process(String para1, String para2, DBEncryptConnection dbec, KeyDBConnection kdbc, String name) {
		String res = ServerMessage.INSERTSUCCESS;

		String tb = getTb(para1);
		getVal(dbec, tb, command);

		boolean exist = false;
		String sql = command;

		KeyManager km = new KeyManager();
		Key key = null;
		String dataType = null;

		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {
			boolean autoCommit = dbec.dbConn.getAutoCommit();
			// 关闭自动提交功能
			dbec.dbConn.setAutoCommit(false);

			// 查看将修改的列是否为敏感属性
			// 采用Iterator遍历HashMap
			Iterator<String> it = col_value.keySet().iterator();
			while (it.hasNext()) {
				exist = false;
				String col = (String) it.next(); // 列
				String value = col_value.get(col); // 值

				System.out.println("col:" + col); // 等位信息
				System.out.println("value:" + value); // 等位信息

				// 判断这个表列是否被加密
				String sql1 = "select key_name from [DBEncryption].[dbo].[encrypt_message] where tb_name = '" + tb + "' and col_name = '"
						+ col + "'";
				pstmt = (PreparedStatement) dbec.dbConn.prepareStatement(sql1);
				rs = pstmt.executeQuery();
				while (rs.next()) {
					exist = true;
					key = km.getKey(rs.getString(1), dbec, kdbc);
				}

				if (exist) { // 是敏感属性
					sql1 = "select a.name 表名,b.name 字段名,c.name 字段类型,c.length 字段长度 "
							+ " from sysobjects a,syscolumns b,systypes c" + " where a.id=b.id and a.name='" + tb
							+ "' and a.xtype='U' and b.xtype=c.xtype";

					pstmt = (PreparedStatement) dbec.dbConn.prepareStatement(sql1);
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
						String target = value.substring(1, value.length() - 1);
						sql1 = "SELECT [dbo].[DESEncrypt]('" + target + "', '" + key.getKeyData() + "', '"
								+ key.getVtData() + "')";
						pstmt = (PreparedStatement) dbec.dbConn.prepareStatement(sql1);
						ResultSet rs2 = pstmt.executeQuery();
						while (rs2.next()) {
							tempString = rs2.getString(1);
							break;
						}
						tempString = "'" + tempString + "'";
						// 将更新的内容换到字符串里面
						sql = sql.replace(col_value.get(col), tempString);
					} else if (dataType.equals("int")) {
						sql1 = "SELECT [dbo].[INTEncrypt](" + value + "," + key.getKeyData() + ")";
						pstmt = (PreparedStatement) dbec.dbConn.prepareStatement(sql1);
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

			pstmt = (PreparedStatement) dbec.dbConn.prepareStatement(sql);
			int i = pstmt.executeUpdate();
			if (i == 0)
				res = ServerMessage.INSERTFAIL;

			// 提交事务
			dbec.dbConn.commit();
			// 恢复原来的提交模式
			dbec.dbConn.setAutoCommit(autoCommit);
		} catch (SQLException e) {
			res = ServerMessage.INSERTFAIL;
			try {
				// 回滚、取消前述操作
				dbec.dbConn.rollback();
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
	private void getVal(DBEncryptConnection dbec, String table, String target) {
		int valEnd = 0;
		int valStart = 0;
		int colEnd = 0; // 不一定存在
		int colStart = 0; // 不一定存在

		valEnd = target.lastIndexOf(")");
		valStart = target.lastIndexOf("(");

		colStart = target.indexOf("(");

		if (colStart == valStart) { // INSERT INTO 表名称 VALUES (值1,值2,....)
//			String[] val = target.substring(valStart + 1, valEnd).split(",");
//			int i = val.length - 1;
//
//			// 取得所有的列，插入到hashMap中
//			// 这个sql命令的结果只有一列name，而列名从上到下排列
//			String sql = "select name from syscolumns where id = object_id('" + table + "')"; // 查询所有的列
//
//			PreparedStatement pstmt = null;
//			try {
//				pstmt = (PreparedStatement) dbec.dbConn.prepareStatement(sql);
//				ResultSet rs = pstmt.executeQuery();
//				while (rs.next()) {
//					String temp = rs.getString(1); // 列名
//					col_value.put(temp, val[i].trim());
//					i--;
//				}
//			} catch (SQLException e) {
//				e.printStackTrace();
//			}
		} else { // INSERT INTO table_name (列1, 列2,...) VALUES (值1, 值2,....)
			colEnd = target.indexOf(")");

			// 取得对应的列，插入到hashMap中
			String[] col = target.substring(colStart + 1, colEnd).split(",");
			String[] val = target.substring(valStart + 1, valEnd).split(",");

			for (int i = 0; i < col.length; i++) {
				col_value.put(col[i].trim(), val[i].trim());
			}
		}
	}
}