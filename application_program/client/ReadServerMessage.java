package client;

import java.io.*;
import java.net.*;

import message_center.ClientMessage;
import ui.LoginUI;

public class ReadServerMessage extends Thread// �ӷ�������ȡ��Ϣ
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
		
		while (true)// һֱ�ȴ��ŷ���������Ϣ
		{
			try {
				res = bReader.readLine();
				ClientMessage.ClientMessageOutput(res);
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