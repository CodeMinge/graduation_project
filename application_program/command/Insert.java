package command;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import message_center.ServerMessage;
import server.DatabaseConnection;

public class Insert extends Command {

	// 存储列值对
	Map<String, String> col_value = new HashMap<String, String>();

	public Insert(String command) {
		super(command);
	}

	/**
	 * para1 -----表信息 para2 -----无作为
	 */
	public String process(String para1, String para2, DatabaseConnection dbc) {
		String res = ServerMessage.INSERTSUCCESS;

		String tb = getTb(para1);
		getVal(dbc, tb, command);

		synchronized (workerTbLock) {
			String sql = command;
			
			PreparedStatement pstmt = null;
			try {
				boolean autoCommit = dbc.dbConn.getAutoCommit();
				// 关闭自动提交功能
				dbc.dbConn.setAutoCommit(false);
				
				// 查看将修改的列是否为敏感属性
				// 采用Iterator遍历HashMap
				Iterator<String> it = col_value.keySet().iterator();
				while (it.hasNext()) {
					String col = (String) it.next(); // 列
					String value = col_value.get(col); // 值

					System.out.println("col:" + col); // 等位信息
					System.out.println("value:" + value); // 等位信息

					// 利用表和列在message_tb上查找加密信息
					String sql1 = "SELECT * from [graduation_project].[dbo].[message_tb] where tb_name = '" + tb
							+ "' and property = '" + col + "'";

					String key = ""; // 密码
					String vt = ""; // 向量

					pstmt = (PreparedStatement) dbc.dbConn.prepareStatement(sql1);
					ResultSet rs = pstmt.executeQuery();
					while (rs.next()) { // 取得解密的密钥和向量
						key = rs.getString("secret_key");
						vt = rs.getString("vector");
						break; // 这里只能有一行
					}

					if (!key.equals("") && !vt.equals("")) { // 是敏感属性
						sql1 = "SELECT [graduation_project].[dbo].[Des_Encrypt]('" + value + "', '" + key + "', '" + vt
								+ "')";
						pstmt = (PreparedStatement) dbc.dbConn.prepareStatement(sql1);
						rs = pstmt.executeQuery();
						while (rs.next()) {
							value = rs.getString(1);
							break;
						}
					}

					// 将更新的内容换到字符串里面
					sql = sql.replace(col_value.get(col), value);
				}

				pstmt = (PreparedStatement) dbc.dbConn.prepareStatement(sql);
				int i = pstmt.executeUpdate();
				if (i == 0)
					res = ServerMessage.INSERTFAIL;
				
				// 提交事务
				dbc.dbConn.commit();
				// 恢复原来的提交模式
				dbc.dbConn.setAutoCommit(autoCommit);
			} catch (SQLException e) {
				res = ServerMessage.INSERTFAIL;
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

	// 获得表
	private String getTb(String target) {
		String[] res = target.split(" ");
		return res[0].trim();
	}

	// 获得值
	private void getVal(DatabaseConnection dbc, String table, String target) {
		int valEnd = 0;
		int valStart = 0;
		int colEnd = 0; // 不一定存在
		int colStart = 0; // 不一定存在

		valEnd = target.lastIndexOf(")");
		valStart = target.lastIndexOf("(");

		colStart = target.indexOf("(");

		if (colStart == valStart) { //  INSERT INTO 表名称 VALUES (值1,值2,....)
			int i = 1;
			String[] val = target.substring(valStart + 1, valEnd).split(",");
			
			// 取得所有的列，插入到hashMap中
			// 这个sql命令的结果只有一列name，而列名从上到下排列
			String sql = "select name from syscolumns where id = object_id('graduation_project.dbo." + table + "')"; // 查询所有的列

			PreparedStatement pstmt = null;
			try {
				pstmt = (PreparedStatement) dbc.dbConn.prepareStatement(sql);
				ResultSet rs = pstmt.executeQuery();
				while (rs.next()) {
					String temp = rs.getString(1); // 列名
					col_value.put(temp, val[i].trim().substring(1, val[i].trim().length() - 1));
					i ++;
					if(i == val.length)
						i = 0;
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}

		} else { // INSERT INTO table_name (列1, 列2,...) VALUES (值1, 值2,....)
			colEnd = target.indexOf(")");
			
			// 取得对应的列，插入到hashMap中
			String[] col = target.substring(colStart, colEnd).split(",");
			String[] val = target.substring(valStart, valEnd).split(",");
			
			for(int i = 0; i < col.length; i ++) {
				col_value.put(col[i], val[i].trim().substring(1, val[1].length() - 2));
			}
		}
	}
}
