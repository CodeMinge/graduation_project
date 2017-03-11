package server;

import java.io.PrintWriter;
import java.sql.*;

import message_center.ServerMessage;

/**
 * ��֧�ּ����򵥵������� 1��q --------�˳� 2��login �û��� ���� --------��¼ 3��encrypt table��������
 * property���������� --------���ܣ�ʵ���������������� 4��decrypt table�������� property����������
 * -------����
 */

public class Command extends Thread {

	public static final String COMMAND_QIUT = "q";
	public static final String COMMAND_LOGIN = "login";
	public static final String COMMAND_ENCRYPT = "encrypt";
	public static final String COMMAND_DECRYPT = "decrypt";

	private static Object mylock = new Object(); // staticȷ��ֻ��һ����

	private String command = null;
	private KeyAndVector kav = null; // ��Կ����������
	private PrintWriter pw = null;
	private DatabaseConnection dbc = null;
	private String result = null;
	
	public Command(String target, PrintWriter pw, DatabaseConnection dbc) {
		command = target;
		this.pw = pw;
		this.dbc = dbc;
		kav = new KeyAndVector();
	}
		
	public void run() {
		result = process(dbc);
		ServerMessage.ServerMessageOutput(result);
		// ��ͻ��˷�������
		pw.println(result);
		pw.flush();
	}

	/**
	 * ���û����͹�������������������
	 * 
	 * @param target
	 *            ����
	 * @param dbc
	 * @return ��Ϣid
	 */
	public String process(DatabaseConnection dbc) {
		// ����������
		String[] commandArr = command.split(" ");

		if (commandArr[0].equals(COMMAND_QIUT)) {
			return ServerMessage.QIUT;
		} else if (commandArr[0].equals(COMMAND_LOGIN)) {
			return loginProcess(commandArr[1], commandArr[2], dbc);
		} else if (commandArr[0].equals(COMMAND_ENCRYPT)) {
			return encryptProcess(commandArr[1], commandArr[2], dbc);
		} else if (commandArr[0].equals(COMMAND_DECRYPT)) {
			return decryptProcess(commandArr[1], commandArr[2], dbc);
		}

		return ServerMessage.NULL;
	}

