import server.HttpTaskServer;
import server.KVServer;
import service.ManagersService;
import service.task_manager.HttpTaskManager;
import java.io.IOException;

public class KanbanApplication {
    public static void main(String[] args) throws IOException {
          new KVServer().start();
          new HttpTaskServer().start();
        HttpTaskManager manager = ManagersService.getHttpManager(ManagersService.getDefaultHistory());
    }
}

