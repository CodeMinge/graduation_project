package ui;

import java.awt.event.*;
import java.awt.*;

import javax.swing.*;

import client.Client;

/**
 * ע����档
 */
public class RegisterUI extends JFrame implements ActionListener {
	// �������
	JFrame jf;
	JPanel jp;
	JLabel jl1, jl2;
	JTextField jtf1, jtf2;
	JButton jb1, jb2;

	Client client = null;

	public RegisterUI(Client client) {
		// ��ʼ�����
		jf = new JFrame();
		jp = new JPanel();
		jl1 = new JLabel("�������û�����");
		jtf1 = new JTextField(10);
		jl2 = new JLabel("���������룺");
		jtf2 = new JTextField(10);

		jb1 = new JButton("����");
		jb1.setToolTipText("���ҷ��ص�¼����Ŷ");
		jb2 = new JButton("ע��");
		jb1.addActionListener(this);
		jb2.addActionListener(this);

		jp.setLayout(new GridLayout(5, 2));

		jp.add(jl1);
		jp.add(jtf1);

		jp.add(jl2);
		jp.add(jtf2);

		jp.add(jb1);
		jp.add(jb2);

		this.add(jp);
		this.setTitle("ע�����");
		this.setBounds(200, 100, 250, 150);
		this.setVisible(true);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		// this.setResizable(false);

		this.client = client;
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand() == "����") {
			this.dispose();
			LoginUI lui = new LoginUI(client);
			lui.setVisible(true);

		} else if (e.getActionCommand() == "ע��") {
			// ����ע�᷽��
			this.register();
			new LoginUI(client);
		}
	}

	public void register() {
		String user = jtf1.getText();
		String password = jtf2.getText();

		String temp = "register " + user + " " + password;
		client.pw.println(temp);// д��������
		client.pw.flush();

		this.jtf1.setText("");
		this.jtf2.setText("");
	}
}