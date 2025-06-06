package Util;

public class Response {
    private boolean success;
    private String message;
    
    public Response(boolean success, String message) {
        this.success = success;
        this.message = message;
    }
    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }
    public static Response Success(String message) {
        return new Response(true, message);
    }

    public static Response Error(String message) {
        return new Response(false, message);
    }
}
