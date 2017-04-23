package command;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import message_center.ServerMessage;
import server.DatabaseConnection;
import server.Server;

public class Insert extends Command {

	// �洢��ֵ��
	Map<String, String> col_value = new HashMap<String, String>();

	public Insert(String command) {
		super(command);
	}

	/**
	 * para1 -----����Ϣ para2 -----����Ϊ
	 */
	public String process(String para1, String para2, DatabaseConnection dbc, String name) {
		String res = ServerMessage.INSERTSUCCESS;

		String tb = getTb(para1);
		getVal(dbc, tb, command);

		int type = -1;
		String b_key = null;
		String b_vt = null;
		boolean exist = false;

		String sql = command;

		PreparedStatement pstmt = null;
		try {
			boolean autoCommit = dbc.dbConn.getAutoCommit();
			// �ر��Զ��ύ����
			dbc.dbConn.setAutoCommit(false);

			// ���Ȳ��ȷ���Լ����û����ͣ������û�������ͨ�û���
			for (int i = 0; i < Server.userList.size(); i++) {
				if (Server.userList.get(i).getName().equals(name)) {
					type = Server.userList.get(i).getType();
					if (type == 1) { // �����û�
						b_key = Server.userList.get(i).getKey();
						b_vt = Server.userList.get(i).getVector();
					} else { // ��ͨ�û�
						if (!Server.userList.get(i).contain(tb)) {
							return ServerMessage.UPDATEFAIL;
						}
						b_key = Server.userList.get(i).getKey();
						b_vt = Server.userList.get(i).getVector();
					}
					break;
				}
			}

			String key = b_key;
			String vt = b_vt;

			// �鿴���޸ĵ����Ƿ�Ϊ��������
			// ����Iterator����HashMap
			Iterator<String> it = col_value.keySet().iterator();
			while (it.hasNext()) {
				exist = false;
				String col = (String) it.next(); // ��
				String value = col_value.get(col); // ֵ

				System.out.println("col:" + col); // ��λ��Ϣ
				System.out.println("value:" + value); // ��λ��Ϣ

				String sql1 = null;
				// ���ñ���в��Ҽ�����Ϣ
				if (type == 1) {
					// �����û��Լ����ܵ�
					String sql2 = "select * from message_tb_super where tb_name = '" + tb + "' and property = '"
							+ col + "'";
					pstmt = (PreparedStatement) dbc.dbConn.prepareStatement(sql2);
					ResultSet rs = pstmt.executeQuery();
					while (rs.next()) {
						exist = true;
					}
					if (exist) { // ���ǳ����û��ӵ���
						key = b_key;
						vt = b_vt;
					} else {// �����ǳ����û��ӵ���
						//�鿴�Ƿ�����ͨ�û��ӵ���
						sql1 = "SELECT * from [graduation_project].[dbo].[message_tb] where tb_name = '" + tb
								+ "' and property = '" + col + "'";
						pstmt = (PreparedStatement) dbc.dbConn.prepareStatement(sql1);
						ResultSet rs1 = pstmt.executeQuery();
						exist = false;
						while (rs1.next()) { // ȷ�����Ƿ�����������
							exist = true;
							break; // ����ֻ����һ��
						}
						
						if(!exist) {
							continue;
						}
						
						key = null;
						vt = null;
						sql2 = "select * from user_kv"; // �鿴�����û�
						pstmt = (PreparedStatement) dbc.dbConn.prepareStatement(sql2);
						rs = pstmt.executeQuery();
						while (rs.next()) {
							sql2 = "select table_name from " + rs.getString(1); // �鿴�û�������ı�
							pstmt = (PreparedStatement) dbc.dbConn.prepareStatement(sql2);
							ResultSet rs2 = pstmt.executeQuery();
							while (rs2.next()) {
								if (rs2.getString(1).equals(tb)) { // �ñ���Ҫ���ܵı���ͬ
									int k1 = rs.getInt(2);
									int k2 = rs.getInt(3);
									int v1 = rs.getInt(4);
									int v2 = rs.getInt(5);

									key = k1 + "" + k2;
									vt = v1 + "" + v2;
									break;
								}
							}

							if (key != null && vt != null) {
								break;
							}
						}
					}
				} else {
					sql1 = "SELECT * from [graduation_project].[dbo].[message_tb] where tb_name = '" + tb
							+ "' and property = '" + col + "'";
					pstmt = (PreparedStatement) dbc.dbConn.prepareStatement(sql1);
					ResultSet rs = pstmt.executeQuery();
					exist = false;
					while (rs.next()) { // ȷ�����Ƿ�����������
						exist = true;
						break; // ����ֻ����һ��
					}
				}

				if (exist) { // ����������
					sql1 = "select a.name ����,b.name �ֶ���,c.name �ֶ�����,c.length �ֶγ��� "
							+ " from sysobjects a,syscolumns b,systypes c" + " where a.id=b.id and a.name='" + tb
							+ "' and a.xtype='U' and b.xtype=c.xtype";

					String dataType = null;
					pstmt = (PreparedStatement) dbc.dbConn.prepareStatement(sql1);
					ResultSet rs = pstmt.executeQuery();
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
						sql1 = "SELECT [graduation_project].[dbo].[Des_Encrypt]('" + target + "', '" + key + "', '" + vt
								+ "')";
						pstmt = (PreparedStatement) dbc.dbConn.prepareStatement(sql1);
						ResultSet rs2 = pstmt.executeQuery();
						while (rs2.next()) {
							tempString = rs2.getString(1);
							break;
						}
						tempString = "'" + tempString + "'";
						// �����µ����ݻ����ַ�������
						sql = sql.replace(col_value.get(col), tempString);
					} else if (dataType.equals("int")) {
						sql1 = "SELECT [graduation_project].[dbo].[INTEncrypt](" + value + "," + key + ")";
						pstmt = (PreparedStatement) dbc.dbConn.prepareStatement(sql1);
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

		if (colStart == valStart) { // INSERT INTO ������ VALUES (ֵ1,ֵ2,....)
			String[] val = target.substring(valStart + 1, valEnd).split(",");
			int i = val.length - 1;

			// ȡ�����е��У����뵽hashMap��
			// ���sql����Ľ��ֻ��һ��name�����������ϵ�������
			String sql = "select name from syscolumns where id = object_id('graduation_project.dbo." + table + "')"; // ��ѯ���е���

			PreparedStatement pstmt = null;
			try {
				pstmt = (PreparedStatement) dbc.dbConn.prepareStatement(sql);
				ResultSet rs = pstmt.executeQuery();
				while (rs.next()) {
					String temp = rs.getString(1); // ����
					col_value.put(temp, val[i].trim());
					i--;
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}

		} else { // INSERT INTO table_name (��1, ��2,...) VALUES (ֵ1, ֵ2,....)
			colEnd = target.indexOf(")");

			// ȡ�ö�Ӧ���У����뵽hashMap��
			String[] col = target.substring(colStart, colEnd).split(",");
			String[] val = target.substring(valStart, valEnd).split(",");

			for (int i = 0; i < col.length; i++) {
				col_value.put(col[i], val[i].trim());
			}
		}
	}
}
