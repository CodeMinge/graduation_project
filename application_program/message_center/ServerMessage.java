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
	public static final String INSERTSUCCESS = "13"; // ����ɹ�
	public static final String INSERTFAIL = "14"; // ����ʧ��
	public static final String DELETESUCCESS = "15"; // ɾ���ɹ�
	public static final String DELETEFAIL = "16"; // ɾ��ʧ��
	public static final String UPDATESUCCESS = "17"; // ���³ɹ�
	public static final String UPDATEFAIL = "18"; // ����ʧ��
	public static final String SELECTSUCCESS = "19"; // ��ѯ�ɹ�
	public static final String SELECTFAIL = "20"; // ��ѯʧ��
	public static final String REGISTERSUCCESS = "21"; // ע��ɹ�
	public static final String REGISTERFAIL = "22"; // ע��ʧ��
	public static final String CREATETABLESUCCESS = "23"; // ������ɹ�
	public static final String CREATETABLEFAIL = "24"; // ���Ѿ�����
	public static final String DROPTABLESUCCESS = "25"; // ɾ����ɹ�
	public static final String DROPTABLEFAIL = "26"; // ������
	public static final String GENKEKSUCCESS = "27"; // ������Կ������Կ�ɹ�
	public static final String GENKEKFAIL = "28"; // ������Կ������Կʧ��
	public static final String EKEYSUCCESS = "29"; // ������Կ�ɹ�
	public static final String EKEYFAIL = "30"; // ������Կʧ��
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
			"����ʧ��",
			"����ɹ�",
			"����ʧ��",
			"ɾ���ɹ�",
			"ɾ��ʧ��",
			"���³ɹ�",
			"����ʧ��",
			"��ѯ�ɹ�",
			"��ѯʧ��",
			"ע��ɹ�",
			"ע��ʧ��",
			"������ɹ�",
			"���Ѿ�����",
			"ɾ����ɹ�",
			"������",
			"������Կ������Կ�ɹ�",
			"������Կ������Կʧ��",
			"������Կ�ɹ�",
			"������Կʧ��"
			};

	//�����Ǻõ�����
	public static void ServerMessageOutput(String id) {
		if(id == null)
			return;
		
		if(Integer.parseInt(id) >= Integer.parseInt(QIUT) &&
				Integer.parseInt(id) <= Integer.parseInt(EKEYFAIL)) {
			System.out.println(ServerMessage[Integer.parseInt(id)]);
		}
	}
}
