package command;

import message_center.ServerMessage;
import server.DatabaseConnection;

public class Quit extends Command {

	public String process(String para1, String para2, DatabaseConnection dbc) {
		return ServerMessage.QIUT;
	}
}
