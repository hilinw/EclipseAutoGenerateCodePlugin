package autogenerate.code.plugin.generator;

public class CodeGenException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public CodeGenException() {
    }

    public CodeGenException(String message) {
        super(message);
    }

    public CodeGenException(String message, Throwable cause) {
        super(message, cause);
    }

    public CodeGenException(Throwable cause) {
        super(cause);
    }

}
