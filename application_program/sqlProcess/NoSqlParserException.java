package sqlProcess;

public class NoSqlParserException extends Exception {
	private static final long serialVersionUID = 1L;

	NoSqlParserException() {

	}

	NoSqlParserException(String sql) {
		// ���ø��෽��
		super(sql);
	}
}