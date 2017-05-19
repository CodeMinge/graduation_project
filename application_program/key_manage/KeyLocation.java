package key_manage;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

import DBConnect.DBEncryptConnection;
import DBConnect.KeyDBConnection;
import server.Server;

/**
 * 密钥存储单元
 *
 */
public class KeyLocation {

	/**
	 * 生成一个新的密钥加密密钥，它不需要任何参数，返回值是新创建的KEK的ID(这个返回值现在其他地方没有用它的)。
	 */
	public void generate_KeyEncryptKey(DBEncryptConnection dbec, KeyDBConnection kdbc) {
		// String res = ServerMessage.GENKEKSUCCESS;

		int kekID = -1;

		KeyAndVector kav = new KeyAndVector();
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// 设置日期格式

		String key = kav.getKey();
		String vt = kav.getVector();

		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql = null;
		
		try {

			boolean autoCommit = kdbc.dbConn.getAutoCommit();
			// 关闭自动提交功能
			kdbc.dbConn.setAutoCommit(false);
			
			// 存储新的密钥加密密钥
			sql = "insert into key_encrypt_key(key_data, key_vt, efficient_date) values('" + key + "', '" + vt + "', '"
					+ df.format(new Date()) + "')";
			pstmt = (PreparedStatement) kdbc.dbConn.prepareStatement(sql);
			pstmt.executeUpdate();

			sql = "select key_encrypt_key_id from key_encrypt_key where key_data = '" + key + "' and key_vt = '" + vt
					+ "'";
			pstmt = (PreparedStatement) kdbc.dbConn.prepareStatement(sql);
			rs = pstmt.executeQuery();
			while (rs.next()) {
				kekID = rs.getInt(1);
			}
			
			if(kekID != 1) {
				// 利用旧的密钥解密密钥解密----作废旧密钥
				// 利用新的密钥重新加密
				sql = "select key_id, key_data, key_vt from key_store";
				pstmt = (PreparedStatement) kdbc.dbConn.prepareStatement(sql);
				rs = pstmt.executeQuery();
				while (rs.next()) {
					int id = rs.getInt(1);
					String data = Server.kek.decryptKey(rs.getString(2), kdbc);
					String vector = Server.kek.decryptKey(rs.getString(3), kdbc);
					String sql1 = "update key_store set key_data = '" + data + "', key_vt = '" + vector + "' where key_id = " + id;
					pstmt = (PreparedStatement) kdbc.dbConn.prepareStatement(sql1);
					pstmt.executeUpdate();
				}
				
				// 还得将用户表重新加密
				sql = "select userName, password from [user]";
				pstmt = (PreparedStatement) dbec.dbConn.prepareStatement(sql);
				rs = pstmt.executeQuery();
				while (rs.next()) {
					String name = rs.getString(1);
					String password = Server.kek.decryptKey(rs.getString(2), kdbc);
					String sql1 = "update [user] set password = '" + password + "' where userName = '" + name + "'";
					pstmt = (PreparedStatement) dbec.dbConn.prepareStatement(sql1);
					pstmt.executeUpdate();
				}
			}

			Server.kek.setKekld(kekID);
			Server.kek.setRawKey(key);
			Server.kek.setRawVt(vt);
			
			sql = "select key_id, key_data, key_vt from key_store";
			pstmt = (PreparedStatement) kdbc.dbConn.prepareStatement(sql);
			rs = pstmt.executeQuery();
			while (rs.next()) {
				int id = rs.getInt(1);
				String data = Server.kek.encryptKey(rs.getString(2), kdbc);
				String vector = Server.kek.encryptKey(rs.getString(3), kdbc);
				String sql1 = "update key_store set key_data = '" + data + "', key_vt = '" + vector + "' where key_id = " + id;
				pstmt = (PreparedStatement) kdbc.dbConn.prepareStatement(sql1);
				pstmt.executeUpdate();
			}
			
			// 还得将用户表重新加密
			sql = "select userName, password from [user]";
			pstmt = (PreparedStatement) dbec.dbConn.prepareStatement(sql);
			rs = pstmt.executeQuery();
			while (rs.next()) {
				String name = rs.getString(1);
				String password = Server.kek.encryptKey(rs.getString(2), kdbc);
				String sql1 = "update [user] set password = '" + password + "' where userName = '" + name + "'";
				pstmt = (PreparedStatement) dbec.dbConn.prepareStatement(sql1);
				pstmt.executeUpdate();
			}

			// 提交事务
			kdbc.dbConn.commit();
			// 恢复原来的提交模式
			kdbc.dbConn.setAutoCommit(autoCommit);
		} catch (SQLException e) {
			try {
				kdbc.dbConn.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			// res = ServerMessage.GENKEKFAIL;
		}

//		return kekID;
	}

	/**
	 * 生成一个新的密钥 keyName：密钥名称
	 */
	public void generate_Key(String keyName, DBEncryptConnection dbec, KeyDBConnection kdbc) {
		int keyID = -1;
		int kekID = Server.kek.getKekld();

		// 先分配密钥
		KeyAndVector kav = new KeyAndVector();
		String key = kav.getKey();
		String vt = kav.getVector();

		try {
			boolean autoCommit = kdbc.dbConn.getAutoCommit();
			// 关闭自动提交功能
			kdbc.dbConn.setAutoCommit(false);

			key = Server.kek.encryptKey(key, kdbc);
			vt = Server.kek.encryptKey(vt, kdbc);

			insertKeyStore(key, vt, kekID, kdbc);

			// 取得密钥标识
			keyID = getKeyID(key, vt, kdbc);
			
			// 我们会将第一个密钥设置为
//			if(keyID == 1)
				

			// 插入密钥列表
			insertKeyList(keyName, keyID, dbec);

			// 提交事务
			kdbc.dbConn.commit();
			// 恢复原来的提交模式
			kdbc.dbConn.setAutoCommit(autoCommit);
		} catch (SQLException e) {
			try {
				kdbc.dbConn.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			// res = ServerMessage.GENKEKFAIL;
		}
	}
	
	// 实质是根据key_id得到解密后的密钥和向量
	public Key getKey(Key key, KeyDBConnection kdbc) throws SQLException {
		String key_data = null;
		String key_vt = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql = "SELECT * from key_store where key_id = " + key.getKeyld();

		pstmt = (PreparedStatement) kdbc.dbConn.prepareStatement(sql);
		rs = pstmt.executeQuery();
		while (rs.next()) {
			key_data = rs.getString(2);
			key_vt = rs.getString(3);
			break;
		}
		
		key_data = Server.kek.decryptKey(key_data, kdbc);
		key_vt = Server.kek.decryptKey(key_vt, kdbc);
		
		key.setKeyData(key_data);
		key.setVtData(key_vt);
		
		return key;
	}

	// 更新密钥加密密钥
	public void updateKek(DBEncryptConnection dbec, KeyDBConnection kdbc) {
		generate_KeyEncryptKey(dbec, kdbc);
	}
	
	// 停用密钥
	public void stopKey(String keyName, DBEncryptConnection dbec) throws SQLException {
		KeyList kList = new KeyList(keyName);
		kList.stopKey(dbec);
	}
	
	public String checkLiveKey(DBEncryptConnection dbec) throws SQLException {
		KeyList kList = new KeyList();
		return kList.checkLiveKey(dbec);
	}
	
	public String checkKey(String keyName, DBEncryptConnection dbec) throws SQLException {
		KeyList kList = new KeyList(keyName);
		return kList.checkKey(dbec);
	}
	
	public void destoryKey(String keyName, DBEncryptConnection dbec, KeyDBConnection kdbc) throws SQLException {
		KeyList kList = new KeyList(keyName);
		Key key = new Key(keyName, dbec);
		kList.destoryKey(dbec);
		key.destoryKey(kdbc);
	}

	// 插入密钥库
	private void insertKeyStore(String key, String vt, int kekID, KeyDBConnection kdbc) throws SQLException {
		PreparedStatement pstmt = null;
		String sql = "insert into key_store(key_data, key_vt, key_encrypt_key_id) values('" + key + "', '" + vt + "', "
				+ kekID + ")";

		pstmt = (PreparedStatement) kdbc.dbConn.prepareStatement(sql);
		pstmt.executeUpdate();
	}

	// 取得密钥标识
	private int getKeyID(String key, String vt, KeyDBConnection kdbc) throws SQLException {
		int keyID = -1;
		String sql = "select key_id from key_store where key_data = '" + key + "' and key_vt = '" + vt + "'";
		PreparedStatement pstmt = (PreparedStatement) kdbc.dbConn.prepareStatement(sql);
		ResultSet rs = pstmt.executeQuery();
		while (rs.next()) {
			keyID = rs.getInt(1);
		}

		return keyID;
	}

	// 插入密钥列表
	private void insertKeyList(String keyName, int keyID, DBEncryptConnection dbec) throws SQLException {
		PreparedStatement pstmt = null;
		String sql = "insert into key_list values('" + keyName + "', 'true', NULL, " + keyID + ")";

		pstmt = (PreparedStatement) dbec.dbConn.prepareStatement(sql);
		pstmt.executeUpdate();
	}
}