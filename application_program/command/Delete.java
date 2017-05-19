package command;

import java.sql.*;

import DBConnect.DBEncryptConnection;
import DBConnect.KeyDBConnection;
import message_center.ServerMessage;

/**
 * delete语句并不需要加解密操作，所以是最简单的
 */
public class Delete extends Command {

	public Delete(String command) {
		super(command);
	}

	public String process(String para1, String para2, DBEncryptConnection dbec, KeyDBConnection kdbc, String name) {
		String res = ServerMessage.DELETESUCCESS;

		PreparedStatement pstmt = null;	
		try {
			pstmt = (PreparedStatement) dbec.dbConn.prepareStatement(command);
			pstmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return res;
	}
}