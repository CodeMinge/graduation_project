package server;

import java.sql.*;

import message_center.ServerMessage;

/**
 * 连接数据库
 */
public class DatabaseConnection {
	public static final String DRIVERNAME = "com.microsoft.sqlserver.jdbc.SQLServerDriver";

	public static final String DBURL = "jdbc:sqlserver://localhost:1433;DatabaseName=graduation_project";

	public static final String LOGINNAME = "sa";

	public static final String LOGINPWD = "zxc1234";
	
	public static final int CONNECTION_COUNT = 10;

	public Connection dbConn = null;

	public String connect() {
		try	{
			Class.forName(DRIVERNAME);
			dbConn = DriverManager.getConnection(DBURL, LOGINNAME, LOGINPWD);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return ServerMessage.DBSUCCESS;
	}
}
