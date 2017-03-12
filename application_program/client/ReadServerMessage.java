package client;

import java.io.*;
import java.net.*;

import message_center.ClientMessage;

public class ReadServerMessage extends Thread// 从服务器读取消息
{
	BufferedReader bReader = null;
	Socket socket = null;
	Client client = null;

	public ReadServerMessage(Client c, Socket s, BufferedReader br) {
		client = c;
		this.socket = s;
		this.bReader = br;
		start();
	}

	public void run() {

		String res = ClientMessage.NULL;
		while (true)// 一直等待着服务器的消息
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