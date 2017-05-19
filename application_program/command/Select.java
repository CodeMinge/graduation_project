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
	 * para1 -----�� para2 -----��
	 */
	public String process(String para1, String para2, DBEncryptConnection dbec, KeyDBConnection kdbc, String name) {
		String res = ServerMessage.SELECTSUCCESS;
		String str = "";

		boolean exist = false;
		KeyManager km = new KeyManager();
		Key key = null;
		String dataType = null;

		String sql = command;

		// �Ա���н��н�������һ��ֻ��һ�����У� //��֧�ֵ����ѯ
		String[] col = para1.trim().split(",");
		String[] tb = para2.trim().split(",");

		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			boolean autoCommit = dbec.dbConn.getAutoCommit();
			// �ر��Զ��ύ����
			dbec.dbConn.setAutoCommit(false);

			LinkedList<String> list = new LinkedList<String>();
			if (col.length == 1 && col[0].equals("*")) {
				for (int i = 0; i < tb.length; i++) { // ��֧�ֵ����ѯ
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

			for (int i = 0; i < tb.length; i++) { // ��֧�ֵ����ѯ
				for (int j = 0; j < col.length; j++) {
					exist = false;
					String sql1 = null;

					// �ж���������Ƿ񱻼���
					sql1 = "select key_name from [DBEncryption].[dbo].[encrypt_message] where tb_name = '" + tb[i]
							+ "' and col_name = '" + col[j] + "'";
					pstmt = (PreparedStatement) dbec.dbConn.prepareStatement(sql1);
					rs = pstmt.executeQuery();
					while (rs.next()) {
						exist = true;
						key = km.getKey(rs.getString(1), dbec, kdbc);
					}

					Messsge_of_Select mos = new Messsge_of_Select(tb[i], col[j], key, exist);
					
					sql1 = "select a.name ����,b.name �ֶ���,c.name �ֶ�����,c.length �ֶγ��� "
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
			while (rs.next()) { // ȷ�����Ƿ�����������
				for (int i = 0; i < message_sel.size(); i++) {
					message_sel.get(i).print();
					if (message_sel.get(i).isEncrypt) {
						String tempString = "";
						int tempInt = 0;
						dataType = message_sel.get(i).dataType;
						// ִ�м��ܲ���,temp1�Ǽ��ܺ�Ľ��
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

			// �ύ����
			dbec.dbConn.commit();
			// �ָ�ԭ�����ύģʽ
			dbec.dbConn.setAutoCommit(autoCommit);
		} catch (SQLException e) {
			res = ServerMessage.SELECTFAIL;
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