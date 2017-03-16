package command;

import java.sql.*;
import java.util.*;

import message_center.ServerMessage;
import server.DatabaseConnection;

public class Update extends Command {
	// �洢��ֵ��
	Map<String, String> col_value = new HashMap<String, String>();

	public Update(String command) {
		super(command);
	}

	/*
	 * para1 -----�� para2 -----��
	 */
	public String process(String para1, String para2, DatabaseConnection dbc) {
		String res = ServerMessage.UPDATESUCCESS;

		// ��ֻ��һ�����������
		// �ȶ��н��н�������������洢��Map��
		analysisCol(para2);

		synchronized (workerTbLock) {
			String sql = command;

			PreparedStatement pstmt = null;
			try {
				boolean autoCommit = dbc.dbConn.getAutoCommit();
				// �ر��Զ��ύ����
				dbc.dbConn.setAutoCommit(false);

				// �鿴���޸ĵ����Ƿ�Ϊ��������
				// ����Iterator����HashMap
				Iterator<String> it = col_value.keySet().iterator();
				while (it.hasNext()) {
					String col = (String) it.next(); // ��
					String value = col_value.get(col); // ֵ

					System.out.println("col:" + col); // ��λ��Ϣ
					System.out.println("value:" + value); // ��λ��Ϣ

					// ���ñ������message_tb�ϲ��Ҽ�����Ϣ
					String sql1 = "SELECT * from [graduation_project].[dbo].[message_tb] where tb_name = '" + para1
							+ "' and property = '" + col + "'";

					String key = ""; // ����
					String vt = ""; // ����

					pstmt = (PreparedStatement) dbc.dbConn.prepareStatement(sql1);
					ResultSet rs = pstmt.executeQuery();
					while (rs.next()) { // ȡ�ý��ܵ���Կ������
						key = rs.getString("secret_key");
						vt = rs.getString("vector");
						break; // ����ֻ����һ��
					}

					if (!key.equals("") && !vt.equals("")) { // ����������
						sql1 = "SELECT [graduation_project].[dbo].[Des_Encrypt]('" + value + "', '" + key + "', '" + vt
								+ "')";
						pstmt = (PreparedStatement) dbc.dbConn.prepareStatement(sql1);
						rs = pstmt.executeQuery();
						while (rs.next()) {
							value = rs.getString(1);
							break;
						}
					}

					// �����µ����ݻ����ַ�������
					sql = sql.replace(col_value.get(col), value);
				}

				pstmt = (PreparedStatement) dbc.dbConn.prepareStatement(sql);
				int i = pstmt.executeUpdate();
				if (i == 0)
					res = ServerMessage.UPDATEFAIL;

				// �ύ����
				dbc.dbConn.commit();
				// �ָ�ԭ�����ύģʽ
				dbc.dbConn.setAutoCommit(autoCommit);
			} catch (SQLException e) {
				res = ServerMessage.UPDATEFAIL;
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

	private void analysisCol(String para2) {
		// �Ƚ��ж��ŷָ�
		String[] str_by_comma = para2.split(",");
		for (int i = 0; i < str_by_comma.length; i++) {
			// �ٽ��еȺŷָ�
			String[] str_by_equal = str_by_comma[i].split("=");
			col_value.put(str_by_equal[0].trim(), str_by_equal[1].trim().substring(1, str_by_equal[1].length() - 2));
		}
	}
}