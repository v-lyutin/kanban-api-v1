package service.history_manager;

import models.Epic;
import models.SubTask;
import models.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.ManagersService;
import service.task_manager.InMemoryTaskManager;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {
    private HistoryManager historyManager;
    private Task task1;
    private Task task2;
    private SubTask subTask1;
    private Epic epic1;
    private Epic epic2;

    @BeforeEach
    void setUp() {
        historyManager = ManagersService.getDefaultHistory();
        InMemoryTaskManager manager = new InMemoryTaskManager(historyManager);

        task1 = new Task("Task 1", "Task 1 description");
        manager.createTask(task1);
        task2 = new Task("Task 2", "Task 2 description");
        manager.createTask(task2);
        epic1 = new Epic("Epic 1", "Epic 1 description");
        manager.createEpic(epic1);
        epic2 = new Epic("Epic 2", "Epic 2 description");
        manager.createEpic(epic2);
        subTask1 = new SubTask("Subtask 1", "Subtask 1 description", epic1.getId());
        manager.createSubTask(subTask1);

        historyManager.add(subTask1);
        historyManager.add(epic1);
        historyManager.add(task1);
        historyManager.add(epic2);
        historyManager.add(task2);
    }

    @Test
    void getHistory_emptyHistory() {
        historyManager.remove(subTask1.getId());
        historyManager.remove(epic1.getId());
        historyManager.remove(task1.getId());
        historyManager.remove(epic2.getId());
        historyManager.remove(task2.getId());

        List<Task> history = historyManager.getHistory();

        assertNotNull(history);
        assertTrue(history.isEmpty());
    }

    @Test
    void getHistory_notEmptyHistory() {
        List<Task> history = historyManager.getHistory();

        assertNotNull(history);
        assertFalse(history.isEmpty());
        assertEquals(5, history.size());
    }

    @Test
    void add_duplication() {
        historyManager.add(subTask1);
        List<Task> expectedHistory = List.of(epic1, task1, epic2, task2, subTask1);
        assertArrayEquals(expectedHistory.toArray(), historyManager.getHistory().toArray());
    }

    @Test
    void remove_atTheTopOfTheHistory() {
        historyManager.remove(subTask1.getId());

        List<Task> expectedHistory = List.of(epic1, task1, epic2, task2);
        assertArrayEquals(expectedHistory.toArray(), historyManager.getHistory().toArray());
    }

    @Test
    void remove_atTheMiddleOfTheHistory() {
        historyManager.remove(task1.getId());

        List<Task> expectedHistory = List.of(subTask1, epic1, epic2, task2);
        assertArrayEquals(expectedHistory.toArray(), historyManager.getHistory().toArray());
    }

    @Test
    void remove_atTheEndOfTheHistory() {
        historyManager.remove(task2.getId());

        List<Task> expectedHistory = List.of(subTask1, epic1, task1, epic2);
        assertArrayEquals(expectedHistory.toArray(), historyManager.getHistory().toArray());
    }
}