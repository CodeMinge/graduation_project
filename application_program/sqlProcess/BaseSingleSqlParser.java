package sqlProcess;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/** */
/**
 * ����Sql�����������伴��Ƕ�׵���˼
 */
public abstract class BaseSingleSqlParser {
	/** */
	/**
	 * ԭʼSql���
	 */
	protected String originalSql;
	/** */
	/**
	 * Sql���Ƭ��
	 */
	protected List<SqlSegment> segments;

	/** */
	/**
	 * ���캯��������ԭʼSql��䣬�������֡�
	 * 
	 * @param originalSql
	 */
	public BaseSingleSqlParser(String originalSql) {
		this.originalSql = originalSql;
		segments = new ArrayList<SqlSegment>();
		initializeSegments();
		splitSql2Segment();
	}

	/** */
	/**
	 * ��ʼ��segments��ǿ������ʵ��
	 *
	 */
	protected abstract void initializeSegments();

	/** */
	/**
	 * ��originalSql���ֳ�һ����Ƭ��
	 *
	 */
	protected void splitSql2Segment() {
		for (SqlSegment sqlSegment : segments) {
			sqlSegment.parse(originalSql);
		}
	}

	/** */
	/**
	 * �õ�������ϵ�Sql���
	 * 
	 * @return
	 */
	public String getParsedSql(LinkedList<String> mystr) {

		// �����������Ƭ�ε���Ϣ
		/*
		 * for(SqlSegment sqlSegment:segments) { String
		 * start=sqlSegment.getStart(); String end=sqlSegment.getEnd();
		 * System.out.println(start); System.out.println(end); }
		 */
		int i = 0;
		StringBuffer sb = new StringBuffer();
		for (SqlSegment sqlSegment : segments) {
//			System.out.println(sqlSegment.getBody());
			if(i == 0) {
				i ++;
				mystr.addLast(sqlSegment.getStart());
				System.out.println(sqlSegment.getStart());
			}
				
			System.out.println(sqlSegment.getBody());
			mystr.addLast(sqlSegment.getBody());
			sb.append(sqlSegment.getParsedSqlSegment());
		}
		String retval = sb.toString().replaceAll("@+", "\n");
		return retval;
	}

	/** */
	/**
	 * �õ�������SqlƬ��
	 * 
	 * @return
	 */
	public List<SqlSegment> RetrunSqlSegments() {
		int SegmentLength = this.segments.size();
		if (SegmentLength != 0) {
			List<SqlSegment> result = this.segments;
			return result;
		} else {
			// throw new Exception();
			return null;
		}
	}
}
