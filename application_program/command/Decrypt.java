package command;

import java.sql.*;

import DBConnect.DBEncryptConnection;
import DBConnect.KeyDBConnection;
import key_manage.Key;
import key_manage.KeyManager;
import message_center.ServerMessage;

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
	public String process(String para1, String para2, DBEncryptConnection dbec, KeyDBConnection kdbc, String name) {
		String res = ServerMessage.DECRYPTSUCCESS;
		String[] commandArr = command.split(" ");

		KeyManager km = new KeyManager();
		Key key = km.getKey(commandArr[3], dbec, kdbc);

		// 取得要加密的数据的数据类型，来选择加密算法
		String sql = "select a.name 表名,b.name 字段名,c.name 字段类型,c.length 字段长度 "
				+ " from sysobjects a,syscolumns b,systypes c" + " where a.id=b.id and a.name='" + para1
				+ "' and a.xtype='U' and b.xtype=c.xtype";

		PreparedStatement pstmt = null;
		try {
			boolean autoCommit = dbec.dbConn.getAutoCommit();
			// 关闭自动提交功能
			dbec.dbConn.setAutoCommit(false);

			String dataType = null;
			pstmt = (PreparedStatement) dbec.dbConn.prepareStatement(sql);
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				if (rs.getString(2).equals(para2)) {
					dataType = rs.getString(3);
					break;
				}
			}

			// 将所有数据取出加密
			sql = "SELECT * from [" + para1 + "]";
			pstmt = (PreparedStatement) dbec.dbConn.prepareStatement(sql);
			rs = pstmt.executeQuery();
			while (rs.next()) { // 不停地进行加密
				// 取得加密数据
				// String temp = rs.getString(1); //
				// 这里，我的表必须是以第一列为主键，而且不能对第一列进行加密
				String target = rs.getString(para2); // 这是将要加密的数据
				// System.out.println(temp + " " + target);

				String tempString = "";
				int tempInt = 0;
				// 执行加密操作,temp1是加密后的结果
				if (dataType.equals("char") || dataType.equals("varchar") || dataType.equals("nchar")
						|| dataType.equals("nvarchar")) {
					sql = "SELECT [dbo].[DESDecrypt]('" + target + "', '" + key.getKeyData() + "', '" + key.getVtData()
							+ "')";
					pstmt = (PreparedStatement) dbec.dbConn.prepareStatement(sql);
					ResultSet rs2 = pstmt.executeQuery();
					while (rs2.next()) {
						tempString = rs2.getString(1);
						break;
					}
					// System.out.println(target + " " + tempString);

					// 更新表，将加密后的内容更新到表中
					sql = "UPDATE [" + para1 + "] SET " + para2 + " = '" + tempString + "' WHERE " + para2 + " = '"
							+ target + "'";
					pstmt = (PreparedStatement) dbec.dbConn.prepareStatement(sql);
					int i = pstmt.executeUpdate();
					if (i == 0) { // 检测加密情况
						res = ServerMessage.ENCRYPTFAIL;
					}
				} else if (dataType.equals("int")) {
					sql = "SELECT [dbo].[INTDecrypt](" + target + "," + key.getKeyData() + ")";
					pstmt = (PreparedStatement) dbec.dbConn.prepareStatement(sql);
					ResultSet rs2 = pstmt.executeQuery();
					while (rs2.next()) {
						tempInt = rs2.getInt(1);
						break;
					}
					// System.out.println(target + " " + tempInt);

					// 更新表，将加密后的内容更新到表中
					sql = "UPDATE [" + para1 + "] SET " + para2 + " = " + tempInt + " WHERE " + para2 + " = " + target
							+ "";
					pstmt = (PreparedStatement) dbec.dbConn.prepareStatement(sql);
					int i = pstmt.executeUpdate();
					if (i == 0) { // 检测加密情况
						res = ServerMessage.ENCRYPTFAIL;
					}
				}
			}

			// 记录加密信息
			sql = "delete from [encrypt_message] where username = '" + name + "' and tb_name = '" + para1
					+ "' and col_name = '" + para2 + "' and key_name = '" + commandArr[3] + "'";
			pstmt = (PreparedStatement) dbec.dbConn.prepareStatement(sql);
			pstmt.executeUpdate();

			// 提交事务
			dbec.dbConn.commit();
			// 恢复原来的提交模式
			dbec.dbConn.setAutoCommit(autoCommit);
		} catch (SQLException e) { // 只要其中有一个sql执行错误，就应该回滚
			res = ServerMessage.DECRYPTFAIL;
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
}