package command;

import java.sql.*;
import java.util.LinkedList;

import DBConnect.DBEncryptConnection;
import DBConnect.KeyDBConnection;
import key_manage.Key;
import key_manage.KeyManager;
import message_center.ServerMessage;

public class Select extends Command {

	LinkedList<Messsge_of_Select> message_sel = new LinkedList<Messsge_of_Select>();

	public Select(String command) {
		super(command);
	}

	/*
	 * para1 -----列 para2 -----表
	 */
	public String process(String para1, String para2, DBEncryptConnection dbec, KeyDBConnection kdbc, String name) {
		String res = ServerMessage.SELECTSUCCESS;
		String str = "";

		boolean exist = false;
		KeyManager km = new KeyManager();
		Key key = null;
		String dataType = null;

		String sql = command;

		// 对表和列进行解析（不一定只有一个表、列） //先支持单表查询
		String[] col = para1.trim().split(",");
		String[] tb = para2.trim().split(",");

		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			boolean autoCommit = dbec.dbConn.getAutoCommit();
			// 关闭自动提交功能
			dbec.dbConn.setAutoCommit(false);

			LinkedList<String> list = new LinkedList<String>();
			if (col.length == 1 && col[0].equals("*")) {
				for (int i = 0; i < tb.length; i++) { // 先支持单表查询
					String sql1 = "Select name from syscolumns where id =object_id('" + tb[i] + "')";

					pstmt = (PreparedStatement) dbec.dbConn.prepareStatement(sql1);
					rs = pstmt.executeQuery();
					while (rs.next()) {
						String temp = rs.getString(1);
						list.addFirst(temp);
					}
				}
			}

			col = new String[list.size()];
			for (int i = 0; i < col.length; i++) {
				col[i] = list.get(i);
				str += col[i] + " ";
			}
			str += "&";

			for (int i = 0; i < tb.length; i++) { // 先支持单表查询
				for (int j = 0; j < col.length; j++) {
					exist = false;
					String sql1 = null;

					// 判断这个表列是否被加密
					sql1 = "select key_name from [DBEncryption].[dbo].[encrypt_message] where tb_name = '" + tb[i]
							+ "' and col_name = '" + col[j] + "'";
					pstmt = (PreparedStatement) dbec.dbConn.prepareStatement(sql1);
					rs = pstmt.executeQuery();
					while (rs.next()) {
						exist = true;
						key = km.getKey(rs.getString(1), dbec, kdbc);
					}

					Messsge_of_Select mos = new Messsge_of_Select(tb[i], col[j], key, exist);
					
					sql1 = "select a.name 表名,b.name 字段名,c.name 字段类型,c.length 字段长度 "
							+ " from sysobjects a,syscolumns b,systypes c" + " where a.id=b.id and a.name='" + tb[i]
							+ "' and a.xtype='U' and b.xtype=c.xtype";

					pstmt = (PreparedStatement) dbec.dbConn.prepareStatement(sql1);
					rs = pstmt.executeQuery();
					while (rs.next()) {
						if (rs.getString(2).equals(col[j])) {
							dataType = rs.getString(3);
							break;
						}
					}
					mos.dataType = dataType;
					message_sel.addLast(mos);
				}
			}

			pstmt = (PreparedStatement) dbec.dbConn.prepareStatement(sql);
			rs = pstmt.executeQuery();
			while (rs.next()) { // 确定其是否是敏感数据
				for (int i = 0; i < message_sel.size(); i++) {
					message_sel.get(i).print();
					if (message_sel.get(i).isEncrypt) {
						String tempString = "";
						int tempInt = 0;
						dataType = message_sel.get(i).dataType;
						// 执行加密操作,temp1是加密后的结果
						if (dataType.equals("char") || dataType.equals("varchar") || dataType.equals("nchar")
								|| dataType.equals("nvarchar")) {
							System.out.println(rs.getString(message_sel.get(i).col));
							String sql1 = "SELECT [dbo].[DESDecrypt]('" + rs.getString(message_sel.get(i).col) + "', '"
									+ message_sel.get(i).key.getKeyData() + "', '" + message_sel.get(i).key.getVtData() + "')";
							pstmt = (PreparedStatement) dbec.dbConn.prepareStatement(sql1);
							ResultSet rs2 = pstmt.executeQuery();
							while (rs2.next()) {
								tempString = rs2.getString(1);
								break;
							}
							str += tempString + " ";
						} else if (dataType.equals("int")) {
							System.out.println(rs.getString(message_sel.get(i).col));
							String sql1 = "SELECT [dbo].[INTDecrypt](" + rs.getString(message_sel.get(i).col) + ","
									+ message_sel.get(i).key.getKeyData() + ")";
							pstmt = (PreparedStatement) dbec.dbConn.prepareStatement(sql1);
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
				str += "&";
			}
			System.out.println(str);

			res += "@" + str;

			// 提交事务
			dbec.dbConn.commit();
			// 恢复原来的提交模式
			dbec.dbConn.setAutoCommit(autoCommit);
		} catch (SQLException e) {
			res = ServerMessage.SELECTFAIL;
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

	class Messsge_of_Select {
		String table = null;
		String col = null;
		Key key = null;
		String dataType = null;
		boolean isEncrypt;

		Messsge_of_Select(String table, String col, Key key, boolean isEncrypt) {
			this.table = table;
			this.col = col;
			this.key = key;
			this.isEncrypt = isEncrypt;
		}
		
		public void print() {
			System.out.println(table + " " + col + " " + dataType + " " + isEncrypt);
		}
	}
}