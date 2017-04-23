package client;

import java.io.*;
import java.net.*;

import message_center.ClientMessage;
import message_center.ServerMessage;
import ui.LoginUI;
import ui.Select_ResultUI;

public class ReadServerMessage extends Thread// 从服务器读取消息
{
	BufferedReader bReader = null;
	Socket socket = null;
	Client client = null;
	LoginUI loginUI = null;

	public ReadServerMessage(Client c, Socket s, BufferedReader br) {
		client = c;
		this.socket = s;
		this.bReader = br;
		
		loginUI = new LoginUI(client);
		
		start();
	}

	public void run() {

		String res = ClientMessage.NULL;
		
		while (true)// 一直等待着服务器的消息
		{
			try {
				res = bReader.readLine();
//				ClientMessage.ClientMessageOutput(res);
				if(res.contains("@")) {
					String [][] target = null;
					
					String [] temp1 = res.split("@");
					System.out.println(temp1[0]);
					System.out.println(temp1[1]);
					loginUI.mainUI.result = temp1[0];
					
					String [] temp2 = temp1[1].split("&");
					String [] temp3 = temp2[0].split(" ");
					target = new String[temp2.length - 1][temp3.length];
					for(int i = 1; i < temp2.length; i ++) {
						String []temp = temp2[i].split(" ");
						for(int j = 0;j < temp3.length; j ++) {
							target[i - 1][j] = temp[j];
						}
					}
					loginUI.mainUI.srUI = new Select_ResultUI(temp3, target);
				} else {
					loginUI.mainUI.result = res;
				}
				if(res.equals(ClientMessage.QIUT)) {
					client.close();
					break;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}