package command;

import java.sql.*;

import message_center.ServerMessage;
import server.DatabaseConnection;

public class Decrypt extends Command {

	public Decrypt(String command) {
		super(command);
	}

	/**
	 * ������Ϣ��������ĳ���ĳ�н��н��ܣ�ĳ���ĳ�е�ǰ�����Ѿ�������
	 * 
	 * @param para1
	 *            ����
	 * @param para2
	 *            ����
	 * @param dbc
	 * @return ��Ϣid
	 */
	public String process(String para1, String para2, DatabaseConnection dbc, String name) {
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

		res = ServerMessage.DECRYPTSUCCESS;
		String key = "";
		String vt = "";

		String sql = "SELECT * from [graduation_project].[dbo].[message_tb] where tb_name = '" + para1
				+ "' and property = '" + para2 + "'";

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
			// if(key.equals("") || vt.equals(""))
			// throw SQLException;
			// �����key��vt�����ڵ�ʱ�򣬽�����һ����ʧ�ܵģ����ʱ��Ӧ�û�ع�

			sql = "SELECT * from [graduation_project].[dbo].[" + para1 + "]";
			pstmt = (PreparedStatement) dbc.dbConn.prepareStatement(sql);
			rs = pstmt.executeQuery();
			while (rs.next()) { // ��ͣ�ؽ��н���
				// ȡ�ü�������
				String temp = rs.getString(1); // ����ҵı�������Ե�һ��Ϊ���������Ҳ��ܶԵ�һ�н��м���
				String target = rs.getString(para2); // ���ǽ�Ҫ���ܵ�����
				System.out.println(temp + " " + target);

				// ִ�н��ܲ���,temp1�ǽ��ܺ�Ľ��
				String temp1 = "";
				sql = "SELECT [graduation_project].[dbo].[Des_Decrypt]('" + target + "', '" + key + "', '" + vt + "')";
				pstmt = (PreparedStatement) dbc.dbConn.prepareStatement(sql);
				ResultSet rs2 = pstmt.executeQuery();
				while (rs2.next()) {
					temp1 = rs2.getString(1);
					break;
				}
				System.out.println(target + " " + temp1);

				// ���±������ܺ�����ݸ��µ�����
				sql = "UPDATE [graduation_project].[dbo].[" + para1 + "] SET " + para2 + " = '" + temp1 + "' WHERE "
						+ para2 + " = '" + target + "'";
				pstmt = (PreparedStatement) dbc.dbConn.prepareStatement(sql);
				int i = pstmt.executeUpdate();
				if (i == 0) { // ���������
					res = ServerMessage.DECRYPTFAIL;
				}
			}
			// ��¼������Ϣ��ʵ����ɾ��ԭ���ĵļ�����Ϣ
			sql = "DELETE FROM message_tb WHERE tb_name = '" + para1 + "' and property = '" + para2 + "'";
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

		return res;
	}
}
