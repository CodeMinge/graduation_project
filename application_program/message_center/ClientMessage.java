package message_center;

/**
 * 客户端信息中心
 */
public class ClientMessage {

	public static final String QIUT = "0"; // 退出信息
	public static final String DBSUCCESS = "1"; // 数据库连接成功
	public static final String DBFAIL = "2"; // 数据库连接失败
	public static final String LOGINSUCCESS = "3"; // 用户登录成功
	public static final String LOGINFAIL = "4"; // 用户登录失败
	public static final String NOTABLE = "5"; // 无此用户表
	public static final String EXISTTABLE = "6"; // 存在此用户表
	public static final String NOPROPERTY = "7"; // 无此列
	public static final String EXISTPROPERTY = "8"; // 存在此列
	public static final String ENCRYPTSUCCESS = "9"; // 加密成功
	public static final String ENCRYPTFAIL = "10"; // 加密失败
	public static final String DECRYPTSUCCESS = "11"; // 解密成功
	public static final String DECRYPTFAIL = "12"; // 解密失败
	public static final String NULL = "1000"; // 无信息

	public static final String[] ClientMessage = { 
			"正在退出", 
			"连接数据库成功，可作下一步操作", 
			"数据库连接失败，等待数据库连接", 
			"成功登录",
			"登录失败，检查用户名密码是否正确",
			"无此用户表",
			"存在此用户表",
			"无此列",
			"存在此列",
			"加密成功",
			"加密失败",
			"解密成功",
			"解密失败" 
			};

	//并不是好的做法
	public static void ClientMessageOutput(String id) {
		if(id == null)
			return;
		
		if(Integer.parseInt(id) >= Integer.parseInt(QIUT) &&
				Integer.parseInt(id) <= Integer.parseInt(DECRYPTFAIL)) {
			System.out.println(ClientMessage[Integer.parseInt(id)]);
		}
	}
}
