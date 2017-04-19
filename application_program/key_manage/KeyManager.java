package key_manage;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import server.DatabaseConnection;
import server.KeyAndVector;

/**
 * √‹‘øπ‹¿Ì
 */
public class KeyManager {
	public String key = null; // √‹‘ø
	public String vector = null; // œÚ¡ø

	KeyAndVector kv = null;

	// …˙≥…√‹‘ø
	private void create_KeyAndVector() {
		kv = new KeyAndVector();
		key = kv.getKey();
		vector = kv.getVector();
	}

	// ¥Ê¥¢√‹‘ø
	public void save_KeyAndVector(String user, DatabaseConnection dbc) throws SQLException {
		create_KeyAndVector();

		String sql = "insert into user_kv VALUES(?,?,?,?,?)";

		PreparedStatement pstmt = null;

		pstmt = (PreparedStatement) dbc.dbConn.prepareStatement(sql);
		pstmt.setString(1, user);
		pstmt.setInt(2, Integer.parseInt(key.substring(0, 4)));
		pstmt.setInt(3, Integer.parseInt(key.substring(4)));
		pstmt.setInt(4, Integer.parseInt(vector.substring(0, 4)));
		pstmt.setInt(5, Integer.parseInt(vector.substring(4)));
		pstmt.executeUpdate();
	}
}