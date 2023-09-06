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
    protected SubTask subTask1;
    protected SubTask subTask2;
    protected Epic epic1;
    protected Epic epic2;

    protected void init() {
        historyManager = ManagersService.getDefaultHistory();
        task1 = new Task("Task 1", "Task 1 description");
        task2 = new Task("Task 2", "Task 2 description");
        epic1 = new Epic("Epic 1", "Epic 1 description");
        epic2 = new Epic("Epic 2", "Epic 2 description");
        subTask1 = new SubTask("Subtask 1", "Subtask 1 description", epic1.getId());
        subTask2 = new SubTask("Subtask 2", "Subtask 2 description", epic1.getId());
    }
}