package server;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import message_center.ServerMessage;
/**
 * 先支持几个简单的命令行
 *  1、q --------退出
 *  2、login 用户名 密码 --------登录
 *  3、encrypt  table（表名） property（属性名） --------加密，实质是设置敏感属性
 *  4、decrypt  table（表名） property（属性名） -------解密
 */

public class Command {
	
	public static final String COMMAND_QIUT = "q";
	public static final String COMMAND_LOGIN = "login";
	public static final String COMMAND_ENCRYPT = "encrypt";
	public static final String COMMAND_DECRYPT = "decrypt";
	
	private String command = null;
	private KeyAndVector kav = new KeyAndVector(); //密钥向量生成器

	public void setCommand(String target) {
		command = target;
	}

	/**
	 * 对用户发送过来的命令做解析处理
	 * @param target  命令
	 * @param dbc
	 * @return 信息id
	 */
	public String process(String target, DatabaseConnection dbc) {
		// 处理命令行
		setCommand(target);
		String[] commandArr = command.split(" ");

		if (commandArr[0].equals(COMMAND_QIUT)) {
			return ServerMessage.QIUT;
		} else if (commandArr[0].equals(COMMAND_LOGIN)) {
			return loginProcess(commandArr[1], commandArr[2], dbc);
		} else if (commandArr[0].equals(COMMAND_ENCRYPT)) {
			return encryptProcess(commandArr[1], commandArr[2], dbc);
		} else if (commandArr[0].equals(COMMAND_DECRYPT)) {
	//		return decryptProcess(commandArr[1], commandArr[2], dbc);
		}
		
		return ServerMessage.NULL;
	}

	/**
	 * 登录命令解析
	 * @param userName 用户名
	 * @param password 密码
	 * @param dbc
	 * @return   信息id
	 */
	private String loginProcess(String userName, String password, DatabaseConnection dbc) {
		String res = ServerMessage.NULL;
		String sql = "SELECT password from [graduation_project].[dbo].[user_tb] where username = ?";

		PreparedStatement pstmt = null;
		try {
			pstmt = (PreparedStatement) dbc.dbConn.prepareStatement(sql);
			pstmt.setString(1, userName);
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				/*
				 * 这里有个问题，从数据取出来的数据后面有许多空格
				 * 本来我的想法是暴力砍掉后面所有的空格，因为我认为字符串后面有许多空格解密出来的东西应该是不同的
				 * 后经过验证发现，解密后也是原来的东西，所以先不砍掉后面所有的空格
				 */
				String temp = rs.getString(1);
				System.out.println(password + " t");
				System.out.println(temp + " t");
				
				sql = "SELECT [graduation_project].[dbo].[DESDecrypt]('"+ temp +"', '20111219', '12345678');";
				pstmt = (PreparedStatement) dbc.dbConn.prepareStatement(sql);
				ResultSet rs2 = pstmt.executeQuery();
				
				while(rs2.next()) {
					temp = rs2.getString(1);
					break;
				}
				
				System.out.println(password + " t");
				System.out.println(temp + " t");
				
				if(temp.equals(password)) {
					res = ServerMessage.LOGINSUCCESS;
					break;
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return res;
	}
	
	/**
	 * 加密信息解析，对某表的某列进行加密，某表的某列的前提是未经过加密
	 * @param table        表名
	 * @param column       列名
	 * @param dbc
	 * @return   信息id
	 */
	private String encryptProcess(String table, String property, DatabaseConnection dbc) {
		String res = ServerMessage.NULL;
		// 确保表和列存在
		res = tbExists(table, dbc);
		if(res.equals(ServerMessage.NOTABLE))
			return res;
		
		ServerMessage.ServerMessageOutput(ServerMessage.EXISTTABLE); //定位信息
		
		res = propertyExists(table, property, dbc);
		if(res.equals(ServerMessage.NOTCOL))
			return res;
		
		ServerMessage.ServerMessageOutput(ServerMessage.EXISTCOL); //定位信息
		
		res = ServerMessage.ENCRYPTSUCCESS;
	    // 首先取得密钥和向量
		String key = kav.getKey();
		String vt = kav.getVector();
		System.out.println(key + " " + vt);
		
		String sql = "SELECT * from [graduation_project].[dbo].[" + table + "]";

		PreparedStatement pstmt = null;
		try {
			pstmt = (PreparedStatement) dbc.dbConn.prepareStatement(sql);
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) { //不停地进行加密
				//取得加密数据
				String temp = rs.getString(1);    //这里，我的表必须是以第一列为主键，而且不能对第一列进行加密
				String target = rs.getString(property); //这是将要加密的数据
				System.out.println(temp + " " + target);
				
				//执行加密操作,temp1是加密后的结果
				String temp1 = "";
				sql = "SELECT [graduation_project].[dbo].[Des_Encrypt]('"+ target +"', '" + key + "', '" + vt + "')";
				pstmt = (PreparedStatement) dbc.dbConn.prepareStatement(sql);
				ResultSet rs2 = pstmt.executeQuery();
				while(rs2.next()) {
					temp1 = rs2.getString(1);
					break;
				}
				System.out.println(target + " " + temp1);
				
				//更新表，将加密后的内容更新到表中
				sql = "UPDATE [graduation_project].[dbo].["+ table + "] SET " + property + " = '"+  temp1 + "' WHERE " + property + " = '" + target + "'";
				pstmt = (PreparedStatement) dbc.dbConn.prepareStatement(sql);
				int i =  pstmt.executeUpdate();
				if(i == 0) {         //检测加密情况
					res = ServerMessage.ENCRYPTFAIL;
				}
			}
			//记录加密信息
			sql= "insert into message_tb(tb_name,property,algorithm,secret_key,vector) VALUES(?,?,?,?,?)";
			pstmt = (PreparedStatement) dbc.dbConn.prepareStatement(sql);
			pstmt.setString(1, table);
			pstmt.setString(2, property); 
			pstmt.setString(3, "des"); //现在只有des算法
			pstmt.setString(4, key); 
			pstmt.setString(5, vt); 
			pstmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return res;
	}
	
	/**
	 * 确认数据库中是否存在表
	 * @param table 表名
	 * @param dbc
	 * @return
	 */
	private String tbExists(String table, DatabaseConnection dbc) {
		String res = ServerMessage.NOTABLE;
		
		String sql = "select * from sys.tables"; //查询所有的表

		PreparedStatement pstmt = null;
		try {
			pstmt = (PreparedStatement) dbc.dbConn.prepareStatement(sql);			
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				String temp = rs.getString(1); // 取出的列与表的名称对应
				if(table.equals(temp)) {
					System.out.println(table + " " + temp);
					res = ServerMessage.EXISTTABLE;
					break;
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return res;
	}
	
	/**
	 * 确认表中是否存在某列
	 * @param table      表名
	 * @param property   列名
	 * @param dbc
	 * @return
	 */
	private String propertyExists(String table, String property, DatabaseConnection dbc) {
		String res = ServerMessage.NOTCOL;
		
		//这个sql命令的结果只有一列name，而列名从上到下排列
		String sql = "select name from syscolumns where id = object_id('graduation_project.dbo." + table + "')"; //查询所有的表

		PreparedStatement pstmt = null;
		try {
			pstmt = (PreparedStatement) dbc.dbConn.prepareStatement(sql);			
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				String temp = rs.getString(1); 
				if(property.equals(temp)) {
					System.out.println(property + " " + temp);
					res = ServerMessage.EXISTCOL;
					break;
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return res;
	}
}