package engine;

public class InvalidCommandException extends Exception {
		private static final long serialVersionUID = 1L;

		public InvalidCommandException() {
			super("Il comando non e' valido");
		}
}
