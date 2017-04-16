package command;

import java.sql.*;

import message_center.ServerMessage;
import server.DatabaseConnection;
import server.Server;

public class Encrypt extends Command {

	public Encrypt(String command) {
		super(command);
	}

	/**
	 * ������Ϣ��������ĳ���ĳ�н��м��ܣ�ĳ���ĳ�е�ǰ����δ��������
	 * 
	 * @param para1
	 *            ����
	 * @param para2
	 *            ����
	 * @param dbc
	 * @return ��Ϣid
	 */
	public String process(String para1, String para2, DatabaseConnection dbc, String name) {
		// String res = ServerMessage.NULL;
		// ȷ������д���//���������Խ������ݿ�����
		// res = tbExists(para1, dbc);
		// if (res.equals(ServerMessage.NOTABLE))
		// return res;
		//
		// ServerMessage.ServerMessageOutput(ServerMessage.EXISTTABLE); // ��λ��Ϣ
		//
		// res = propertyExists(para1, para2, dbc);
		// if (res.equals(ServerMessage.NOPROPERTY))
		// return res;

		// ServerMessage.ServerMessageOutput(ServerMessage.EXISTPROPERTY); //
		// ��λ��Ϣ

		String res = ServerMessage.ENCRYPTSUCCESS;

		int type = -1;
		String key = null;
		String vt = null;
		// ���Ȳ��ȷ���Լ����û����ͣ������û�������ͨ�û���
		for (int i = 0; i < Server.userList.size(); i++) {
			if (Server.userList.get(i).getName().equals(name)) {
				type = Server.userList.get(i).getType();
				if (type == 1) { // �����û�
					// �ٴα��������û����鿴����������ĸ��û��Ĺ���ṹ��ȡ������û�����Կ������

				} else { // ��ͨ�û�
					key = Server.userList.get(i).getKey();
					vt = Server.userList.get(i).getVector();
//					System.out.println(key + " " + vt);
				}
			}
		}

		// ȡ��Ҫ���ܵ����ݵ��������ͣ���ѡ������㷨
		String sql = "select a.name ����,b.name �ֶ���,c.name �ֶ�����,c.length �ֶγ��� "
				+ " from sysobjects a,syscolumns b,systypes c" + " where a.id=b.id and a.name='" + para1
				+ "' and a.xtype='U' and b.xtype=c.xtype";

		PreparedStatement pstmt = null;
		try {

			boolean autoCommit = dbc.dbConn.getAutoCommit();
			// �ر��Զ��ύ����
			dbc.dbConn.setAutoCommit(false);

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
			while (rs.next()) { // ��ͣ�ؽ��м���
				// ȡ�ü�������
//				String temp = rs.getString(1); // ����ҵı�������Ե�һ��Ϊ���������Ҳ��ܶԵ�һ�н��м���
				String target = rs.getString(para2); // ���ǽ�Ҫ���ܵ�����
//				System.out.println(temp + " " + target);

				String tempString = "";
				int tempInt = 0;
				// ִ�м��ܲ���,temp1�Ǽ��ܺ�Ľ��
				if (dataType.equals("char") || dataType.equals("varchar") || dataType.equals("nchar")
						|| dataType.equals("nvarchar")) {
					sql = "SELECT [graduation_project].[dbo].[Des_Encrypt]('" + target + "', '" + key + "', '" + vt + "')";
					pstmt = (PreparedStatement) dbc.dbConn.prepareStatement(sql);
					ResultSet rs2 = pstmt.executeQuery();
					while (rs2.next()) {
						tempString = rs2.getString(1);
						break;
					}
//					System.out.println(target + " " + tempString);
					
					// ���±������ܺ�����ݸ��µ�����
					sql = "UPDATE [graduation_project].[dbo].[" + para1 + "] SET " + para2 + " = '" + tempString + "' WHERE "
							+ para2 + " = '" + target + "'";
					pstmt = (PreparedStatement) dbc.dbConn.prepareStatement(sql);
					int i = pstmt.executeUpdate();
					if (i == 0) { // ���������
						res = ServerMessage.ENCRYPTFAIL;
					}
				} else if(dataType.equals("int")) {
					sql = "SELECT [graduation_project].[dbo].[INTEncrypt](" + target + "," + key + ")";
					pstmt = (PreparedStatement) dbc.dbConn.prepareStatement(sql);
					ResultSet rs2 = pstmt.executeQuery();
					while (rs2.next()) {
						tempInt = rs2.getInt(1);
						break;
					}
					System.out.println(target + " " + tempInt);
					
					// ���±������ܺ�����ݸ��µ�����
					sql = "UPDATE [graduation_project].[dbo].[" + para1 + "] SET " + para2 + " = " + tempInt + " WHERE "
							+ para2 + " = " + target + "";
					pstmt = (PreparedStatement) dbc.dbConn.prepareStatement(sql);
					int i = pstmt.executeUpdate();
					if (i == 0) { // ���������
						res = ServerMessage.ENCRYPTFAIL;
					}
				}
			}
			// ��¼������Ϣ
			sql = "insert into message_tb(tb_name,property) VALUES(?,?)";
			pstmt = (PreparedStatement) dbc.dbConn.prepareStatement(sql);
			pstmt.setString(1, para1);
			pstmt.setString(2, para2);
			pstmt.executeUpdate();

			// �ύ����
			dbc.dbConn.commit();
			// �ָ�ԭ�����ύģʽ
			dbc.dbConn.setAutoCommit(autoCommit);
		} catch (SQLException e) { // ֻҪ������һ��sqlִ�д��󣬾�Ӧ�ûع�
			res = ServerMessage.ENCRYPTFAIL;
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