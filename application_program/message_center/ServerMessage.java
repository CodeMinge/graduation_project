package message_center;

/**
 * ��������Ϣ����
 */
public class ServerMessage {

	public static final String QIUT = "0"; // �˳���Ϣ
	public static final String DBSUCCESS = "1"; // ���ݿ����ӳɹ�
	public static final String DBFAIL = "2"; // ���ݿ�����ʧ��
	public static final String LOGINSUCCESS = "3"; // �û���¼�ɹ�
	public static final String LOGINFAIL = "4"; // �û���¼ʧ��
	public static final String NOTABLE = "5"; // �޴��û���
	public static final String EXISTTABLE = "6"; // ���ڴ��û���
	public static final String NOPROPERTY = "7"; // �޴���
	public static final String EXISTPROPERTY = "8"; // ���ڴ���
	public static final String ENCRYPTSUCCESS = "9"; // ���ܳɹ�
	public static final String ENCRYPTFAIL = "10"; // ����ʧ��
	public static final String DECRYPTSUCCESS = "11"; // ���ܳɹ�
	public static final String DECRYPTFAIL = "12"; // ����ʧ��
	public static final String NULL = "1000"; // ����Ϣ

	public static final String[] ServerMessage = { 
			"�û���Ҫ�˳�", 
			"�������ݿ�ɹ�", 
			"���ݿ�����ʧ�ܣ����������", 
			"�û���¼�ɹ�", 
			"�û���¼ʧ��",
			"�޴��û���",
			"���ڴ��û���",
			"�޴���",
			"���ڴ���",
			"���ܳɹ�",
			"����ʧ��",
			"���ܳɹ�",
			"����ʧ��"
			};

	//�����Ǻõ�����
	public static void ServerMessageOutput(String id) {
		if(id == null)
			return;
		
		if(Integer.parseInt(id) >= Integer.parseInt(QIUT) &&
				Integer.parseInt(id) <= Integer.parseInt(DECRYPTFAIL)) {
			System.out.println(ServerMessage[Integer.parseInt(id)]);
		}
	}
}
