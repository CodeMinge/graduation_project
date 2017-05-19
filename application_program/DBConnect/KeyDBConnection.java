package DBConnect;

import java.sql.Connection;
import java.sql.DriverManager;

import message_center.ServerMessage;

/**
 * ����KeyDB���ݿ⣬KeyDB����Կ��
 */
public class KeyDBConnection {
	public static final String DRIVERNAME = "com.microsoft.sqlserver.jdbc.SQLServerDriver";

	public static final String DBURL = "jdbc:sqlserver://localhost:1433;DatabaseName=KeyDB";

	public static final String LOGINNAME = "sa";

	public static final String LOGINPWD = "zxc1234";
	
	public static final int CONNECTION_COUNT = 5;

	public Connection dbConn = null;
	
	/*
	 * ���Ӳ��ɹ�������ʮ�εĳ��ԣ����������
	 */
	public String connectTen() {
		int count = 0;
		
		while (!connectOnce().equals(ServerMessage.DBSUCCESS)) {
			count ++;
			if(count > KeyDBConnection.CONNECTION_COUNT) {
				break;
			}
			
			//���������ݿ�Ļ����ȵȴ�һ��
			try {
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		if(count > KeyDBConnection.CONNECTION_COUNT)
			return ServerMessage.DBFAIL;
		else
			return ServerMessage.DBSUCCESS;
	}

	//����һ��
	public String connectOnce() {
		String res = ServerMessage.DBFAIL;
		try	{
			Class.forName(DRIVERNAME);
			dbConn = DriverManager.getConnection(DBURL, LOGINNAME, LOGINPWD);
			res = ServerMessage.DBSUCCESS;
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return res;
	}
}