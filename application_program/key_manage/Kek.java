package key_manage;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import DBConnect.KeyDBConnection;

public class Kek {
	// private String keyld = null;// 加密后的密钥ID
	// private String keyData = null;// 加密后的密钥数据
	// private String VtData = null;// 加密后的向量数据

	private int kekld = -1;// 密钥加密密钥的ID
	private String rawKey = null;// 密钥原始数据
	private String rawVt = null;// 向量原始数据

	/**
	 * 系统初始化时需要将三属性加载进来
	 * @throws SQLException 
	 */
	public void initialize(KeyDBConnection kdbc) throws SQLException {
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql = "SELECT * from key_encrypt_key order by efficient_date DESC";

		pstmt = (PreparedStatement) kdbc.dbConn.prepareStatement(sql);
		rs = pstmt.executeQuery();
		while (rs.next()) {
			kekld = rs.getInt(1);
			rawKey = rs.getString(2);
			rawVt = rs.getString(3);
			break;
		}
	}

	/**
	 * 加密密钥/向量
	 */
	public String encryptKey(String target, KeyDBConnection kdbc) throws SQLException {
		String res = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql = "SELECT [dbo].[DESEncrypt]('" + target + "', '" + rawKey + "', '" + rawVt + "')";

		pstmt = (PreparedStatement) kdbc.dbConn.prepareStatement(sql);
		rs = pstmt.executeQuery();
		while (rs.next()) {
			res = rs.getString(1);
			break;
		}
		
		return res;
	}

	/**
	 * 解密密钥/向量
	 */
	public String decryptKey(String target, KeyDBConnection kdbc) throws SQLException {
		String res = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql = "SELECT [dbo].[DESDecrypt]('" + target + "', '" + rawKey + "', '" + rawVt + "')";

		pstmt = (PreparedStatement) kdbc.dbConn.prepareStatement(sql);
		rs = pstmt.executeQuery();
		while (rs.next()) {
			res = rs.getString(1);
			break;
		}
		
		return res;
	}

	public int getKekld() {
		return kekld;
	}

	public void setKekld(int kekld) {
		this.kekld = kekld;
	}

	public String getRawKey() {
		return rawKey;
	}

	public void setRawKey(String rawKey) {
		this.rawKey = rawKey;
	}

	public String getRawVt() {
		return rawVt;
	}

	public void setRawVt(String rawVt) {
		this.rawVt = rawVt;
	}
}