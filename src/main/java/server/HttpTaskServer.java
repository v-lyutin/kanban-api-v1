package server;

import com.sun.net.httpserver.HttpServer;
import service.ManagersService;
import service.task_manager.FileBackedTasksManager;

import java.io.IOException;
import java.net.InetSocketAddress;

public class HttpTaskServer {
    private static final int PORT = 8080;
    private final HttpServer server;

    public HttpTaskServer() throws IOException {
        server = HttpServer.create(new InetSocketAddress(PORT), 0);
        server.createContext("/tasks",
                new TaskHandler(ManagersService.getDefault(ManagersService.getDefaultHistory())));
    }

    public void start() {
        server.start();
        System.out.println("HTTP-сервер запущен на " + PORT + " порту!");
    }

    public void stop(int delay) {
        server.stop(delay);
        System.out.println("HTTP-сервер остановлен!");
    }
}
