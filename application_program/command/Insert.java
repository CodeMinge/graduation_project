package command;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import message_center.ServerMessage;
import server.DatabaseConnection;

public class Insert extends Command {

	// �洢��ֵ��
	Map<String, String> col_value = new HashMap<String, String>();

	public Insert(String command) {
		super(command);
	}

	/**
	 * para1 -----����Ϣ para2 -----����Ϊ
	 */
	public String process(String para1, String para2, DatabaseConnection dbc) {
		String res = ServerMessage.INSERTSUCCESS;

		String tb = getTb(para1);
		getVal(dbc, tb, command);

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
					String sql1 = "SELECT * from [graduation_project].[dbo].[message_tb] where tb_name = '" + tb
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
					res = ServerMessage.INSERTFAIL;
				
				// �ύ����
				dbc.dbConn.commit();
				// �ָ�ԭ�����ύģʽ
				dbc.dbConn.setAutoCommit(autoCommit);
			} catch (SQLException e) {
				res = ServerMessage.INSERTFAIL;
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

	// ��ñ�
	private String getTb(String target) {
		String[] res = target.split(" ");
		return res[0].trim();
	}

	// ���ֵ
	private void getVal(DatabaseConnection dbc, String table, String target) {
		int valEnd = 0;
		int valStart = 0;
		int colEnd = 0; // ��һ������
		int colStart = 0; // ��һ������

		valEnd = target.lastIndexOf(")");
		valStart = target.lastIndexOf("(");

		colStart = target.indexOf("(");

		if (colStart == valStart) { //  INSERT INTO ������ VALUES (ֵ1,ֵ2,....)
			int i = 1;
			String[] val = target.substring(valStart + 1, valEnd).split(",");
			
			// ȡ�����е��У����뵽hashMap��
			// ���sql����Ľ��ֻ��һ��name�����������ϵ�������
			String sql = "select name from syscolumns where id = object_id('graduation_project.dbo." + table + "')"; // ��ѯ���е���

			PreparedStatement pstmt = null;
			try {
				pstmt = (PreparedStatement) dbc.dbConn.prepareStatement(sql);
				ResultSet rs = pstmt.executeQuery();
				while (rs.next()) {
					String temp = rs.getString(1); // ����
					col_value.put(temp, val[i].trim().substring(1, val[i].trim().length() - 1));
					i ++;
					if(i == val.length)
						i = 0;
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}

		} else { // INSERT INTO table_name (��1, ��2,...) VALUES (ֵ1, ֵ2,....)
			colEnd = target.indexOf(")");
			
			// ȡ�ö�Ӧ���У����뵽hashMap��
			String[] col = target.substring(colStart, colEnd).split(",");
			String[] val = target.substring(valStart, valEnd).split(",");
			
			for(int i = 0; i < col.length; i ++) {
				col_value.put(col[i], val[i].trim().substring(1, val[1].length() - 2));
			}
		}
	}
}
