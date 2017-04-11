package command;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import message_center.ServerMessage;
import server.DatabaseConnection;

public class Drop_Table extends Command {
	public Drop_Table(String command) {
		super(command);
	}

	@Override
	public String process(String para1, String para2, DatabaseConnection dbc, String name) {
		String res = ServerMessage.DROPTABLESUCCESS;

		String sql = command;

		PreparedStatement pstmt = null;
		try {

			boolean autoCommit = dbc.dbConn.getAutoCommit();
			// 关闭自动提交功能
			dbc.dbConn.setAutoCommit(false);

			pstmt = (PreparedStatement) dbc.dbConn.prepareStatement(sql);
			pstmt.executeUpdate();

			// 提交事务
			dbc.dbConn.commit();
			// 恢复原来的提交模式
			dbc.dbConn.setAutoCommit(autoCommit);
		} catch (SQLException e) {
			res = ServerMessage.DROPTABLEFAIL;
		}

		// 用户创建表，我们要将表加入到用户的管理结构中
		if (res.equals(ServerMessage.DROPTABLESUCCESS)) {

		}

		return res;
	}
}