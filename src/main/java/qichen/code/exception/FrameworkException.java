package qichen.code.exception;

public class FrameworkException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public FrameworkException() {
		super();
	}

	public FrameworkException(String arg0, Throwable arg1, boolean arg2, boolean arg3) {
		super(arg0, arg1, arg2, arg3);
	}

	public FrameworkException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	public FrameworkException(String arg0) {
		super(arg0);
	}

	public FrameworkException(Throwable arg0) {
		super(arg0);
	}

}
