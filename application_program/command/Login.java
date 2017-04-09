package command;

import java.sql.*;

import message_center.ServerMessage;
import server.DatabaseConnection;
import server.Server;
import server.User;

public class Login extends Command {

	public Login(String command) {
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
		String res = ServerMessage.LOGINFAIL;
		String sql = "SELECT * from [graduation_project].[dbo].[user_kv] where [user] = '" + para1 + "'";
		
		User user = new User();
		user.setName(para1);

		PreparedStatement pstmt = null;
		try {
			boolean autoCommit = dbc.dbConn.getAutoCommit();
			// �ر��Զ��ύ����
			dbc.dbConn.setAutoCommit(false);

			pstmt = (PreparedStatement) dbc.dbConn.prepareStatement(sql);
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				int k1 = rs.getInt(2);
				int k2 = rs.getInt(3);
				int v1 = rs.getInt(4);
				int v2 = rs.getInt(5);
				
				user.setKey(k1 + "" + k2);
				user.setVector(v1 + "" + v2);
				break;
			}
			
			sql = "SELECT password,usertype from [graduation_project].[dbo].[user_tb] where username = ?";
			pstmt = (PreparedStatement) dbc.dbConn.prepareStatement(sql);
			pstmt.setString(1, para1);
			rs = pstmt.executeQuery();
			while (rs.next()) {
				String password = rs.getString(1);
				int usertype = rs.getInt(2);
				
				user.setPassword(password);
				user.setType(usertype);
				break;
			}
			// ��������н��ܣ�ִ�н��ܲ�����temp1�ǽ��ܺ�Ľ��
			String temp1 = "";
			sql = "SELECT [graduation_project].[dbo].[Des_Decrypt]('" + user.getPassword() + "', '" + user.getKey() + "', '" + user.getVector()
					+ "')";
			pstmt = (PreparedStatement) dbc.dbConn.prepareStatement(sql);
			ResultSet rs2 = pstmt.executeQuery();
			while (rs2.next()) {
				temp1 = rs2.getString(1);
				break;
			}
			user.setPassword(temp1);
			
			// ���û�����ı�ӵ�list��
			sql = "SELECT * from [graduation_project].[dbo].["+ para1 +"]";
			pstmt = (PreparedStatement) dbc.dbConn.prepareStatement(sql);
			rs2 = pstmt.executeQuery();
			while (rs2.next()) {
				temp1 = rs2.getString(1);
				user.add(temp1);
			}
			
			// ��user�ӵ�userList��
			Server.userList.add(user);

			// �ύ����
			dbc.dbConn.commit();
			// �ָ�ԭ�����ύģʽ
			dbc.dbConn.setAutoCommit(autoCommit);
		} catch (SQLException e) {
			res = ServerMessage.LOGINFAIL;
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
