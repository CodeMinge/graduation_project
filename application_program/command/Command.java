package command;

import java.sql.*;

import message_center.ServerMessage;
import server.DatabaseConnection;
import server.KeyAndVector;

public abstract class Command {
	
	protected KeyAndVector kav = new KeyAndVector(); // 密钥向量生成器
	
	protected String command = null;
	
	public Command(String command) {
		this.command = command;
	}
	
	public abstract String process(String para1, String para2, DatabaseConnection dbc, String name);
	
	/**
	 * 确认数据库中是否存在表
	 * 
	 * @param table
	 *            表名
	 * @param dbc
	 * @return
	 */
	protected String tbExists(String table, DatabaseConnection dbc) {
		String res = ServerMessage.NOTABLE;

		String sql = "select * from sys.tables"; // 查询所有的表

		PreparedStatement pstmt = null;
		try {
			pstmt = (PreparedStatement) dbc.dbConn.prepareStatement(sql);
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				String temp = rs.getString(1); // 取出的列与表的名称对应
				if (table.equals(temp)) {
					System.out.println(table + " " + temp);
					res = ServerMessage.EXISTTABLE;
					break;
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return res;
	}

	/**
	 * 确认表中是否存在某列
	 * 
	 * @param table
	 *            表名
	 * @param property
	 *            列名
	 * @param dbc
	 * @return
	 */
	protected String propertyExists(String table, String property, DatabaseConnection dbc) {
		String res = ServerMessage.NOPROPERTY;

		// 这个sql命令的结果只有一列name，而列名从上到下排列
		String sql = "select name from syscolumns where id = object_id('graduation_project.dbo." + table + "')"; // 查询所有的列

		PreparedStatement pstmt = null;
		try {
			pstmt = (PreparedStatement) dbc.dbConn.prepareStatement(sql);
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				String temp = rs.getString(1);
				if (property.equals(temp)) {
					System.out.println(property + " " + temp);
					res = ServerMessage.EXISTPROPERTY;
					break;
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return res;
	}
}
