package client;

/**
 *测试程序 
 */
public class TestProgram {
	
	/**
	 * 交互测试
	 */
	public static void interactTest() {
		Client client = new Client();
		
		while (true) {
			String temp = client.scanner.nextLine();// 从键盘读取一行
			client.pw.println(temp);// 写到服务器
			client.pw.flush();
			if (temp.equals("q")) {
				break;
			}
		}
	}

	/**
	 * 自测程序
	 */
	public static void selfTest() {
		Client client = new Client();

//		loginSelfTest(client);
//
//		try {
//			Thread.sleep(10000); // 两个测试之间要有停顿，否则出错，我这里的停顿较长
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}
//
//		quitSelfTest(client);
//
//		encryptSelfTest(client);
//		decryptSelfTest(client);
//		selectSelfTest(client);
//		deleteSelfTest(client);
//		insertSelfTest(client);
//		updateSelfTest(client);
	}

	/**
	 * 退出命令自测程序
	 */
	public static void quitSelfTest(Client client) {
		String temp = "q";
		client.pw.println(temp);// 写到服务器
		client.pw.flush();
	}

	/**
	 * 登录命令自测程序
	 */
	public static void loginSelfTest(Client client) {
		String temp = "login jd 金胖子死了";
		client.pw.println(temp);// 写到服务器
		client.pw.flush();
	}

	/**
	 * 加密独立测试程序
	 */
	public static void encryptSelfTest(Client client) {
		String temp = "encrypt worker_table card_number";
		client.pw.println(temp);// 写到服务器
		client.pw.flush();
	}

	/**
	 * 解密独立测试程序
	 */
	public static void decryptSelfTest(Client client) {
		String temp = "decrypt worker_table card_number";
		client.pw.println(temp);// 写到服务器
		client.pw.flush();
	}

	/**
	 * 加密解密混合测试程序 这个测试主要是测试锁和事务的
	 * 这个测试
	 */
	public static void encrypt_decrypt_mix_test() {
		Client c1 = new Client();
		Client c2 = new Client();
		String temp1 = "encrypt worker_table card_number";
		c1.pw.println(temp1);// 写到服务器
		c1.pw.flush();
		
		try {
			Thread.sleep(800);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		String temp2 = "decrypt worker_table card_number";
		c2.pw.println(temp2);// 写到服务器
		c2.pw.flush();
	}
	
	/**
	 * 查询测试程序
	 */
	public static void selectSelfTest(Client client) {
		String temp = "select card_number,salary  from    worker_table";
		client.pw.println(temp);// 写到服务器
		client.pw.flush();
	}
	
	/**
	 * 删除测试程序
	 */
	public static void deleteSelfTest(Client client) {
		String temp = "delete from worker_table where worker_id='010'";
		client.pw.println(temp);// 写到服务器
		client.pw.flush();
	}
	
	/**
	 * 插入测试程序
	 */
	public static void insertSelfTest(Client client) {
		String temp = "INSERT INTO worker_table VALUES('011', '440104', '7000')"; // (worker_id, card_number, salary)
		client.pw.println(temp);// 写到服务器
		client.pw.flush();
	}
	
	/**
	 * 更新测试程序
	 */
	public static void updateSelfTest(Client client) {
		String temp = "UPDATE worker_table SET card_number = '440102', salary = '2000' WHERE worker_id = '009'";
		client.pw.println(temp);// 写到服务器
		client.pw.flush();
	}
	
	/**
	 * 创建表测试程序
	 */
	public static void createTableSelfTest(Client client) {
		String temp = "create table yy(table_name varchar(20))";
		client.pw.println(temp);// 写到服务器
		client.pw.flush();
	}
}
