package command;

import java.sql.*;

import DBConnect.DBEncryptConnection;
import DBConnect.KeyDBConnection;
import message_center.ServerMessage;

/**
 * delete��䲢����Ҫ�ӽ��ܲ�������������򵥵�
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