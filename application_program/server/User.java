package server;

import java.util.LinkedList;

/**
 * 用户，每一个连上了的client都是一个用户 这个类之所以存在，是因为方便管理
 */
public class User {
	private String name; // 用户名
	private String password; // 密码
	private int type; // 类型：普通用户、超级用户
	private String key; // 密钥
	private String vector; // 向量
	private LinkedList<String> tbList = new LinkedList<String>(); // 用户所管理的表

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
