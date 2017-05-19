package command;

import java.sql.*;
import java.util.*;

import DBConnect.DBEncryptConnection;
import DBConnect.KeyDBConnection;
import key_manage.Key;
import key_manage.KeyManager;
import message_center.ServerMessage;

public class Update extends Command {
	// �洢��ֵ��
	Map<String, String> col_value = new HashMap<String, String>();

	public Update(String command) {
		super(command);
	}

	public String process(String para1, String para2, DBEncryptConnection dbec, KeyDBConnection kdbc, String name) {
		String res = ServerMessage.UPDATESUCCESS;

		boolean exist = false;
		
		KeyManager km = new KeyManager();
		Key key = null;
		String dataType = null;

		// ��ֻ��һ�����������
		// �ȶ��н��н�������������洢��Map��
		analysisCol(para2);

		String sql = command;

		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			boolean autoCommit = dbec.dbConn.getAutoCommit();
			// �ر��Զ��ύ����
			dbec.dbConn.setAutoCommit(false);

			// �鿴���޸ĵ����Ƿ�Ϊ��������
			// ����Iterator����HashMap
			Iterator<String> it = col_value.keySet().iterator();
			while (it.hasNext()) {
				exist = false;
				String col = (String) it.next(); // ��
				String value = col_value.get(col); // ֵ

				System.out.println("col:" + col); // ��λ��Ϣ
				System.out.println("value:" + value); // ��λ��Ϣ

				// �ж���������Ƿ񱻼���
				String sql1 = "select key_name from [DBEncryption].[dbo].[encrypt_message] where tb_name = '" + para1
						+ "' and col_name = '" + col + "'";
				pstmt = (PreparedStatement) dbec.dbConn.prepareStatement(sql1);
				rs = pstmt.executeQuery();
				while (rs.next()) {
					exist = true;
					key = km.getKey(rs.getString(1), dbec, kdbc);
				}
				
				if (exist) { // ����������
					sql1 = "select a.name ����,b.name �ֶ���,c.name �ֶ�����,c.length �ֶγ��� "
							+ " from sysobjects a,syscolumns b,systypes c" + " where a.id=b.id and a.name='" + para1
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
					// ִ�м��ܲ���,temp1�Ǽ��ܺ�Ľ��
					if (dataType.equals("char") || dataType.equals("varchar") || dataType.equals("nchar")
							|| dataType.equals("nvarchar")) {
						sql1 = "SELECT [dbo].[DESEncrypt]('" + value + "', '" + key.getKeyData() + "', '"
								+ key.getVtData() + "')";
						pstmt = (PreparedStatement) dbec.dbConn.prepareStatement(sql1);
						ResultSet rs2 = pstmt.executeQuery();
						while (rs2.next()) {
							tempString = rs2.getString(1);
							break;
						}
						// �����µ����ݻ����ַ�������
						sql = sql.replace(col_value.get(col), tempString);
					} else if (dataType.equals("int")) {
						sql1 = "SELECT [dbo].[INTEncrypt](" + value + "," + key.getKeyData() + ")";
						pstmt = (PreparedStatement) dbec.dbConn.prepareStatement(sql1);
						ResultSet rs2 = pstmt.executeQuery();
						while (rs2.next()) {
							tempInt = rs2.getInt(1);
							break;
						}
						// �����µ����ݻ����ַ�������
						sql = sql.replace(col_value.get(col), tempInt + "");
					}
				}
			}

			pstmt = (PreparedStatement) dbec.dbConn.prepareStatement(sql);
			int i = pstmt.executeUpdate();
			if (i == 0)
				res = ServerMessage.UPDATEFAIL;

			// �ύ����
			dbec.dbConn.commit();
			// �ָ�ԭ�����ύģʽ
			dbec.dbConn.setAutoCommit(autoCommit);
		} catch (SQLException e) {
			res = ServerMessage.UPDATEFAIL;
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

	private void analysisCol(String para2) {
		// �Ƚ��ж��ŷָ�
		String[] str_by_comma = para2.split(",");
		for (int i = 0; i < str_by_comma.length; i++) {
			// �ٽ��еȺŷָ�
			String[] str_by_equal = str_by_comma[i].split("=");
			if (str_by_equal[1].contains("'")) {
				col_value.put(str_by_equal[0].trim(),
						str_by_equal[1].trim().substring(1, str_by_equal[1].length() - 2));
			} else {
				col_value.put(str_by_equal[0].trim(), str_by_equal[1].trim());
			}
		}
	}
}