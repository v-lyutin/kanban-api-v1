import models.Epic;
import models.SubTask;
import models.Task;
import service.ManagersService;
import service.history_manager.HistoryManager;
import service.task_manager.FileBackedTasksManager;

import java.io.File;
import java.time.LocalDateTime;
import java.util.Objects;

public class Tests {
    public static void main(String[] args) {
        HistoryManager historyManager = ManagersService.getDefaultHistory();
        FileBackedTasksManager manager = ManagersService.getDefault(historyManager);

        Task task1 = new Task("Task 1", "Task 1 description");
        manager.createTask(task1);

        Epic epic = new Epic("Epic", "Description");
        manager.createEpic(epic);

        SubTask subTask1 = new SubTask("Subtask 1", "...", epic.getId());
        manager.createSubTask(subTask1);

        SubTask subTask2 = new SubTask("Subtask 1", "...", "2025.10.10, 10:00", 120,epic.getId());
        manager.createSubTask(subTask2);

        SubTask subTask3 = new SubTask("Subtask 1", "...", "2023.12.12, 10:00", 120,epic.getId());
        manager.createSubTask(subTask3);

        SubTask subTask4 = new SubTask("Subtask 1", "...", "2024.01.01, 10:00", 120,epic.getId());
        manager.createSubTask(subTask4);

        manager.getSubtask(subTask4.getId());
        manager.getTask(task1.getId());
    }
}

