import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    private final int port;
    final ExecutorService threadPool = Executors.newFixedThreadPool(64);
    Map<Map<String, String>, Handler> handlerList = new HashMap<>();

    public Server(int port) {
        this.port = port;
    }

    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {

            System.out.println("Server started...");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                threadPool.submit(connection(clientSocket));
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private Thread connection(Socket socket){
        return new Thread(()-> {
            try {
                final BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                final BufferedOutputStream out = new BufferedOutputStream(socket.getOutputStream());

                final String requestLine = in.readLine();
                final String[] parts = requestLine.split(" ");

                if (parts.length == 3) {//проверяю запрос
                    Request request = new Request(parts[0], parts[1], parts[2]);

                    for(Map<String, String> key: handlerList.keySet()){//пробегаемся по ключам списка хендлеров
                        for(String method: key.keySet()){//Пробегаюсь по методам
                            if(method.equals(request.getMethod())){//если метод равняется методу запроса
                                if(key.get(method).equals(request.getMessage())){//проверяю сообщение
                                    handlerList.get(key).handle(request, out);//если нашли, то вызываю метод handle
                                    break;
                                }
                            }
                        }
                    }
                }
                out.write((//если не нашел подходящий хендлер отвечаю
                        "HTTP/1.1 404 Not Found\r\n" +
                                "Content-Length: 0\r\n" +
                                "Connection: close\r\n" +
                                "\r\n"
                ).getBytes());
                out.flush();
            }catch (Exception e){
                e.printStackTrace();
            }
        });
    }
    public void addHandler(String method, String message, Handler handler){
        Map<String, String> map= new HashMap<>();
        map.put(method, message);
        handlerList.put(map, handler);
    }
}



