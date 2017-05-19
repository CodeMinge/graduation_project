package command;

import java.sql.*;
import java.util.*;

import DBConnect.DBEncryptConnection;
import DBConnect.KeyDBConnection;
import key_manage.Key;
import key_manage.KeyManager;
import message_center.ServerMessage;

public class Insert extends Command {

	// �洢��ֵ��
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
				String sql1 = "select key_name from [DBEncryption].[dbo].[encrypt_message] where tb_name = '" + tb + "' and col_name = '"
						+ col + "'";
				pstmt = (PreparedStatement) dbec.dbConn.prepareStatement(sql1);
				rs = pstmt.executeQuery();
				while (rs.next()) {
					exist = true;
					key = km.getKey(rs.getString(1), dbec, kdbc);
				}

				if (exist) { // ����������
					sql1 = "select a.name ����,b.name �ֶ���,c.name �ֶ�����,c.length �ֶγ��� "
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
					// ִ�м��ܲ���,temp1�Ǽ��ܺ�Ľ��
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
				res = ServerMessage.INSERTFAIL;

			// �ύ����
			dbec.dbConn.commit();
			// �ָ�ԭ�����ύģʽ
			dbec.dbConn.setAutoCommit(autoCommit);
		} catch (SQLException e) {
			res = ServerMessage.INSERTFAIL;
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

	// ��ñ�
	private String getTb(String target) {
		String[] res = target.split(" ");
		return res[0].trim();
	}

	// ���ֵ
	private void getVal(DBEncryptConnection dbec, String table, String target) {
		int valEnd = 0;
		int valStart = 0;
		int colEnd = 0; // ��һ������
		int colStart = 0; // ��һ������

		valEnd = target.lastIndexOf(")");
		valStart = target.lastIndexOf("(");

		colStart = target.indexOf("(");

		if (colStart == valStart) { // INSERT INTO ������ VALUES (ֵ1,ֵ2,....)
//			String[] val = target.substring(valStart + 1, valEnd).split(",");
//			int i = val.length - 1;
//
//			// ȡ�����е��У����뵽hashMap��
//			// ���sql����Ľ��ֻ��һ��name�����������ϵ�������
//			String sql = "select name from syscolumns where id = object_id('" + table + "')"; // ��ѯ���е���
//
//			PreparedStatement pstmt = null;
//			try {
//				pstmt = (PreparedStatement) dbec.dbConn.prepareStatement(sql);
//				ResultSet rs = pstmt.executeQuery();
//				while (rs.next()) {
//					String temp = rs.getString(1); // ����
//					col_value.put(temp, val[i].trim());
//					i--;
//				}
//			} catch (SQLException e) {
//				e.printStackTrace();
//			}
		} else { // INSERT INTO table_name (��1, ��2,...) VALUES (ֵ1, ֵ2,....)
			colEnd = target.indexOf(")");

			// ȡ�ö�Ӧ���У����뵽hashMap��
			String[] col = target.substring(colStart + 1, colEnd).split(",");
			String[] val = target.substring(valStart + 1, valEnd).split(",");

			for (int i = 0; i < col.length; i++) {
				col_value.put(col[i].trim(), val[i].trim());
			}
		}
	}
}