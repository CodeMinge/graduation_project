package ui;

import java.awt.GridLayout;
import java.awt.event.*;

import javax.swing.*;

import client.Client;
import message_center.ClientMessage;

/**
 * ������
 */
public class MainUI extends JFrame implements ActionListener {
	JPanel j1 = new JPanel(), j2 = new JPanel();
	JTextArea tf1 = new JTextArea(4, 40), tf2 = new JTextArea(5, 40);// ����һ���ı���
	JMenuBar bar1 = new JMenuBar(), bar2 = new JMenuBar();// ����һ���˵���
	JMenu menu1 = new JMenu("SQL����");// ����һ���˵�
	JMenu menu2 = new JMenu("�༭");
	JMenu menu3 = new JMenu("�鿴");
	JMenu menu4 = new JMenu("����");
	JMenuItem submit = new JMenuItem("�ύ");// ����˵���
	JMenuItem clear = new JMenuItem("���");
	JMenuItem copy = new JMenuItem("����");
	JMenuItem cut = new JMenuItem("����");
	JMenuItem paste = new JMenuItem("ճ��");
	JMenuItem selfMessage = new JMenuItem("������Ϣ");
	JMenuItem tip = new JMenuItem("������ʾ");
	
	JMenu menu5 = new JMenu("���");// ����һ���˵�
	JMenuItem message = new JMenuItem("ִ����Ϣ");// ����˵���
	JMenuItem fruit = new JMenuItem("ִ�н��");
	
	Client client = null;
	public static String result = null;
	public Select_ResultUI srUI = null;

	private void workArea() {
		tf1.setEditable(true);// �����ı���ɱ༭
		bar1.setOpaque(true);// ���ò˵���͸��Ч��
		menu1.add(submit); menu1.add(clear); menu1.addSeparator();// ���÷ָ���
		menu2.add(copy); menu2.add(cut); menu2.add(paste); menu2.addSeparator();
		menu3.add(selfMessage); menu3.addSeparator();
		menu4.add(tip); menu4.addSeparator();
		bar1.add(menu1); bar1.add(menu2); bar1.add(menu3); bar1.add(menu4);
		submit.addActionListener(this);
		clear.addActionListener(this);
		selfMessage.addActionListener(this);
		tip.addActionListener(this);
		
		j1.add(bar1);// ���ô��ڲ˵���
		j1.add(new JScrollPane(tf1, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS));
		
		this.add(j1);
	}
	
	private void resultArea() {
		tf2.setEditable(true);// �����ı���ɱ༭
		bar2.setOpaque(true);// ���ò˵���͸��Ч��
		menu5.add(message); menu5.add(fruit); menu5.addSeparator();
		bar2.add(menu5);
		message.addActionListener(this);
		fruit.addActionListener(this);
		
		j2.add(bar2);// ���ô��ڲ˵���
		j2.add(new JScrollPane(tf2, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS));
		
		this.add(j2);
	}

	public MainUI(Client client) {
		super("���ã���ӭʹ��");
		this.setLayout(new GridLayout(2, 1));
		
		workArea();
		resultArea();
		
		addWindowListener(new WinLis());

		setSize(500, 350);
		setVisible(false);
		
		this.client = client;
	}

	// �˵������¼��������
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
		client.pw.println(temp);// д��������
		client.pw.flush();
		
		tf1.setText("");
	}

	class WinLis extends WindowAdapter {
		public void windowClosing(WindowEvent e) {
			dispose();

			String temp = "q";
			client.pw.println(temp);// д��������
			client.pw.flush();
		}
	}
}