package command;

import DBConnect.DBEncryptConnection;
import DBConnect.KeyDBConnection;
import message_center.ServerMessage;

public class Quit extends Command {

	public Quit(String command) {
		super(command);
	}

	@Override
	public String process(String para1, String para2, DBEncryptConnection dbec, KeyDBConnection kdbc, String name) {
		return ServerMessage.QIUT;
	}
}