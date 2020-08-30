package engine;

public class TrapException extends Exception{
	
	private static final long serialVersionUID = 1L;
	
	private TrapHandler handler;;

	public TrapException(TrapHandler handler) {
		// TODO Auto-generated constructor stub
		this.handler = handler;
	}

	public TrapException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	public TrapException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}

	public TrapException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

	public TrapException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
		// TODO Auto-generated constructor stub
	}

	public TrapHandler getHandler() {
		return handler;
	}
}
