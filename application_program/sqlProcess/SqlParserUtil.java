package sqlProcess;

import java.util.*;


/** */
/**
 * ����Sql���������칤��
 */
public class SqlParserUtil {
	
	public LinkedList<String> mystr = new LinkedList<String>();
		
	/** */
	/**
	 * ��������Ҫ��� @param sql:Ҫ������sql��� @return ���ؽ������
	 */
	public String getParsedSql(String sql) {
		sql = sql.trim();
		sql = sql.toLowerCase();
		sql = sql.replaceAll("\\s{1,}", " ");
		sql = "" + sql + " ENDOFSQL";
		// System.out.println(sql);
		return SingleSqlParserFactory.generateParser(sql).getParsedSql(mystr);
	}

	/** */
	/**
	 * SQL�������Ľӿ� @param sql:Ҫ������sql��� @return ���ؽ������
	 */
	public List<SqlSegment> getParsedSqlList(String sql) {
		sql = sql.trim();
		sql = sql.toLowerCase();
		sql = sql.replaceAll("\\s{1,}", " ");
		sql = "" + sql + " ENDOFSQL";
		// System.out.println(sql);
		return SingleSqlParserFactory.generateParser(sql).RetrunSqlSegments();
	}
}
