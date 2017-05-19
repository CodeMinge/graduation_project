package key_manage;

import java.util.*;

/**
 * 密钥和向量的生成，两者都得8位
 */
// 密钥向量生成器
public class KeyAndVector {
	private static final int KEYANDVECTORLENGTH = 8;
	private int[] key = new int[KEYANDVECTORLENGTH];
	private int[] vector = new int[KEYANDVECTORLENGTH];
	private Random ran = new Random();

	public String getKey() {
		String str = "";

		for (int i = 0; i < key.length; i++) {
			key[i] = ran.nextInt(10); // 生成随机数[0,10)
			System.out.print(key[i]);
		}

		for (int i = 0; i < key.length; i++) {
			str = str + key[i];// 拼接成字符串，最终放在变量str中
		}
		
		System.out.println();

		return str;
	}

	public String getVector() {
		String str = "";

		for (int i = 0; i < vector.length; i++) {
			vector[i] = ran.nextInt(10); // 生成随机数[0,10)
			System.out.print(vector[i]);
		}

		for (int i = 0; i < vector.length; i++) {
			str = str + vector[i];// 拼接成字符串，最终放在变量str中
		}
		
		System.out.println();

		return str;
	}
}
