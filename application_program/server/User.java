package server;

import java.util.LinkedList;

/**
 * �û���ÿһ�������˵�client����һ���û� �����֮���Դ��ڣ�����Ϊ�������
 */
public class User {
	private String name; // �û���
	private String password; // ����
	private int type; // ���ͣ���ͨ�û��������û�
	private String key; // ��Կ
	private String vector; // ����
	private LinkedList<String> tbList = new LinkedList<String>(); // �û�������ı�

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getVector() {
		return vector;
	}

	public void setVector(String vector) {
		this.vector = vector;
	}

	public boolean add(String tb) {
		return tbList.add(tb);
	}

	public boolean romove(String tb) {
		return tbList.remove(tb);
	}

	public boolean contain(String tb) {
		return tbList.contains(tb);
	}
}
