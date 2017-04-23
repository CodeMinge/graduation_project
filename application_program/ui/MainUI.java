package ui;

import java.awt.GridLayout;
import java.awt.event.*;

import javax.swing.*;

import client.Client;
import message_center.ClientMessage;

/**
 * 主界面
 */
public class MainUI extends JFrame implements ActionListener {
	JPanel j1 = new JPanel(), j2 = new JPanel();
	JTextArea tf1 = new JTextArea(4, 40), tf2 = new JTextArea(5, 40);// 定义一个文本域
	JMenuBar bar1 = new JMenuBar(), bar2 = new JMenuBar();// 定义一个菜单条
	JMenu menu1 = new JMenu("SQL操作");// 定义一个菜单
	JMenu menu2 = new JMenu("编辑");
	JMenu menu3 = new JMenu("查看");
	JMenu menu4 = new JMenu("帮助");
	JMenuItem submit = new JMenuItem("提交");// 定义菜单项
	JMenuItem clear = new JMenuItem("清除");
	JMenuItem copy = new JMenuItem("复制");
	JMenuItem cut = new JMenuItem("剪切");
	JMenuItem paste = new JMenuItem("粘贴");
	JMenuItem selfMessage = new JMenuItem("个人信息");
	JMenuItem tip = new JMenuItem("操作提示");
	
	JMenu menu5 = new JMenu("结果");// 定义一个菜单
	JMenuItem message = new JMenuItem("执行信息");// 定义菜单项
	JMenuItem fruit = new JMenuItem("执行结果");
	
	Client client = null;
	public static String result = null;
	public Select_ResultUI srUI = null;

	private void workArea() {
		tf1.setEditable(true);// 设置文本域可编辑
		bar1.setOpaque(true);// 设置菜单条透明效果
		menu1.add(submit); menu1.add(clear); menu1.addSeparator();// 设置分隔线
		menu2.add(copy); menu2.add(cut); menu2.add(paste); menu2.addSeparator();
		menu3.add(selfMessage); menu3.addSeparator();
		menu4.add(tip); menu4.addSeparator();
		bar1.add(menu1); bar1.add(menu2); bar1.add(menu3); bar1.add(menu4);
		submit.addActionListener(this);
		clear.addActionListener(this);
		selfMessage.addActionListener(this);
		tip.addActionListener(this);
		
		j1.add(bar1);// 设置窗口菜单条
		j1.add(new JScrollPane(tf1, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS));
		
		this.add(j1);
	}
	
	private void resultArea() {
		tf2.setEditable(true);// 设置文本域可编辑
		bar2.setOpaque(true);// 设置菜单条透明效果
		menu5.add(message); menu5.add(fruit); menu5.addSeparator();
		bar2.add(menu5);
		message.addActionListener(this);
		fruit.addActionListener(this);
		
		j2.add(bar2);// 设置窗口菜单条
		j2.add(new JScrollPane(tf2, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS));
		
		this.add(j2);
	}

	public MainUI(Client client) {
		super("您好，欢迎使用");
		this.setLayout(new GridLayout(2, 1));
		
		workArea();
		resultArea();
		
		addWindowListener(new WinLis());

		setSize(500, 350);
		setVisible(false);
		
		this.client = client;
	}

	// 菜单项点击事件处理程序
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == submit)
			this.submit();
		else if (e.getSource() == clear) {
			tf1.setText("");
		}
		else if (e.getSource() == message)
			tf2.setText(ClientMessage.ClientMessageOutput(result));
		else if(e.getSource() == fruit) {
			srUI.setVisible(true);
		}
//		if (e.getSource() == quit4)
//			System.exit(0);
	}

	private void submit() {
		String temp = tf1.getText();
		client.pw.println(temp);// 写到服务器
		client.pw.flush();
		
		tf1.setText("");
	}

	class WinLis extends WindowAdapter {
		public void windowClosing(WindowEvent e) {
			dispose();

			String temp = "q";
			client.pw.println(temp);// 写到服务器
			client.pw.flush();
		}
	}
}