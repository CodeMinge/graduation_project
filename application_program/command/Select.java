package command;

import java.sql.*;
import java.util.LinkedList;

import message_center.ServerMessage;
import server.DatabaseConnection;
import server.Server;

public class Select extends Command {

	LinkedList<Messsge_of_Select> message_sel = new LinkedList<Messsge_of_Select>();

	public Select(String command) {
		super(command);
	}

	/*
	 * para1 -----�� para2 -----��
	 */
	public String process(String para1, String para2, DatabaseConnection dbc, String name) {
		String res = ServerMessage.SELECTSUCCESS;
		String str = "";

		int type = -1;
		String b_key = null;
		String b_vt = null;
		boolean exist = false;

		String sql = command;

		// �Ա���н��н�������һ��ֻ��һ�����У� //��֧�ֵ����ѯ
		String[] col = para1.trim().split(",");
		String[] tb = para2.trim().split(",");

		PreparedStatement pstmt = null;
		ResultSet rs = null;
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
						if (!Server.userList.get(i).contain(para1)) {
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
			LinkedList<String> list = new LinkedList<String>();
			if (col.length == 1 && col[0].equals("*")) {
				for (int i = 0; i < tb.length; i++) { // ��֧�ֵ����ѯ
					String sql1 = "Select name from syscolumns where id =object_id('" + tb[i] + "')";

					pstmt = (PreparedStatement) dbc.dbConn.prepareStatement(sql1);
					rs = pstmt.executeQuery();
					while (rs.next()) {
						String temp = rs.getString(1);
						list.addFirst(temp);
						str += temp + " ";
					}
				}
				str += "&&";
			}

			col = new String[list.size()];
			for (int i = 0; i < col.length; i++)
				col[i] = list.get(i);

			for (int i = 0; i < tb.length; i++) { // ��֧�ֵ����ѯ
				for (int j = 0; j < col.length; j++) {
					exist = false;
					String sql1 = null;
					// ���ñ���в��Ҽ�����Ϣ
					if (type == 1) {
						// �����û��Լ����ܵ�
						String sql2 = "select * from message_tb_super where tb_name = '" + tb[i] + "' and property = '"
								+ col[j] + "'";
						pstmt = (PreparedStatement) dbc.dbConn.prepareStatement(sql2);
						rs = pstmt.executeQuery();
						while (rs.next()) {
							exist = true;
						}
						if (exist) { // ���ǳ����û��ӵ���
							key = b_key;
							vt = b_vt;
						} else {// �����ǳ����û��ӵ���
							// �鿴�Ƿ�����ͨ�û��ӵ���
							sql1 = "SELECT * from [graduation_project].[dbo].[message_tb] where tb_name = '" + tb[i]
									+ "' and property = '" + col[j] + "'";
							pstmt = (PreparedStatement) dbc.dbConn.prepareStatement(sql1);
							ResultSet rs1 = pstmt.executeQuery();
							exist = false;
							while (rs1.next()) { // ȷ�����Ƿ�����������
								exist = true;
								break; // ����ֻ����һ��
							}

							if (!exist) {
								Messsge_of_Select mos = new Messsge_of_Select(tb[i], col[j], key, vt, exist);
								message_sel.addLast(mos);
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
									if (rs2.getString(1).equals(tb[i])) { // �ñ���Ҫ���ܵı���ͬ
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
						sql1 = "SELECT * from [graduation_project].[dbo].[message_tb] where tb_name = '" + tb[i]
								+ "' and property = '" + col[j] + "'";
						pstmt = (PreparedStatement) dbc.dbConn.prepareStatement(sql1);
						rs = pstmt.executeQuery();
						exist = false;
						while (rs.next()) { // ȷ�����Ƿ�����������
							exist = true;
							break; // ����ֻ����һ��
						}
					}

					Messsge_of_Select mos = new Messsge_of_Select(tb[i], col[j], key, vt, exist);
					String sql3 = "select a.name ����,b.name �ֶ���,c.name �ֶ�����,c.length �ֶγ��� "
							+ " from sysobjects a,syscolumns b,systypes c" + " where a.id=b.id and a.name='" + tb[i]
							+ "' and a.xtype='U' and b.xtype=c.xtype";

					String dataType = null;
					pstmt = (PreparedStatement) dbc.dbConn.prepareStatement(sql3);
					ResultSet rs3 = pstmt.executeQuery();
					while (rs3.next()) {
						if (rs3.getString(2).equals(col[j])) {
							dataType = rs3.getString(3);
							break;
						}
					}
					mos.dataType = dataType;
					message_sel.addLast(mos);
				}
			}

			pstmt = (PreparedStatement) dbc.dbConn.prepareStatement(sql);
			rs = pstmt.executeQuery();
			while (rs.next()) { // ȷ�����Ƿ�����������
				for (int i = 0; i < message_sel.size(); i++) {
					if (message_sel.get(i).isEncrypt) {
						String tempString = "";
						int tempInt = 0;
						String dataType = message_sel.get(i).dataType;
						// ִ�м��ܲ���,temp1�Ǽ��ܺ�Ľ��
						if (dataType.equals("char") || dataType.equals("varchar") || dataType.equals("nchar")
								|| dataType.equals("nvarchar")) {
							System.out.println(rs.getString(message_sel.get(i).col));
							String sql1 = "SELECT [graduation_project].[dbo].[Des_Decrypt]('"
									+ rs.getString(message_sel.get(i).col) + "', '" + message_sel.get(i).key + "', '"
									+ message_sel.get(i).vector + "')";
							pstmt = (PreparedStatement) dbc.dbConn.prepareStatement(sql1);
							ResultSet rs2 = pstmt.executeQuery();
							while (rs2.next()) {
								tempString = rs2.getString(1);
								break;
							}
							str += tempString + " ";
						} else if (dataType.equals("int")) {
							System.out.println(rs.getString(message_sel.get(i).col));
							String sql1 = "SELECT [graduation_project].[dbo].[INTDecrypt]("
									+ rs.getString(message_sel.get(i).col) + "," + message_sel.get(i).key + ")";
							pstmt = (PreparedStatement) dbc.dbConn.prepareStatement(sql1);
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
				str += "&&";
			}
			System.out.println(str);
			
			res += "||" + str;

			// �ύ����
			dbc.dbConn.commit();
			// �ָ�ԭ�����ύģʽ
			dbc.dbConn.setAutoCommit(autoCommit);
		} catch (SQLException e) {
			res = ServerMessage.SELECTFAIL;
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

	class Messsge_of_Select {
		String table;
		String col;
		String key;
		String vector;
		String dataType;
		boolean isEncrypt;

		Messsge_of_Select(String table, String col, String key, String vector, boolean isEncrypt) {
			this.table = table;
			this.col = col;
			this.key = key;
			this.vector = vector;
			this.isEncrypt = isEncrypt;
		}
	}
}