package key_manage;

import java.sql.*;

import DBConnect.DBEncryptConnection;

public class KeyList {
	private String keyName = null;
	private String keyState = null;
	private Date date = null;
	private int keyID = -1;

	public KeyList() {
	}

	public KeyList(String keyName) {
		this.keyName = keyName;
	}

	public void stopKey(DBEncryptConnection dbec) throws SQLException {
		PreparedStatement pstmt = null;
		String sql = "UPDATE [key_list] SET key_state = 'false' where key_name = '" + keyName + "'";
		pstmt = (PreparedStatement) dbec.dbConn.prepareStatement(sql);
		pstmt.executeUpdate();
	}

	public String checkLiveKey(DBEncryptConnection dbec) throws SQLException {
		String res = "";
		PreparedStatement pstmt = null;
		String sql = "select key_name from [key_list] where key_state = 'true'";
		pstmt = (PreparedStatement) dbec.dbConn.prepareStatement(sql);
		ResultSet rs = pstmt.executeQuery();
		while (rs.next()) {
			res += rs.getString(1) + " ";
		}

		return res;
	}

	public String checkKey(DBEncryptConnection dbec) throws SQLException {
		PreparedStatement pstmt = null;
		String sql = "select * from [key_list] where key_name = '" + keyName + "'";
		pstmt = (PreparedStatement) dbec.dbConn.prepareStatement(sql);
		ResultSet rs = pstmt.executeQuery();
		while (rs.next()) {
			keyState = rs.getString(2);
			date = rs.getDate(3);
			keyID = rs.getInt(4);
		}

		return Output();
	}

	private String Output() {
		return "key_name:" + keyName + " key_state:" + keyState + " efficient_date:" + date + " key_id:" + keyID;
	}

	public void destoryKey(DBEncryptConnection dbec) throws SQLException {
		PreparedStatement pstmt = null;
		String sql = "delete from [key_list] where key_name = '" + keyName + "'";
		pstmt = (PreparedStatement) dbec.dbConn.prepareStatement(sql);
		pstmt.executeUpdate();
	}
}