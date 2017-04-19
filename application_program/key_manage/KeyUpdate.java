package key_manage;

import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import server.DatabaseConnection;

//密钥定时更新
public class KeyUpdate {
//	static int count = 0;
	
	//这里可以是static，因为存在一个是合理的
	private static DatabaseConnection dbc = null; 

	public static void showTimer() {
		TimerTask task = new TimerTask() {
			@Override
			public void run() {
//				++count;
//				System.out.println("时间=" + new Date() + " 执行了" + count + "次"); // 1次
				//连接数据库
				dbc = new DatabaseConnection();
				dbc.connect_ten();
				
				//使用超级用户登录
				//对加密的表重新进行解密、加密
			}
		};
		// 设置执行时间
		Calendar calendar = Calendar.getInstance();
		int year = calendar.get(Calendar.YEAR);
		int month = calendar.get(Calendar.MONTH);
		int day = calendar.get(Calendar.DAY_OF_MONTH);// 每天
		// 定制每天的23:30:00执行，
		calendar.set(year, month, day, 23, 30, 00);
		Date date = calendar.getTime();
		Timer timer = new Timer();
		System.out.println(date);

		// 每天的date时刻执行task, 仅执行一次
		 timer.schedule(task, date);
	}
}