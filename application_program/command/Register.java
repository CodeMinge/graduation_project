package command;

import java.sql.*;

import DBConnect.DBEncryptConnection;
import DBConnect.KeyDBConnection;
import message_center.ServerMessage;
import server.Server;

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
	public String process(String para1, String para2, DBEncryptConnection dbec, KeyDBConnection kdbc, String name) {
		String res = ServerMessage.REGISTERSUCCESS;

		PreparedStatement pstmt = null;
		String sql = null;

		try {
			String pwd = Server.kek.encryptKey(para2, kdbc);

			sql = "insert into [user] values('" + para1 + "', '" + pwd + "')";
			pstmt = (PreparedStatement) dbec.dbConn.prepareStatement(sql);
			pstmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return res;
	}
}