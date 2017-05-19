package DBConnect;

import java.sql.Connection;
import java.sql.DriverManager;

import message_center.ServerMessage;

/**
 * 连接KeyDB数据库，KeyDB是密钥库
 */
public class KeyDBConnection {
	public static final String DRIVERNAME = "com.microsoft.sqlserver.jdbc.SQLServerDriver";

	public static final String DBURL = "jdbc:sqlserver://localhost:1433;DatabaseName=KeyDB";

	public static final String LOGINNAME = "sa";

	public static final String LOGINPWD = "zxc1234";
	
	public static final int CONNECTION_COUNT = 5;

	public Connection dbConn = null;
	
	/*
	 * 连接不成功，会有十次的尝试，这个更合理
	 */
	public String connectTen() {
		int count = 0;
		
		while (!connectOnce().equals(ServerMessage.DBSUCCESS)) {
			count ++;
			if(count > KeyDBConnection.CONNECTION_COUNT) {
				break;
			}
			
			//连不上数据库的话，先等待一会
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

	//连接一次
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