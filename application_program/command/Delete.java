package command;

import java.sql.*;

import message_center.ServerMessage;
import server.DatabaseConnection;

/**
 * delete��䲢����Ҫ�ӽ��ܲ�������������򵥵�
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
				// �ر��Զ��ύ����
				dbc.dbConn.setAutoCommit(false);

				pstmt = (PreparedStatement) dbc.dbConn.prepareStatement(sql);
				int i = pstmt.executeUpdate();
				if (i == 0)
					res = ServerMessage.DELETEFAIL;

				// �ύ����
				dbc.dbConn.commit();
				// �ָ�ԭ�����ύģʽ
				dbc.dbConn.setAutoCommit(autoCommit);
			} catch (SQLException e) {
				res = ServerMessage.DELETEFAIL;
				try {
					// �ع���ȡ��ǰ������
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
