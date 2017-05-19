package ui;

import java.awt.event.*;
import java.awt.*;

import javax.swing.*;

import client.Client;

/**
 * 注册界面。
 */
public class RegisterUI extends JFrame implements ActionListener {
	// 定义组件
	JFrame jf;
	JPanel jp;
	JLabel jl1, jl2;
	JTextField jtf1, jtf2;
	JButton jb1, jb2;

	Client client = null;

	public RegisterUI(Client client) {
		// 初始化组件
		jf = new JFrame();
		jp = new JPanel();
		jl1 = new JLabel("请输入用户名：");
		jtf1 = new JTextField(10);
		jl2 = new JLabel("请输入密码：");
		jtf2 = new JTextField(10);

		jb1 = new JButton("返回");
		jb1.setToolTipText("点我返回登录界面哦");
		jb2 = new JButton("注册");
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
		this.setTitle("注册界面");
		this.setBounds(200, 100, 250, 150);
		this.setVisible(true);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		// this.setResizable(false);

		this.client = client;
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand() == "返回") {
			this.dispose();
			LoginUI lui = new LoginUI(client);
			lui.setVisible(true);

		} else if (e.getActionCommand() == "注册") {
			// 调用注册方法
			this.register();
			new LoginUI(client);
		}
	}

	public void register() {
		String user = jtf1.getText();
		String password = jtf2.getText();

		String temp = "register " + user + " " + password;
		client.pw.println(temp);// 写到服务器
		client.pw.flush();

		this.jtf1.setText("");
		this.jtf2.setText("");
	}
}