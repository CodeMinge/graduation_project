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
	public static final String NOTABLE = "5"; // 无此用户表
	public static final String EXISTTABLE = "6"; // 存在此用户表
	public static final String NOPROPERTY = "7"; // 无此列
	public static final String EXISTPROPERTY = "8"; // 存在此列
	public static final String ENCRYPTSUCCESS = "9"; // 加密成功
	public static final String ENCRYPTFAIL = "10"; // 加密失败
	public static final String DECRYPTSUCCESS = "11"; // 解密成功
	public static final String DECRYPTFAIL = "12"; // 解密失败
	public static final String INSERTSUCCESS = "13"; // 插入成功
	public static final String INSERTFAIL = "14"; // 插入失败
	public static final String DELETESUCCESS = "15"; // 删除成功
	public static final String DELETEFAIL = "16"; // 删除失败
	public static final String UPDATESUCCESS = "17"; // 更新成功
	public static final String UPDATEFAIL = "18"; // 更新失败
	public static final String SELECTSUCCESS = "19"; // 查询成功
	public static final String SELECTFAIL = "20"; // 查询失败
	public static final String REGISTERSUCCESS = "21"; // 注册成功
	public static final String REGISTERFAIL = "22"; // 注册失败
	public static final String CREATETABLESUCCESS = "23"; // 创建表成功
	public static final String CREATETABLEFAIL = "24"; // 表已经存在
	public static final String DROPTABLESUCCESS = "25"; // 删除表成功
	public static final String DROPTABLEFAIL = "26"; // 表不存在
	public static final String GENKEKSUCCESS = "27"; // 创建密钥加密密钥成功
	public static final String GENKEKFAIL = "28"; // 创建密钥加密密钥失败
	public static final String EKEYSUCCESS = "29"; // 创建密钥成功
	public static final String EKEYFAIL = "30"; // 创建密钥失败
	public static final String NULL = "1000"; // 无信息

	public static final String[] ServerMessage = { 
			"用户将要退出", 
			"连接数据库成功", 
			"数据库连接失败，将检查配置", 
			"用户登录成功", 
			"用户登录失败",
			"无此用户表",
			"存在此用户表",
			"无此列",
			"存在此列",
			"加密成功",
			"加密失败",
			"解密成功",
			"解密失败",
			"插入成功",
			"插入失败",
			"删除成功",
			"删除失败",
			"更新成功",
			"更新失败",
			"查询成功",
			"查询失败",
			"注册成功",
			"注册失败",
			"创建表成功",
			"表已经存在",
			"删除表成功",
			"表不存在",
			"创建密钥加密密钥成功",
			"创建密钥加密密钥失败",
			"创建密钥成功",
			"创建密钥失败"
			};

	//并不是好的做法
	public static void ServerMessageOutput(String id) {
		if(id == null)
			return;
		
		if(Integer.parseInt(id) >= Integer.parseInt(QIUT) &&
				Integer.parseInt(id) <= Integer.parseInt(EKEYFAIL)) {
			System.out.println(ServerMessage[Integer.parseInt(id)]);
		}
	}
}
