package command;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import DBConnect.DBEncryptConnection;
import DBConnect.KeyDBConnection;
import message_center.ServerMessage;

/**
 * É¾³ý±í
 */
public class Drop_Table extends Command {
	public Drop_Table(String command) {
		super(command);
	}

	@Override
	public String process(String para1, String para2, DBEncryptConnection dbec, KeyDBConnection kdbc, String name) {
		String res = ServerMessage.DROPTABLESUCCESS;

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