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
	 * ������Ϣ��������ĳ���ĳ�н��н��ܣ�ĳ���ĳ�е�ǰ�����Ѿ�������
	 * 
	 * @param para1
	 *            ����
	 * @param para2
	 *            ����
	 * @param dbc
	 * @return ��Ϣid
	 */
	public String process(String para1, String para2, DBEncryptConnection dbec, KeyDBConnection kdbc, String name) {
		String res = ServerMessage.DECRYPTSUCCESS;
		String[] commandArr = command.split(" ");

		KeyManager km = new KeyManager();
		Key key = km.getKey(commandArr[3], dbec, kdbc);

		// ȡ��Ҫ���ܵ����ݵ��������ͣ���ѡ������㷨
		String sql = "select a.name ����,b.name �ֶ���,c.name �ֶ�����,c.length �ֶγ��� "
				+ " from sysobjects a,syscolumns b,systypes c" + " where a.id=b.id and a.name='" + para1
				+ "' and a.xtype='U' and b.xtype=c.xtype";

		PreparedStatement pstmt = null;
		try {
			boolean autoCommit = dbec.dbConn.getAutoCommit();
			// �ر��Զ��ύ����
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

			// ����������ȡ������
			sql = "SELECT * from [" + para1 + "]";
			pstmt = (PreparedStatement) dbec.dbConn.prepareStatement(sql);
			rs = pstmt.executeQuery();
			while (rs.next()) { // ��ͣ�ؽ��м���
				// ȡ�ü�������
				// String temp = rs.getString(1); //
				// ����ҵı�������Ե�һ��Ϊ���������Ҳ��ܶԵ�һ�н��м���
				String target = rs.getString(para2); // ���ǽ�Ҫ���ܵ�����
				// System.out.println(temp + " " + target);

				String tempString = "";
				int tempInt = 0;
				// ִ�м��ܲ���,temp1�Ǽ��ܺ�Ľ��
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

					// ���±������ܺ�����ݸ��µ�����
					sql = "UPDATE [" + para1 + "] SET " + para2 + " = '" + tempString + "' WHERE " + para2 + " = '"
							+ target + "'";
					pstmt = (PreparedStatement) dbec.dbConn.prepareStatement(sql);
					int i = pstmt.executeUpdate();
					if (i == 0) { // ���������
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

					// ���±������ܺ�����ݸ��µ�����
					sql = "UPDATE [" + para1 + "] SET " + para2 + " = " + tempInt + " WHERE " + para2 + " = " + target
							+ "";
					pstmt = (PreparedStatement) dbec.dbConn.prepareStatement(sql);
					int i = pstmt.executeUpdate();
					if (i == 0) { // ���������
						res = ServerMessage.ENCRYPTFAIL;
					}
				}
			}

			// ��¼������Ϣ
			sql = "delete from [encrypt_message] where username = '" + name + "' and tb_name = '" + para1
					+ "' and col_name = '" + para2 + "' and key_name = '" + commandArr[3] + "'";
			pstmt = (PreparedStatement) dbec.dbConn.prepareStatement(sql);
			pstmt.executeUpdate();

			// �ύ����
			dbec.dbConn.commit();
			// �ָ�ԭ�����ύģʽ
			dbec.dbConn.setAutoCommit(autoCommit);
		} catch (SQLException e) { // ֻҪ������һ��sqlִ�д��󣬾�Ӧ�ûع�
			res = ServerMessage.DECRYPTFAIL;
			try {
				// �ع���ȡ��ǰ������
				dbec.dbConn.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			e.printStackTrace();
		}

		return res;
	}
}