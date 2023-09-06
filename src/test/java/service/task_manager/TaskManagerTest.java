package service.task_manager;

import models.Epic;
import models.SubTask;
import models.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.ManagersService;
import service.history_manager.HistoryManager;
import service.history_manager.InMemoryHistoryManager;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

abstract class TaskManagerTest<T extends TaskManager> {
    protected T manager;
    protected HistoryManager historyManager;
    protected Task task1;
    protected Task task2;
    protected Task task3;
    protected Task task4;
    protected Task task5;
    protected SubTask subTask1;
    protected SubTask subTask2;
    protected SubTask subTask3;
    protected SubTask subTask4;
    protected SubTask subTask5;
    protected Epic epic1;
    protected Epic epic2;

    protected void init() {
        historyManager = ManagersService.getDefaultHistory();
        task1 = new Task("Task 1", "Task 1 description");
        task2 = new Task("Task 2", "Task 2 description");
        task3 = new Task("Task 3", "Task 3 description", "2023.09.29, 10:00", 120);
        task4 = new Task("Task 4", "Task 4 description", "2023.09.09, 15:30", 120);
        task5 = new Task("Task 5", "Task 5 description", "2023.09.08, 18:00", 30);
        epic1 = new Epic("Epic 1", "Epic 1 description");
        epic2 = new Epic("Epic 2", "Epic 2 description");
        subTask1 = new SubTask("Subtask 1", "Subtask 1 description", epic1.getId());
        subTask2 = new SubTask("Subtask 2", "Subtask 2 description", epic1.getId());
        subTask3 = new SubTask("Subtask 3", "Subtask 3 description", "2023.09.15, 11:00", 500, epic1.getId());
        subTask4 = new SubTask("Subtask 4", "Subtask 4 description", "2023.09.15, 06:00", 120, epic1.getId());
        subTask5 = new SubTask("Subtask 5", "Subtask 5 description", "2023.09.20, 11:00", 120, epic1.getId());
    }
}