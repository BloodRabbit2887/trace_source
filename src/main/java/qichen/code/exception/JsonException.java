package qichen.code.exception;

public class JsonException extends FrameworkException {

	private static final long serialVersionUID = 1L;

	public JsonException() {
		super();
	}

	public JsonException(String arg0, Throwable arg1, boolean arg2, boolean arg3) {
		super(arg0, arg1, arg2, arg3);
	}

	public JsonException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	public JsonException(String arg0) {
		super(arg0);
	}

	public JsonException(Throwable arg0) {
		super(arg0);
	}

}
