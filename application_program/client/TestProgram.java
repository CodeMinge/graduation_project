package client;

/**
 *���Գ��� 
 */
public class TestProgram {
	
	/**
	 * ��������
	 */
	public static void interactTest() {
		Client client = new Client();
		
		while (true) {
			String temp = client.scanner.nextLine();// �Ӽ��̶�ȡһ��
			client.pw.println(temp);// д��������
			client.pw.flush();
			if (temp.equals("q")) {
				break;
			}
		}
	}

	/**
	 * �Բ����
	 */
	public static void selfTest() {
		Client client = new Client();

//		loginSelfTest(client);
//
//		try {
//			Thread.sleep(10000); // ��������֮��Ҫ��ͣ�٣���������������ͣ�ٽϳ�
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
	 * �˳������Բ����
	 */
	public static void quitSelfTest(Client client) {
		String temp = "q";
		client.pw.println(temp);// д��������
		client.pw.flush();
	}

	/**
	 * ��¼�����Բ����
	 */
	public static void loginSelfTest(Client client) {
		String temp = "login jd ����������";
		client.pw.println(temp);// д��������
		client.pw.flush();
	}

	/**
	 * ���ܶ������Գ���
	 */
	public static void encryptSelfTest(Client client) {
		String temp = "encrypt worker_table card_number";
		client.pw.println(temp);// д��������
		client.pw.flush();
	}

	/**
	 * ���ܶ������Գ���
	 */
	public static void decryptSelfTest(Client client) {
		String temp = "decrypt worker_table card_number";
		client.pw.println(temp);// д��������
		client.pw.flush();
	}

	/**
	 * ���ܽ��ܻ�ϲ��Գ��� ���������Ҫ�ǲ������������
	 * �������
	 */
	public static void encrypt_decrypt_mix_test() {
		Client c1 = new Client();
		Client c2 = new Client();
		String temp1 = "encrypt worker_table card_number";
		c1.pw.println(temp1);// д��������
		c1.pw.flush();
		
		try {
			Thread.sleep(800);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		String temp2 = "decrypt worker_table card_number";
		c2.pw.println(temp2);// д��������
		c2.pw.flush();
	}
}
