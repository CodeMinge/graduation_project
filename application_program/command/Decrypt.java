package command;

import java.sql.*;

import message_center.ServerMessage;
import server.DatabaseConnection;
import server.Server;

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
		String res = ServerMessage.DECRYPTSUCCESS;

		int type = -1;
		String key = null;
		String vt = null;
		boolean exist = false;
		boolean sup_en = false;

		// ȡ��Ҫ���ܵ����ݵ��������ͣ���ѡ������㷨
		String sql = "select a.name ����,b.name �ֶ���,c.name �ֶ�����,c.length �ֶγ��� "
				+ " from sysobjects a,syscolumns b,systypes c" + " where a.id=b.id and a.name='" + para1
				+ "' and a.xtype='U' and b.xtype=c.xtype";

		PreparedStatement pstmt = null;
		try {
			boolean autoCommit = dbc.dbConn.getAutoCommit();
			// �ر��Զ��ύ����
			dbc.dbConn.setAutoCommit(false);
			
			// ���Ȳ��ȷ���Լ����û����ͣ������û�������ͨ�û���
			// ȷ�������Ҫ���ܵı��Ƿ����Լ��Ĺ����б���
			for (int i = 0; i < Server.userList.size(); i++) {
				if (Server.userList.get(i).getName().equals(name)) {
					type = Server.userList.get(i).getType();
					if (type == 1) { // �����û�
						//���п����ǳ����û��Լ����ܵ�
						String sql2 = "select * from message_tb_super where tb_name = '" + para1 + "' and property = '" + para2 + "'";
						pstmt = (PreparedStatement) dbc.dbConn.prepareStatement(sql2);
						ResultSet rs = pstmt.executeQuery();
						while (rs.next()) {
							sup_en = true;
							exist = true;
						}
						if(exist) { // �ǳ����û��ӵ���
							key = Server.userList.get(i).getKey();
							vt = Server.userList.get(i).getVector();
						} else {
							sql2 = "select * from user_kv"; //�鿴�����û�
							pstmt = (PreparedStatement) dbc.dbConn.prepareStatement(sql2);
							rs = pstmt.executeQuery();
							while (rs.next()) {
								sql2 = "select table_name from " + rs.getString(1); // �鿴�û�������ı�
								pstmt = (PreparedStatement) dbc.dbConn.prepareStatement(sql2);
								ResultSet rs2 = pstmt.executeQuery();
								while(rs2.next()) {
									if(rs2.getString(1).equals(para1)) { //�ñ���Ҫ���ܵı���ͬ
										int k1 = rs.getInt(2);
										int k2 = rs.getInt(3);
										int v1 = rs.getInt(4);
										int v2 = rs.getInt(5);

										key = k1 + "" + k2;
										vt = v1 + "" + v2;
										break;
									}
								}
								
								if(key != null && vt != null) {
									break;
								}
							}
						}
					} else { // ��ͨ�û�
						if (!Server.userList.get(i).contain(para1)) {
							return ServerMessage.DECRYPTFAIL;
						}
						//��������ͨ�û����ܵ�
						String sql2 = "select * from message_tb where tb_name = '" + para1 + "' and property = '" + para2 + "'";
						pstmt = (PreparedStatement) dbc.dbConn.prepareStatement(sql2);
						ResultSet rs = pstmt.executeQuery();
						while (rs.next()) {
							exist = true;
						}
						if(exist) {
							key = Server.userList.get(i).getKey();
							vt = Server.userList.get(i).getVector();
						} else {
							return ServerMessage.DECRYPTFAIL;
						}
//						System.out.println(key + " " + vt);
					}
					break;
				}
			}

			String dataType = null;
			pstmt = (PreparedStatement) dbc.dbConn.prepareStatement(sql);
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				if (rs.getString(2).equals(para2)) {
					dataType = rs.getString(3);
					break;
				}
			}

			// ����������ȡ������
			sql = "SELECT * from [graduation_project].[dbo].[" + para1 + "]";
			pstmt = (PreparedStatement) dbc.dbConn.prepareStatement(sql);
			rs = pstmt.executeQuery();
			while (rs.next()) { // ��ͣ�ؽ��н���
				// ȡ�ý�������
				// String temp = rs.getString(1); //
				// ����ҵı�������Ե�һ��Ϊ���������Ҳ��ܶԵ�һ�н��н���
				String target = rs.getString(para2); // ���ǽ�Ҫ���ܵ�����
				// System.out.println(temp + " " + target);

				String tempString = "";
				int tempInt = 0;
				// ִ�н��ܲ���,temp�ǽ��ܺ�Ľ��
				if (dataType.equals("char") || dataType.equals("varchar") || dataType.equals("nchar")
						|| dataType.equals("nvarchar")) {
					sql = "SELECT [graduation_project].[dbo].[Des_Decrypt]('" + target + "', '" + key + "', '" + vt
							+ "')";
					pstmt = (PreparedStatement) dbc.dbConn.prepareStatement(sql);
					ResultSet rs2 = pstmt.executeQuery();
					while (rs2.next()) {
						tempString = rs2.getString(1);
						break;
					}
//					System.out.println(target + " " + tempString);

					// ���±������ܺ�����ݸ��µ�����
					sql = "UPDATE [graduation_project].[dbo].[" + para1 + "] SET " + para2 + " = '" + tempString
							+ "' WHERE " + para2 + " = '" + target + "'";
					pstmt = (PreparedStatement) dbc.dbConn.prepareStatement(sql);
					int i = pstmt.executeUpdate();
					if (i == 0) { // ���������
						res = ServerMessage.DECRYPTFAIL;
					}
				} else if (dataType.equals("int")) {
					sql = "SELECT [graduation_project].[dbo].[INTDecrypt](" + target + "," + key + ")";
					pstmt = (PreparedStatement) dbc.dbConn.prepareStatement(sql);
					ResultSet rs2 = pstmt.executeQuery();
					while (rs2.next()) {
						tempInt = rs2.getInt(1);
						break;
					}
//					System.out.println(target + " " + tempInt);

					// ���±������ܺ�����ݸ��µ�����
					sql = "UPDATE [graduation_project].[dbo].[" + para1 + "] SET " + para2 + " = " + tempInt + " WHERE "
							+ para2 + " = " + target + "";
					pstmt = (PreparedStatement) dbc.dbConn.prepareStatement(sql);
					int i = pstmt.executeUpdate();
					if (i == 0) { // ���������
						res = ServerMessage.DECRYPTFAIL;
					}
				}
			}

			// ��¼������Ϣ��ʵ����ɾ��ԭ���ĵļ�����Ϣ
			if(sup_en)
				sql = "DELETE FROM message_tb_super WHERE tb_name = '" + para1 + "' and property = '" + para2 + "'";
			else
				sql = "DELETE FROM message_tb WHERE tb_name = '" + para1 + "' and property = '" + para2 + "'";
			pstmt = (PreparedStatement) dbc.dbConn.prepareStatement(sql);
			int i = pstmt.executeUpdate();
			if (i == 0) {
				res = ServerMessage.DECRYPTFAIL;
			}
			
			// �ύ����
			dbc.dbConn.commit();
			// �ָ�ԭ�����ύģʽ
			dbc.dbConn.setAutoCommit(autoCommit);
		} catch (SQLException e) { // ֻҪ������һ��sqlִ�д��󣬾�Ӧ�ûع�
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