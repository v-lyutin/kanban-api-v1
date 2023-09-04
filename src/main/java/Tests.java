import models.SubTask;
import models.Task;
import service.history_manager.HistoryManager;
import service.task_manager.FileBackedTasksManager;
import models.Epic;
import service.ManagersService;

import java.io.File;
import java.time.LocalDateTime;
import java.util.List;


public class Tests {
    public static void main(String[] args) {
        HistoryManager historyManager = ManagersService.getDefaultHistory();
//        FileBackedTasksManager manager = ManagersService.getDefault(historyManager);
        FileBackedTasksManager manager = FileBackedTasksManager.loadFromFile(
                new File("src/main/resources/tasks_history.csv"),
                historyManager);

        System.out.println(manager.getHistory());

//        Task task1 = new Task("task1", "task1 description");
//        manager.createTask(task1);
//
//        Task task2 = new Task("task2", "task2 description", "2023.09.05, 11:00", 120);
//        manager.createTask(task2);
//
//        Epic epic1 = new Epic("epic1", "epic1 description");
//        manager.createEpic(epic1);
//
//        SubTask subTask2 = new SubTask("subTask2", "subTask2 description", epic1.getId());
//        manager.createSubTask(subTask2);
//
//        SubTask subTask3 = new SubTask("subTask3", "subTask3 description", "2024.09.10, 10:00", 120, epic1.getId());
//        manager.createSubTask(subTask3);
//
//        SubTask subTask4 = new SubTask("subTask4", "subTask4 description", "2023.09.16, 13:00", 120, epic1.getId());
//        manager.createSubTask(subTask4);
//
//        Epic epic2 = new Epic("epic2", "epic2 description");
//        manager.createEpic(epic2);
//
//
//        System.out.println(manager.getEpic(epic1.getId()));
    }
}

