import models.Epic;
import models.SubTask;
import service.ManagersService;
import service.history_manager.HistoryManager;
import service.task_manager.FileBackedTasksManager;

public class Tests {
    public static void main(String[] args) {
        HistoryManager historyManager = ManagersService.getDefaultHistory();
        FileBackedTasksManager manager = ManagersService.getDefault(historyManager);

        Epic epic1 = new Epic("...", "...");
        manager.createEpic(epic1);
        SubTask subTask1 = new SubTask("Subtask1", "Description", "2023.10.10, 10:00", 120, epic1.getId());
        manager.createSubTask(subTask1);
        SubTask subTask2 = new SubTask("Subtask2", "Description", "2023.10.11, 11:00", 120, epic1.getId());
        manager.createSubTask(subTask2);
    }
}

