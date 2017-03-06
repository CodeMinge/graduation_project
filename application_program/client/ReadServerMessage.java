package client;

import java.io.*;
import java.net.*;

import message_center.ClientMessage;

public class ReadServerMessage extends Thread// 从服务器读取消息
{
	BufferedReader bReader;
	Socket socket;

	public ReadServerMessage(Socket s, BufferedReader br) {
		this.socket = s;
		this.bReader = br;
		start();
	}

	public void run() {
		
		String res = ClientMessage.NULL;
		while (true)// 一直等待着服务器的消息
		{
			try {
				//当socket或io流被关闭
				if(socket.isClosed() || bReader == null) {
					break;
				}
				else {
					res = bReader.readLine();
					System.out.println(res);
					ClientMessage.ClientMessageOutput(res);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}		
		}
	}
}