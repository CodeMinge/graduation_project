package command;

import java.sql.*;

import message_center.ServerMessage;
import server.DatabaseConnection;

public class Select extends Command {

	public Select(String command) {
		super(command);
	}

	/*
	 * para1 -----列      para2 -----表
	 */
	public String process(String para1, String para2, DatabaseConnection dbc, String name) {
		String res = ServerMessage.SELECTSUCCESS;
		
		synchronized (workerTbLock) {
			String sql = command;
			
			//对表和列进行解析（不一定只有一个表、列）
			String [] col = para1.trim().split(",");
			String [] tb = para2.trim().split(",");
			
			//记录了列、是否加密、密码、向量
			String [][] col_msg = new String[col.length][4];
			//初始化
			for(int i = 0; i < col.length; i ++) {
				for(int j = 0; j < 4; j ++) {
					if(j == 0)
						col_msg[i][j] = col[i];
					else
						col_msg[i][j] = "n";
				}
			}
			
			PreparedStatement pstmt = null;
			ResultSet rs = null;
			try {
				boolean autoCommit = dbc.dbConn.getAutoCommit();
				// 关闭自动提交功能
				dbc.dbConn.setAutoCommit(false);
				
				// 根据表和列在message_tb上查找加密信息
				for(int i = 0; i < tb.length; i ++) {
					for(int j = 0; j < col.length; j ++) {
						String key = ""; // 密码
						String vt = ""; // 向量
						
						String sql1 = "SELECT * from [graduation_project].[dbo].[message_tb] where tb_name = '" + tb[i]
								+ "' and property = '" + col[j] + "'";
						
						pstmt = (PreparedStatement) dbc.dbConn.prepareStatement(sql1);
						rs = pstmt.executeQuery();
						while (rs.next()) { // 取得解密的密钥和向量
							key = rs.getString("secret_key");
							vt = rs.getString("vector");
							break; // 这里只能有一行
						}
						
						if (!key.equals("") && !vt.equals("")) { // 是敏感属性
							col_msg[j][1] = "y";
							col_msg[j][2] = key;
							col_msg[j][3] = vt;
						}
					}
				}
				
				for(int i = 0; i < col.length; i ++) {
					System.out.print(col_msg[i][0] + "\t");
				}
				
				System.out.println();
				
				pstmt = (PreparedStatement) dbc.dbConn.prepareStatement(sql);
				rs = pstmt.executeQuery();
				while (rs.next()) {
					String str = ""; 
					for(int i = 0; i < col.length; i ++) {
						if(col_msg[i][1].equals("n")) {   //没加密的
							str += rs.getString(col_msg[i][0]).trim() + "\t";
						} else {
							String target = rs.getString(col_msg[i][0]); 
							String sql1 = "SELECT [graduation_project].[dbo].[Des_Decrypt]('" + target + "', '" + col_msg[i][2] + "', '" + col_msg[i][3] 
									+ "')";
							pstmt = (PreparedStatement) dbc.dbConn.prepareStatement(sql1);
							ResultSet rs1 = pstmt.executeQuery();
							while (rs1.next()) {
								str += rs1.getString(1).trim() + "\t";
								break;
							}
						}
					}
					System.out.println(str);
				}
							
				// 提交事务
				dbc.dbConn.commit();
				// 恢复原来的提交模式
				dbc.dbConn.setAutoCommit(autoCommit);
			} catch (SQLException e) {
				res = ServerMessage.SELECTFAIL;
				try {
					// 回滚、取消前述操作
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
