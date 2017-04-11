package command;

import java.sql.*;

import message_center.ServerMessage;
import server.DatabaseConnection;

/**
 * delete语句并不需要加解密操作，所以是最简单的
 */
public class Delete extends Command {

	public Delete(String command) {
		super(command);
	}

	public String process(String para1, String para2, DatabaseConnection dbc, String name) {
		String res = ServerMessage.DELETESUCCESS;

		synchronized (workerTbLock) {
			String sql = command;

			PreparedStatement pstmt = null;
			try {
				boolean autoCommit = dbc.dbConn.getAutoCommit();
				// 关闭自动提交功能
				dbc.dbConn.setAutoCommit(false);

				pstmt = (PreparedStatement) dbc.dbConn.prepareStatement(sql);
				int i = pstmt.executeUpdate();
				if (i == 0)
					res = ServerMessage.DELETEFAIL;

				// 提交事务
				dbc.dbConn.commit();
				// 恢复原来的提交模式
				dbc.dbConn.setAutoCommit(autoCommit);
			} catch (SQLException e) {
				res = ServerMessage.DELETEFAIL;
				try {
					// 回滚、取消前述操作
					dbc.dbConn.rollback();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}

				e.printStackTrace();
			}
		}
		return res;
	}
}
