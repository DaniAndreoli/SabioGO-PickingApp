package helpers;

/**
 * Created by Dani_ on 29/10/2017.
 */

public class dbOperationResponse {

    boolean ok;
    Exception exception;
    String message;

    public dbOperationResponse() {
        this.ok = false;
        this.exception = null;
        this.message = "";
    }

    public boolean Ok() {
        return ok;
    }

    public void Ok(boolean ok) {
        this.ok = ok;
    }

    public Exception getException() { return exception; }

    public void setException(Exception exception) {
        this.exception = exception;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }




}
