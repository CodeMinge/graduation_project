package client;

import java.io.*;
import java.net.*;

import message_center.ClientMessage;

public class ReadServerMessage extends Thread// �ӷ�������ȡ��Ϣ
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
		while (true)// һֱ�ȴ��ŷ���������Ϣ
		{
			try {
				//��socket��io�����ر�
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