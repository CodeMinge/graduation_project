package message_center;

/**
 * 服务器信息中心
 */
public class ServerMessage {

	public static final String QIUT = "0"; // 退出信息
	public static final String DBSUCCESS = "1"; // 数据库连接成功
	public static final String DBFAIL = "2"; // 数据库连接失败
	public static final String LOGINSUCCESS = "3"; // 用户登录成功
	public static final String LOGINFAIL = "4"; // 用户登录失败
	public static final String NULL = "1000"; // 无信息

	public static final String[] ServerMessage = { 
			"用户将要退出", 
			"连接数据库成功", 
			"数据库连接失败，将检查配置", 
			"用户登录成功", 
			"用户登录失败" 
			};

	//并不是好的做法
	public static void ServerMessageOutput(String id) {
		if(id == null)
			return;
		
		if(Integer.parseInt(id) >= Integer.parseInt(QIUT) &&
				Integer.parseInt(id) <= Integer.parseInt(LOGINFAIL)) {
			System.out.println(ServerMessage[Integer.parseInt(id)]);
		}
	}
}
