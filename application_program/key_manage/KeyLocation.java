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
 * ��Կ�洢��Ԫ
 *
 */
public class KeyLocation {

	/**
	 * ����һ���µ���Կ������Կ��������Ҫ�κβ���������ֵ���´�����KEK��ID(�������ֵ���������ط�û��������)��
	 */
	public void generate_KeyEncryptKey(DBEncryptConnection dbec, KeyDBConnection kdbc) {
		// String res = ServerMessage.GENKEKSUCCESS;

		int kekID = -1;

		KeyAndVector kav = new KeyAndVector();
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// �������ڸ�ʽ

		String key = kav.getKey();
		String vt = kav.getVector();

		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql = null;
		
		try {

			boolean autoCommit = kdbc.dbConn.getAutoCommit();
			// �ر��Զ��ύ����
			kdbc.dbConn.setAutoCommit(false);
			
			// �洢�µ���Կ������Կ
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
				// ���þɵ���Կ������Կ����----���Ͼ���Կ
				// �����µ���Կ���¼���
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
				
				// ���ý��û������¼���
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
			
			// ���ý��û������¼���
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

			// �ύ����
			kdbc.dbConn.commit();
			// �ָ�ԭ�����ύģʽ
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
	 * ����һ���µ���Կ keyName����Կ����
	 */
	public void generate_Key(String keyName, DBEncryptConnection dbec, KeyDBConnection kdbc) {
		int keyID = -1;
		int kekID = Server.kek.getKekld();

		// �ȷ�����Կ
		KeyAndVector kav = new KeyAndVector();
		String key = kav.getKey();
		String vt = kav.getVector();

		try {
			boolean autoCommit = kdbc.dbConn.getAutoCommit();
			// �ر��Զ��ύ����
			kdbc.dbConn.setAutoCommit(false);

			key = Server.kek.encryptKey(key, kdbc);
			vt = Server.kek.encryptKey(vt, kdbc);

			insertKeyStore(key, vt, kekID, kdbc);

			// ȡ����Կ��ʶ
			keyID = getKeyID(key, vt, kdbc);
			
			// ���ǻὫ��һ����Կ����Ϊ
//			if(keyID == 1)
				

			// ������Կ�б�
			insertKeyList(keyName, keyID, dbec);

			// �ύ����
			kdbc.dbConn.commit();
			// �ָ�ԭ�����ύģʽ
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
	
	// ʵ���Ǹ���key_id�õ����ܺ����Կ������
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

	// ������Կ������Կ
	public void updateKek(DBEncryptConnection dbec, KeyDBConnection kdbc) {
		generate_KeyEncryptKey(dbec, kdbc);
	}
	
	// ͣ����Կ
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

	// ������Կ��
	private void insertKeyStore(String key, String vt, int kekID, KeyDBConnection kdbc) throws SQLException {
		PreparedStatement pstmt = null;
		String sql = "insert into key_store(key_data, key_vt, key_encrypt_key_id) values('" + key + "', '" + vt + "', "
				+ kekID + ")";

		pstmt = (PreparedStatement) kdbc.dbConn.prepareStatement(sql);
		pstmt.executeUpdate();
	}

	// ȡ����Կ��ʶ
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

	// ������Կ�б�
	private void insertKeyList(String keyName, int keyID, DBEncryptConnection dbec) throws SQLException {
		PreparedStatement pstmt = null;
		String sql = "insert into key_list values('" + keyName + "', 'true', NULL, " + keyID + ")";

		pstmt = (PreparedStatement) dbec.dbConn.prepareStatement(sql);
		pstmt.executeUpdate();
	}
}