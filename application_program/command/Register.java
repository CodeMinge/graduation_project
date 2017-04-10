package command;

import java.sql.*;

import key_manage.KeyManager;
import message_center.ServerMessage;
import server.DatabaseConnection;

public class Register extends Command {

	public Register(String command) {
		super(command);
	}

	/**
	 * ��¼�������
	 * 
	 * @param para1
	 *            �û���
	 * @param para2
	 *            ����
	 * @param dbc
	 * @return ��Ϣid
	 */
	public String process(String para1, String para2, DatabaseConnection dbc) {
		String res = ServerMessage.REGISTERSUCCESS;
		String sql = "SELECT password from [graduation_project].[dbo].[user_tb] where username = ?";

		PreparedStatement pstmt = null;
		try {
			boolean autoCommit = dbc.dbConn.getAutoCommit();
			// �ر��Զ��ύ����
			dbc.dbConn.setAutoCommit(false);

			pstmt = (PreparedStatement) dbc.dbConn.prepareStatement(sql);
			pstmt.setString(1, para1);
			ResultSet rs = pstmt.executeQuery();
			int count = 0;
			while (rs.next()) {
				count++;
			}

			if (count != 0) { // ������ͬ���û���
				res = ServerMessage.REGISTERFAIL;
			} else {

				// ������Կ��������Կ���뵽��Կ����
				KeyManager km = new KeyManager();
				km.save_KeyAndVector(para1, dbc);

				// ���÷������Կִ�м��ܲ���,temp1�Ǽ��ܺ�Ľ��
				String temp1 = "";
				sql = "SELECT [graduation_project].[dbo].[Des_Encrypt]('" + para2 + "', '" + km.key + "', '" + km.vector
						+ "')";
				pstmt = (PreparedStatement) dbc.dbConn.prepareStatement(sql);
				ResultSet rs2 = pstmt.executeQuery();
				while (rs2.next()) {
					temp1 = rs2.getString(1);
					break;
				}
				System.out.println(para2 + " " + temp1);

				// �����ܵ�������뵽�������
				sql = "insert into [graduation_project].[dbo].[user_tb] values (?,?,?)";
				pstmt = (PreparedStatement) dbc.dbConn.prepareStatement(sql);
				pstmt.setString(1, para1);
				pstmt.setString(2, temp1);
				pstmt.setInt(3, 0);
				int i = pstmt.executeUpdate();
				if (i == 0) {
					res = ServerMessage.REGISTERFAIL;
				}

				// ����һ�����û���ͬ���ı������洢�˸��û����ܹ���ı�������������򲻴���
				boolean b = false;
				sql = "select name from sysobjects where xtype='u'";
				pstmt = (PreparedStatement) dbc.dbConn.prepareStatement(sql);
				rs2 = pstmt.executeQuery();
				while (rs2.next()) {
					temp1 = rs2.getString(1);
					if (temp1.equals(para1)) {
						b = true;
					}
				}
				if (!b) {
					sql = "create table " + para1 + "(table_name varchar(20))";
					pstmt = (PreparedStatement) dbc.dbConn.prepareStatement(sql);
					pstmt.executeUpdate();
				}
			}

			// �ύ����
			dbc.dbConn.commit();
			// �ָ�ԭ�����ύģʽ
			dbc.dbConn.setAutoCommit(autoCommit);
		} catch (SQLException e) {
			res = ServerMessage.REGISTERFAIL;
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