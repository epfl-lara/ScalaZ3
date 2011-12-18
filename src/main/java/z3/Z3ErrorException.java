package z3;

public final class Z3ErrorException extends RuntimeException {
    private final String msg;
    private final int errCode;

    public Z3ErrorException(int errorCode, String message) {
        super(message);
        errCode = errorCode;
        msg = message;
    }

    public int getErrorCode() {
        return errCode;
    }
}
