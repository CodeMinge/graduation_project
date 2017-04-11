package command;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import message_center.ServerMessage;
import server.DatabaseConnection;
import server.Server;
import server.User;

/**
 * ������
 */
public class Create_Table extends Command {
	public Create_Table(String command) {
		super(command);
	}

	@Override
	public String process(String para1, String para2, DatabaseConnection dbc, String name) {
		String res = ServerMessage.CREATETABLESUCCESS;

		String sql = command;
		
		int index = para2.indexOf("(");
		String tb = para2.substring(0, index);

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
			res = ServerMessage.CREATETABLEFAIL;
		}
		
		// �û�����������Ҫ������뵽�û��Ĺ���ṹ��
		if(res.equals(ServerMessage.CREATETABLESUCCESS)) {
			for(int i = 0; i < Server.userList.size(); i ++) {
				if(Server.userList.get(i).getName().equals(name)) {
					User user = new User(Server.userList.get(i));
					Server.userList.remove(i);
					user.add(tb);
					Server.userList.add(user);
					break;
				}
			}
		}

		return res;
	}
}
