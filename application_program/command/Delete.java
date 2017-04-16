package command;

import java.sql.*;

import message_center.ServerMessage;
import server.DatabaseConnection;
import server.Server;

/**
 * delete��䲢����Ҫ�ӽ��ܲ�������������򵥵�
 */
public class Delete extends Command {

	public Delete(String command) {
		super(command);
	}

	public String process(String para1, String para2, DatabaseConnection dbc, String name) {
		String res = ServerMessage.DELETESUCCESS;

		int type = -1;
		// ���Ȳ��ȷ���Լ����û����ͣ������û�������ͨ�û���
		// ȷ�������Ҫɾ���ı��Ƿ����Լ��Ĺ����б���
		for (int i = 0; i < Server.userList.size(); i++) {
			if (Server.userList.get(i).getName().equals(name)) {
				type = Server.userList.get(i).getType();
				if (type == 1) { // �����û�
					// �ٴα��������û����鿴����������ĸ��û��Ĺ���ṹ��ȡ������û�����Կ������

				} else { // ��ͨ�û�
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

		return res;
	}
}