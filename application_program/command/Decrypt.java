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
		// ���Ȳ��ȷ���Լ����û����ͣ������û�������ͨ�û���
		// ȷ�������Ҫ���ܵı��Ƿ����Լ��Ĺ����б���
		for (int i = 0; i < Server.userList.size(); i++) {
			if (Server.userList.get(i).getName().equals(name)) {
				type = Server.userList.get(i).getType();
				if (type == 1) { // �����û�
					// �ٴα��������û����鿴����������ĸ��û��Ĺ���ṹ��ȡ������û�����Կ������

				} else { // ��ͨ�û�
					key = Server.userList.get(i).getKey();
					vt = Server.userList.get(i).getVector();
//					System.out.println(key + " " + vt);
					if (!Server.userList.get(i).contain(para1)) {
						return ServerMessage.DECRYPTFAIL;
					}
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
			sql = "DELETE FROM message_tb WHERE tb_name = '" + para1 + "' and property = '" + para2 + "'";
			pstmt = (PreparedStatement) dbc.dbConn.prepareStatement(sql);
			pstmt.executeUpdate();

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