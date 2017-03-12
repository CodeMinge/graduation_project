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
		decryptSelfTest(client);
		
		
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
}
