package key_manage;

import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import server.DatabaseConnection;

//��Կ��ʱ����
public class KeyUpdate {
//	static int count = 0;
	
	//���������static����Ϊ����һ���Ǻ����
	private static DatabaseConnection dbc = null; 

	public static void showTimer() {
		TimerTask task = new TimerTask() {
			@Override
			public void run() {
//				++count;
//				System.out.println("ʱ��=" + new Date() + " ִ����" + count + "��"); // 1��
				//�������ݿ�
				dbc = new DatabaseConnection();
				dbc.connect_ten();
				
				//ʹ�ó����û���¼
				//�Լ��ܵı����½��н��ܡ�����
			}
		};
		// ����ִ��ʱ��
		Calendar calendar = Calendar.getInstance();
		int year = calendar.get(Calendar.YEAR);
		int month = calendar.get(Calendar.MONTH);
		int day = calendar.get(Calendar.DAY_OF_MONTH);// ÿ��
		// ����ÿ���23:30:00ִ�У�
		calendar.set(year, month, day, 23, 30, 00);
		Date date = calendar.getTime();
		Timer timer = new Timer();
		System.out.println(date);

		// ÿ���dateʱ��ִ��task, ��ִ��һ��
		 timer.schedule(task, date);
	}
}