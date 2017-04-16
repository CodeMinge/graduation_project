package command;

import java.sql.*;

import message_center.ServerMessage;
import server.DatabaseConnection;
import server.Server;

/**
 * delete语句并不需要加解密操作，所以是最简单的
 */
public class Delete extends Command {

	public Delete(String command) {
		super(command);
	}

	public String process(String para1, String para2, DatabaseConnection dbc, String name) {
		String res = ServerMessage.DELETESUCCESS;

		int type = -1;
		// 首先查表确定自己的用户类型（超级用户或者普通用户）
		// 确定这个将要删除的表是否在自己的管理列表中
		for (int i = 0; i < Server.userList.size(); i++) {
			if (Server.userList.get(i).getName().equals(name)) {
				type = Server.userList.get(i).getType();
				if (type == 1) { // 超级用户
					// 再次遍历所有用户，查看这个表是在哪个用户的管理结构中取得这个用户的密钥和向量

				} else { // 普通用户
					if (!Server.userList.get(i).contain(para1)) {
						return ServerMessage.DELETEFAIL;
					}
				}
			}
		}

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

		return res;
	}
}