package key_manage;

import java.util.*;

/**
 * ��Կ�����������ɣ����߶���8λ
 */
// ��Կ����������
public class KeyAndVector {
	private static final int KEYANDVECTORLENGTH = 8;
	private int[] key = new int[KEYANDVECTORLENGTH];
	private int[] vector = new int[KEYANDVECTORLENGTH];
	private Random ran = new Random();

	public String getKey() {
		String str = "";

		for (int i = 0; i < key.length; i++) {
			key[i] = ran.nextInt(10); // ���������[0,10)
			System.out.print(key[i]);
		}

		for (int i = 0; i < key.length; i++) {
			str = str + key[i];// ƴ�ӳ��ַ��������շ��ڱ���str��
		}
		
		System.out.println();

		return str;
	}

	public String getVector() {
		String str = "";

		for (int i = 0; i < vector.length; i++) {
			vector[i] = ran.nextInt(10); // ���������[0,10)
			System.out.print(vector[i]);
		}

		for (int i = 0; i < vector.length; i++) {
			str = str + vector[i];// ƴ�ӳ��ַ��������շ��ڱ���str��
		}
		
		System.out.println();

		return str;
	}
}
