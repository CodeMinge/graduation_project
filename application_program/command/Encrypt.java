package command;

import java.sql.*;

import message_center.ServerMessage;
import server.DatabaseConnection;

public class Encrypt extends Command {

	public Encrypt(String command) {
		super(command);
	}

	/**
	 * ������Ϣ��������ĳ���ĳ�н��м��ܣ�ĳ���ĳ�е�ǰ����δ��������
	 * 
	 * @param para1
	 *            ����
	 * @param para2
	 *            ����
	 * @param dbc
	 * @return ��Ϣid
	 */
	public String process(String para1, String para2, DatabaseConnection dbc) {
		String res = ServerMessage.NULL;
		// ȷ������д���
		res = tbExists(para1, dbc);
		if (res.equals(ServerMessage.NOTABLE))
			return res;

		ServerMessage.ServerMessageOutput(ServerMessage.EXISTTABLE); // ��λ��Ϣ

		res = propertyExists(para1, para2, dbc);
		if (res.equals(ServerMessage.NOPROPERTY))
			return res;

		ServerMessage.ServerMessageOutput(ServerMessage.EXISTPROPERTY); // ��λ��Ϣ

		// ��������֤���������̵�������
		synchronized (workerTbLock) {
			res = ServerMessage.ENCRYPTSUCCESS;
			// ����ȡ����Կ������
			String key = kav.getKey();
			String vt = kav.getVector();
			System.out.println(key + " " + vt);

			String sql = "SELECT * from [graduation_project].[dbo].[" + para1 + "]";

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
					String target = rs.getString(para2); // ���ǽ�Ҫ���ܵ�����
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
					sql = "UPDATE [graduation_project].[dbo].[" + para1 + "] SET " + para2 + " = '" + temp1
							+ "' WHERE " + para2 + " = '" + target + "'";
					pstmt = (PreparedStatement) dbc.dbConn.prepareStatement(sql);
					int i = pstmt.executeUpdate();
					if (i == 0) { // ���������
						res = ServerMessage.ENCRYPTFAIL;
					}
				}
				// ��¼������Ϣ
				sql = "insert into message_tb(tb_name,property,algorithm,secret_key,vector) VALUES(?,?,?,?,?)";
				pstmt = (PreparedStatement) dbc.dbConn.prepareStatement(sql);
				pstmt.setString(1, para1);
				pstmt.setString(2, para2);
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
}