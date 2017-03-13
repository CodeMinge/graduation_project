package sqlProcess;

import java.util.*;


/** */
/**
 * 单句Sql解析器制造工厂
 */
public class SqlParserUtil {
	
	public LinkedList<String> mystr = new LinkedList<String>();
		
	/** */
	/**
	 * 方法的主要入口 @param sql:要解析的sql语句 @return 返回解析结果
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
	 * SQL语句解析的接口 @param sql:要解析的sql语句 @return 返回解析结果
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
