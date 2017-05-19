package key_manage;

import java.sql.SQLException;

import DBConnect.DBEncryptConnection;
import DBConnect.KeyDBConnection;
import server.Server;

/**
 * ��Կ������
 */
public class KeyManager {
	
	KeyLocation kl = new KeyLocation();
	
	// ������Կ������Կ
	public int generate_KeyEncryptKey(DBEncryptConnection dbec, KeyDBConnection kdbc) {
		kl.generate_KeyEncryptKey(dbec, kdbc);
		
		return Server.kek.getKekld();
	}
	
	// ������Կ
	public void generate_Key(String keyName, DBEncryptConnection dbec, KeyDBConnection kdbc) {
		kl.generate_Key(keyName, dbec, kdbc);
	}
	
	// ������Կ����ȡ����Կ��Ϣ
	public Key getKey(String keyName, DBEncryptConnection dbec, KeyDBConnection kdbc) {
		Key key = null;
		try {
			key = new Key(keyName, dbec);
			key = kl.getKey(key, kdbc);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return key;
	}
	
	// ������Կ������Կ
	public void update_KeyEncryptKey(DBEncryptConnection dbec, KeyDBConnection kdbc) {
		kl.updateKek(dbec, kdbc);
	}
	
	// �鿴��Կ��Ϣ
	public String check_Key(String keyName, DBEncryptConnection dbec) {
		String res = null;
		try {
			res = kl.checkKey(keyName, dbec);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return res;
	}
	
	// �鿴��Ч��Կ
	public String check_Live_Key(DBEncryptConnection dbec) {
		String res = null;
		try {
			res = kl.checkLiveKey(dbec);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return res;
	}
	
	// ͣ����Կ
	public void stop_Key(String keyName, DBEncryptConnection dbec) {
		try {
			kl.stopKey(keyName, dbec);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	// ������Կ
	public void destory_Key(String keyName, DBEncryptConnection dbec, KeyDBConnection kdbc) {
		try {
			kl.destoryKey(keyName, dbec, kdbc);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}