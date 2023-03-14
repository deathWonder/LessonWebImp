public class Request {
    private final String method;
    private final String message;
    private final String body;

    public Request(String method, String message, String body){
        this.method = method;
        this.message = message;
        this.body = body;
    }
    public String getMethod(){
        return method;
    }
    public String getMessage() {
        return message;
    }
    public String getBody(){
        return body;
    }
}
