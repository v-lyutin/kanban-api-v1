import models.Epic;
import models.SubTask;
import models.Task;
import service.ManagersService;
import service.history_manager.HistoryManager;
import service.task_manager.FileBackedTasksManager;

public class Tests {
    public static void main(String[] args) {
        HistoryManager historyManager = ManagersService.getDefaultHistory();
        FileBackedTasksManager manager = ManagersService.getDefault(historyManager);

        Epic epic1 = new Epic("...", "...");
        manager.createEpic(epic1);
        Task task3 = new Task("Task 3", "Task 3 description", "2023.09.29, 10:00", 120);
        manager.createTask(task3);
        Task task5 = new Task("Task 5", "Task 5 description", "2023.09.08, 18:00", 30);
        manager.createTask(task5);
        SubTask subTask3 = new SubTask("Subtask 3", "Subtask 3 description", "2023.09.15, 11:00", 500, epic1.getId());
        manager.createSubTask(subTask3);
        SubTask subTask4 = new SubTask("Subtask 4", "Subtask 4 description", "2023.09.15, 06:00", 120, epic1.getId());
        manager.createSubTask(subTask4);

        System.out.println(manager.getPrioritizedTasks());
    }
}

