package command;

import java.sql.*;

import DBConnect.DBEncryptConnection;
import DBConnect.KeyDBConnection;
import message_center.ServerMessage;
import server.Server;

public class Login extends Command {

	public Login(String command) {
		super(command);
	}

	@Override
	public String process(String para1, String para2, DBEncryptConnection dbec, KeyDBConnection kdbc, String name) {
		String res = ServerMessage.LOGINFAIL;

		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql = null;

		try {
			sql = "select password from [user] where userName = '" + para1 + "'";
			pstmt = (PreparedStatement) dbec.dbConn.prepareStatement(sql);
			rs = pstmt.executeQuery();
			while (rs.next()) {
				if (para2.equals(Server.kek.decryptKey(rs.getString(1), kdbc))) {
					res = ServerMessage.LOGINSUCCESS;
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return res;
	}
}