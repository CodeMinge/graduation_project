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
			// �ر��Զ��ύ����
			dbc.dbConn.setAutoCommit(false);

			pstmt = (PreparedStatement) dbc.dbConn.prepareStatement(sql);
			pstmt.executeUpdate();

			// �ύ����
			dbc.dbConn.commit();
			// �ָ�ԭ�����ύģʽ
			dbc.dbConn.setAutoCommit(autoCommit);
		} catch (SQLException e) {
			res = ServerMessage.DROPTABLEFAIL;
		}

		// �û�����������Ҫ������뵽�û��Ĺ���ṹ��
		if (res.equals(ServerMessage.DROPTABLESUCCESS)) {

		}

		return res;
	}
}