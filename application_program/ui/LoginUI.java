package ui;

import javax.swing.*;

import client.Client;

import java.awt.*;
import java.awt.event.*;

/**
 * ��¼����
 */
public class LoginUI extends JFrame implements ActionListener {
	// �����¼��������
	JButton jb1, jb2, jb3 = null;
	JPanel jp1, jp2, jp3 = null;
	JTextField jtf = null;
	JLabel jlb1, jlb2 = null;
	JPasswordField jpf = null;

	Client client = null;
	MainUI mainUI = null;

	public LoginUI(Client client) {
		// �������
		jb1 = new JButton("��¼");
		jb2 = new JButton("ע��");
		jb3 = new JButton("�˳�");
		// ���ü���
		jb1.addActionListener(this);
		jb2.addActionListener(this);
		jb3.addActionListener(this);

		jlb1 = new JLabel("�û�����");
		jlb2 = new JLabel("��    �룺");

		jtf = new JTextField(10);
		jpf = new JPasswordField(10);

		jp1 = new JPanel();
		jp2 = new JPanel();
		jp3 = new JPanel();

		jp1.add(jlb1);
		jp1.add(jtf);

		jp2.add(jlb2);
		jp2.add(jpf);

		jp3.add(jb1);
		jp3.add(jb2);
		jp3.add(jb3);
		this.add(jp1);
		this.add(jp2);
		this.add(jp3);

		this.setVisible(true);
		this.setResizable(false);
		this.setTitle("ע���¼����");
		this.setLayout(new GridLayout(3, 1));
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setBounds(300, 200, 300, 180);

		this.client = client;
		mainUI = new MainUI(client);
	}

	public void actionPerformed(ActionEvent e) {
		// ����������ť
		if (e.getActionCommand() == "�˳�") {
			this.quit();
		} else if (e.getActionCommand() == "��¼") {
			// ���õ�¼����
			this.login();
		} else if (e.getActionCommand() == "ע��") {
			// ����ע�᷽��
			this.regis();
		}
	}

	// �˳�����
	private void quit() {
		this.dispose();

		String temp = "q";
		client.pw.println(temp);// д��������
		client.pw.flush();
	}

	// ע�᷽��
	private void regis() {
		this.dispose(); // �رյ�ǰ����
		new RegisterUI(client); // ���½���
	}

	// ��¼����
	private void login() {
		String temp = "login " + jtf.getText() + " " + jpf.getText();
		client.pw.println(temp);// д��������
		client.pw.flush();

		// �����л�
		this.dispose();
		mainUI.setVisible(true);
	}
}