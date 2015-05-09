package tonivade.db;

public class TinyDBException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public TinyDBException() {
        super();
    }

    public TinyDBException(String message, Throwable cause) {
        super(message, cause);
    }

    public TinyDBException(String message) {
        super(message);
    }

    public TinyDBException(Throwable cause) {
        super(cause);
    }

}
