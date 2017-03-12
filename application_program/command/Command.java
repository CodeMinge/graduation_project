package command;

import java.sql.*;

import message_center.ServerMessage;
import server.DatabaseConnection;
import server.KeyAndVector;

public abstract class Command {
	
	/*
	 * staticȷ��ֻ��һ����
	 * ���Ƕ���ÿ����Ҫ�����ı�����һ����
	 */
	protected static Object workerTbLock = new Object(); 
	
	protected KeyAndVector kav = new KeyAndVector(); // ��Կ����������
	
	public Command() {
		
	}
	
	public abstract String process(String para1, String para2, DatabaseConnection dbc);
	
	/**
	 * ȷ�����ݿ����Ƿ���ڱ�
	 * 
	 * @param table
	 *            ����
	 * @param dbc
	 * @return
	 */
	protected String tbExists(String table, DatabaseConnection dbc) {
		String res = ServerMessage.NOTABLE;

		String sql = "select * from sys.tables"; // ��ѯ���еı�

		PreparedStatement pstmt = null;
		try {
			pstmt = (PreparedStatement) dbc.dbConn.prepareStatement(sql);
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				String temp = rs.getString(1); // ȡ�������������ƶ�Ӧ
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
	 * ȷ�ϱ����Ƿ����ĳ��
	 * 
	 * @param table
	 *            ����
	 * @param property
	 *            ����
	 * @param dbc
	 * @return
	 */
	protected String propertyExists(String table, String property, DatabaseConnection dbc) {
		String res = ServerMessage.NOPROPERTY;

		// ���sql����Ľ��ֻ��һ��name�����������ϵ�������
		String sql = "select name from syscolumns where id = object_id('graduation_project.dbo." + table + "')"; // ��ѯ���е���

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
