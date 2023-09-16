
import models.Epic;
import models.SubTask;
import models.Task;
import server.HttpTaskServer;
import server.KVServer;
import service.ManagersService;
import service.task_manager.HttpTaskManager;
import java.io.IOException;

public class Tests {
    public static void main(String[] args) throws IOException {
          new KVServer().start();
          new HttpTaskServer().start();
        HttpTaskManager manager = ManagersService.getHttpManager(ManagersService.getDefaultHistory());

        Task task = new Task("Task 1", "Task 1 description", "2023.10.10, 10:00", 120);
        Task task1 = new Task("Task 2", "Task 2 description");
        Epic epic = new Epic("Epic 1", "Epic 1 description");
        manager.createEpic(epic);
        SubTask subTask1 = new SubTask("Subtask 1", "Subtask 1 description", "2023.10.11, 10:00", 120, epic.getId());
        manager.createTask(task);
        manager.createTask(task1);
        manager.createSubTask(subTask1);


        HttpTaskManager newManager = HttpTaskManager.loadFromKVServer(ManagersService.getDefaultHistory(), 8078);
        System.out.println(newManager.getAllTasks());
    }
}

