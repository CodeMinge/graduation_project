package key_manage;

import java.sql.SQLException;

import DBConnect.DBEncryptConnection;
import DBConnect.KeyDBConnection;
import server.Server;

/**
 * 密钥管理者
 */
public class KeyManager {
	
	KeyLocation kl = new KeyLocation();
	
	// 生成密钥加密密钥
	public int generate_KeyEncryptKey(DBEncryptConnection dbec, KeyDBConnection kdbc) {
		kl.generate_KeyEncryptKey(dbec, kdbc);
		
		return Server.kek.getKekld();
	}
	
	// 生成密钥
	public void generate_Key(String keyName, DBEncryptConnection dbec, KeyDBConnection kdbc) {
		kl.generate_Key(keyName, dbec, kdbc);
	}
	
	// 根据密钥名称取得密钥信息
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
	
	// 更换密钥加密密钥
	public void update_KeyEncryptKey(DBEncryptConnection dbec, KeyDBConnection kdbc) {
		kl.updateKek(dbec, kdbc);
	}
	
	// 查看密钥信息
	public String check_Key(String keyName, DBEncryptConnection dbec) {
		String res = null;
		try {
			res = kl.checkKey(keyName, dbec);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return res;
	}
	
	// 查看生效密钥
	public String check_Live_Key(DBEncryptConnection dbec) {
		String res = null;
		try {
			res = kl.checkLiveKey(dbec);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return res;
	}
	
	// 停用密钥
	public void stop_Key(String keyName, DBEncryptConnection dbec) {
		try {
			kl.stopKey(keyName, dbec);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	// 销毁密钥
	public void destory_Key(String keyName, DBEncryptConnection dbec, KeyDBConnection kdbc) {
		try {
			kl.destoryKey(keyName, dbec, kdbc);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}