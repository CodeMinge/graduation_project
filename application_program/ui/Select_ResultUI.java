package ui;

import java.awt.BorderLayout;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

public class Select_ResultUI extends JFrame {
	// �������
	JTable table;
	// ������������(����ʹ������Թ���)
	JScrollPane scrollPane;
	// ��������ģ����Ķ���(���Ա�������)��
	DefaultTableModel tableModel;

	public Select_ResultUI(String[] columnNames, String[][] tableValues) {
		super();
		setTitle("��ѯ���");

		scrollPane = new JScrollPane();

//		// ���������������
//		String[] columnNames = { "A", "B", "C" };
//		// ���������������
//		String[][] tableValues = { { "A1", "B1", "C1" }, { "A2", "B2", "C2" }, { "A3", "B3", "C3" },
//				{ "A4", "B4", "C4" } };

		// ����ָ�����������ͱ������ݵı���ģ����Ķ���
		tableModel = new DefaultTableModel(tableValues, columnNames);
		// ����ָ������ģ�͵ı���
		table = new JTable(tableModel);

		// ���� RowSorter(RowSorter �����ṩ�� JTable ������͹���)��
		table.setRowSorter(new TableRowSorter<DefaultTableModel>(tableModel));
		scrollPane.setViewportView(table);
		getContentPane().add(scrollPane, BorderLayout.CENTER);

		setBounds(300, 200, 400, 300);
		setVisible(false);
//		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
}