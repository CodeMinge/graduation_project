package sqlProcess;

import java.util.LinkedList;

/** */
/**
 *
 * µ¥¾ä²åÈëÓï¾ä½âÎöÆ÷
 */
public class InsertSqlParser extends BaseSingleSqlParser {
	public InsertSqlParser(String originalSql) {
		super(originalSql);
	}

	@Override
	protected void initializeSegments() {
		segments.add(new SqlSegment("(insert into)(.+)([(])", "[,]"));
		segments.add(new SqlSegment("([(])(.+)( [)] values )", "[,]"));
		segments.add(new SqlSegment("([)] values [(])(.+)( [)])", "[,]"));
	}

	@Override
	public String getParsedSql(LinkedList<String> mystr) {
		String retval = super.getParsedSql(mystr);
		retval = retval + ")";
		return retval;
	}
}