	/**
	 * ��¼�������
	 * 
	 * @param userName
	 *            �û���
	 * @param password
	 *            ����
	 * @param dbc
	 * @return ��Ϣid
	 */
	private String loginProcess(String userName, String password, DatabaseConnection dbc) {
		String res = ServerMessage.LOGINFAIL;
		String sql = "SELECT password from [graduation_project].[dbo].[user_tb] where username = ?";

		PreparedStatement pstmt = null;
		try {
			pstmt = (PreparedStatement) dbc.dbConn.prepareStatement(sql);
			pstmt.setString(1, userName);
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				/*
				 * �����и����⣬������ȡ���������ݺ��������ո�
				 * �����ҵ��뷨�Ǳ��������������еĿո���Ϊ����Ϊ�ַ������������ո���ܳ����Ķ���Ӧ���ǲ�ͬ��
				 * �󾭹���֤���֣����ܺ�Ҳ��ԭ���Ķ����������Ȳ������������еĿո�
				 */
				String temp = rs.getString(1);
				System.out.println(password + " t");
				System.out.println(temp + " t");

				//����
				sql = "SELECT [graduation_project].[dbo].[Des_Decrypt]('" + temp + "', '20111219', '12345678');";
				pstmt = (PreparedStatement) dbc.dbConn.prepareStatement(sql);
				ResultSet rs2 = pstmt.executeQuery();

				while (rs2.next()) {
					temp = rs2.getString(1);
					break;
				}

				System.out.println(password + " t");
				System.out.println(temp + " t");

				if (temp.equals(password)) {
					res = ServerMessage.LOGINSUCCESS;
					break;
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return res;
	}

	/**
	 * ������Ϣ��������ĳ���ĳ�н��м��ܣ�ĳ���ĳ�е�ǰ����δ��������
	 * 
	 * @param table
	 *            ����
	 * @param property
	 *            ����
	 * @param dbc
	 * @return ��Ϣid
	 */
	private String encryptProcess(String table, String property, DatabaseConnection dbc) {
		String res = ServerMessage.NULL;
		// ȷ������д���
		res = tbExists(table, dbc);
		if (res.equals(ServerMessage.NOTABLE))
			return res;

		ServerMessage.ServerMessageOutput(ServerMessage.EXISTTABLE); // ��λ��Ϣ

		res = propertyExists(table, property, dbc);
		if (res.equals(ServerMessage.NOPROPERTY))
			return res;

		ServerMessage.ServerMessageOutput(ServerMessage.EXISTPROPERTY); // ��λ��Ϣ

		// ��������֤���������̵�������
		synchronized (mylock) {
			res = ServerMessage.ENCRYPTSUCCESS;
			// ����ȡ����Կ������
			String key = kav.getKey();
			String vt = kav.getVector();
			System.out.println(key + " " + vt);

			String sql = "SELECT * from [graduation_project].[dbo].[" + table + "]";

			PreparedStatement pstmt = null;
			try {

				boolean autoCommit = dbc.dbConn.getAutoCommit();
				// �ر��Զ��ύ����
				dbc.dbConn.setAutoCommit(false);

				pstmt = (PreparedStatement) dbc.dbConn.prepareStatement(sql);
				ResultSet rs = pstmt.executeQuery();
				while (rs.next()) { // ��ͣ�ؽ��м���
					// ȡ�ü�������
					String temp = rs.getString(1); // ����ҵı�������Ե�һ��Ϊ���������Ҳ��ܶԵ�һ�н��м���
					String target = rs.getString(property); // ���ǽ�Ҫ���ܵ�����
					System.out.println(temp + " " + target);

					// ִ�м��ܲ���,temp1�Ǽ��ܺ�Ľ��
					String temp1 = "";
					sql = "SELECT [graduation_project].[dbo].[Des_Encrypt]('" + target + "', '" + key + "', '" + vt
							+ "')";
					pstmt = (PreparedStatement) dbc.dbConn.prepareStatement(sql);
					ResultSet rs2 = pstmt.executeQuery();
					while (rs2.next()) {
						temp1 = rs2.getString(1);
						break;
					}
					System.out.println(target + " " + temp1);

					// ���±������ܺ�����ݸ��µ�����
					sql = "UPDATE [graduation_project].[dbo].[" + table + "] SET " + property + " = '" + temp1
							+ "' WHERE " + property + " = '" + target + "'";
					pstmt = (PreparedStatement) dbc.dbConn.prepareStatement(sql);
					int i = pstmt.executeUpdate();
					if (i == 0) { // ���������
						res = ServerMessage.ENCRYPTFAIL;
					}
				}
				// ��¼������Ϣ
				sql = "insert into message_tb(tb_name,property,algorithm,secret_key,vector) VALUES(?,?,?,?,?)";
				pstmt = (PreparedStatement) dbc.dbConn.prepareStatement(sql);
				pstmt.setString(1, table);
				pstmt.setString(2, property);
				pstmt.setString(3, "des"); // ����ֻ��des�㷨
				pstmt.setString(4, key);
				pstmt.setString(5, vt);
				pstmt.executeUpdate();

				// �ύ����
				dbc.dbConn.commit();
				// �ָ�ԭ�����ύģʽ
				dbc.dbConn.setAutoCommit(autoCommit);
			} catch (SQLException e) { // ֻҪ������һ��sqlִ�д��󣬾�Ӧ�ûع�
				res = ServerMessage.ENCRYPTFAIL;
				try {
					// �ع���ȡ��ǰ������
					dbc.dbConn.rollback();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
				e.printStackTrace();
			}
		}

		return res;
	}

	/**
	 * ������Ϣ��������ĳ���ĳ�н��н��ܣ�ĳ���ĳ�е�ǰ�����Ѿ�������
	 * 
	 * @param table
	 *            ����
	 * @param property
	 *            ����
	 * @param dbc
	 * @return ��Ϣid
	 */
	private String decryptProcess(String table, String property, DatabaseConnection dbc) {
		String res = ServerMessage.NULL;
		// ȷ������д���
		res = tbExists(table, dbc);
		if (res.equals(ServerMessage.NOTABLE))
			return res;

		ServerMessage.ServerMessageOutput(ServerMessage.EXISTTABLE); // ��λ��Ϣ

		res = propertyExists(table, property, dbc);
		if (res.equals(ServerMessage.NOPROPERTY))
			return res;

		ServerMessage.ServerMessageOutput(ServerMessage.EXISTPROPERTY); // ��λ��Ϣ

		synchronized (mylock) {
			res = ServerMessage.DECRYPTSUCCESS;
			String key = "";
			String vt = "";

			String sql = "SELECT * from [graduation_project].[dbo].[message_tb] where tb_name = '" + table
					+ "' and property = '" + property + "'";

			PreparedStatement pstmt = null;
			try {
				
				boolean autoCommit = dbc.dbConn.getAutoCommit();
				// �ر��Զ��ύ����
				dbc.dbConn.setAutoCommit(false);
				
				pstmt = (PreparedStatement) dbc.dbConn.prepareStatement(sql);
				ResultSet rs = pstmt.executeQuery();
				while (rs.next()) { // ȡ�ý��ܵ���Կ������
					key = rs.getString("secret_key");
					vt = rs.getString("vector");
					break; // ����ֻ����һ��
				}
				System.out.println(key + " " + vt);
//				if(key.equals("") || vt.equals(""))
//					throw SQLException;
				// �����key��vt�����ڵ�ʱ�򣬽�����һ����ʧ�ܵģ����ʱ��Ӧ�û�ع�

				sql = "SELECT * from [graduation_project].[dbo].[" + table + "]";
				pstmt = (PreparedStatement) dbc.dbConn.prepareStatement(sql);
				rs = pstmt.executeQuery();
				while (rs.next()) { // ��ͣ�ؽ��н���
					// ȡ�ü�������
					String temp = rs.getString(1); // ����ҵı�������Ե�һ��Ϊ���������Ҳ��ܶԵ�һ�н��м���
					String target = rs.getString(property); // ���ǽ�Ҫ���ܵ�����
					System.out.println(temp + " " + target);

					// ִ�н��ܲ���,temp1�ǽ��ܺ�Ľ��
					String temp1 = "";
					sql = "SELECT [graduation_project].[dbo].[Des_Decrypt]('" + target + "', '" + key + "', '" + vt
							+ "')";
					pstmt = (PreparedStatement) dbc.dbConn.prepareStatement(sql);
					ResultSet rs2 = pstmt.executeQuery();
					while (rs2.next()) {
						temp1 = rs2.getString(1);
						break;
					}
					System.out.println(target + " " + temp1);

					// ���±������ܺ�����ݸ��µ�����
					sql = "UPDATE [graduation_project].[dbo].[" + table + "] SET " + property + " = '" + temp1
							+ "' WHERE " + property + " = '" + target + "'";
					pstmt = (PreparedStatement) dbc.dbConn.prepareStatement(sql);
					int i = pstmt.executeUpdate();
					if (i == 0) { // ���������
						res = ServerMessage.DECRYPTFAIL;
					}
				}
				// ��¼������Ϣ��ʵ����ɾ��ԭ���ĵļ�����Ϣ
				sql = "DELETE FROM message_tb WHERE tb_name = '" + table + "' and property = '" + property + "'";
				pstmt = (PreparedStatement) dbc.dbConn.prepareStatement(sql);
				pstmt.executeUpdate();
				
				// �ύ����
				dbc.dbConn.commit();
				// �ָ�ԭ�����ύģʽ
				dbc.dbConn.setAutoCommit(autoCommit);
			} catch (SQLException e) {
				res = ServerMessage.DECRYPTFAIL;
				try {
					// �ع���ȡ��ǰ������
					dbc.dbConn.rollback();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
				e.printStackTrace();
			}
		}
		return res;
	}

	/**
	 * ȷ�����ݿ����Ƿ���ڱ�
	 * 
	 * @param table
	 *            ����
	 * @param dbc
	 * @return
	 */
	private String tbExists(String table, DatabaseConnection dbc) {
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
	private String propertyExists(String table, String property, DatabaseConnection dbc) {
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