package command;

import DBConnect.DBEncryptConnection;
import DBConnect.KeyDBConnection;
import key_manage.KeyManager;

public class Live_Key extends Command {

	public Live_Key(String command) {
		super(command);
		// TODO Auto-generated constructor stub
	}

	@Override
	public String process(String para1, String para2, DBEncryptConnection dbec, KeyDBConnection kdbc, String name) {
		KeyManager km = new KeyManager();
		return km.check_Live_Key(dbec);
	}
}