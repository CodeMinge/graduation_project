package key_manage;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import server.DatabaseConnection;
import server.KeyAndVector;

/**
 * 密钥管理
 */
public class KeyManager {
	public String key = null; // 密钥
	public String vector = null; // 向量

	KeyAndVector kv = null;

	// 生成密钥
	private void create_KeyAndVector() {
		kv = new KeyAndVector();
		key = kv.getKey();
		vector = kv.getVector();
	}

	// 存储密钥
	public void save_KeyAndVector(String user, DatabaseConnection dbc) throws SQLException {
		create_KeyAndVector();

		String sql = "insert into user_kv VALUES(?,?,?,?,?)";

		PreparedStatement pstmt = null;

		pstmt = (PreparedStatement) dbc.dbConn.prepareStatement(sql);
		pstmt.setString(1, user);
		pstmt.setString(2, key.substring(0, 4));
		pstmt.setString(3, key.substring(4));
		pstmt.setString(4, vector.substring(0, 4));
		pstmt.setString(5, vector.substring(4));
		pstmt.executeUpdate();
	}

	// 获取密钥
//	private void get_KeyAndVector() {
//
//	}
}
