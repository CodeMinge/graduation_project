package key_manage;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import DBConnect.DBEncryptConnection;
import DBConnect.KeyDBConnection;

public class Key {
	private int keyld = -1;       // 
	private String keyData = null;
	private String VtData = null;
	
	// 根据密钥名称取得密钥id
	public Key(String keyName, DBEncryptConnection dbec) throws SQLException {
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql = "SELECT key_id from key_list where key_name = '" + keyName + "'";

		pstmt = (PreparedStatement) dbec.dbConn.prepareStatement(sql);
		rs = pstmt.executeQuery();
		while (rs.next()) {
			keyld = rs.getInt(1);
			break;
		}
	}
	
	public void destoryKey(KeyDBConnection kdbc) throws SQLException {
		PreparedStatement pstmt = null;
		String sql = "delete from [key_store] where key_id = " + keyld;
		pstmt = (PreparedStatement) kdbc.dbConn.prepareStatement(sql);
		pstmt.executeUpdate();
	}
	
	public int getKeyld() {
		return keyld;
	}

	public void setKeyld(int keyld) {
		this.keyld = keyld;
	}

	public String getKeyData() {
		return keyData;
	}

	public void setKeyData(String keyData) {
		this.keyData = keyData;
	}

	public String getVtData() {
		return VtData;
	}

	public void setVtData(String vtData) {
		VtData = vtData;
	}
}