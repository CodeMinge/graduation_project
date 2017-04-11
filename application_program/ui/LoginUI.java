package ui;

import javax.swing.*;

import client.Client;

import java.awt.*;
import java.awt.event.*;

/**
 * 登录界面
 */
public class LoginUI extends JFrame implements ActionListener {
	// 定义登录界面的组件
	JButton jb1, jb2, jb3 = null;
	JPanel jp1, jp2, jp3 = null;
	JTextField jtf = null;
	JLabel jlb1, jlb2 = null;
	JPasswordField jpf = null;

	Client client = null;
	MainUI mainUI = null;

	public LoginUI(Client client) {
		// 创建组件
		jb1 = new JButton("登录");
		jb2 = new JButton("注册");
		jb3 = new JButton("退出");
		// 设置监听
		jb1.addActionListener(this);
		jb2.addActionListener(this);
		jb3.addActionListener(this);

		jlb1 = new JLabel("用户名：");
		jlb2 = new JLabel("密    码：");

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
		this.setTitle("注册登录界面");
		this.setLayout(new GridLayout(3, 1));
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setBounds(300, 200, 300, 180);

		this.client = client;
		mainUI = new MainUI(client);
	}

	public void actionPerformed(ActionEvent e) {
		// 监听各个按钮
		if (e.getActionCommand() == "退出") {
			this.quit();
		} else if (e.getActionCommand() == "登录") {
			// 调用登录方法
			this.login();
		} else if (e.getActionCommand() == "注册") {
			// 调用注册方法
			this.regis();
		}
	}

	// 退出方法
	private void quit() {
		this.dispose();

		String temp = "q";
		client.pw.println(temp);// 写到服务器
		client.pw.flush();
	}

	// 注册方法
	private void regis() {
		this.dispose(); // 关闭当前界面
		new RegisterUI(client); // 打开新界面
	}

	// 登录方法
	private void login() {
		String temp = "login " + jtf.getText() + " " + jpf.getText();
		client.pw.println(temp);// 写到服务器
		client.pw.flush();

		// 界面切换
		this.dispose();
		mainUI.setVisible(true);
	}
}