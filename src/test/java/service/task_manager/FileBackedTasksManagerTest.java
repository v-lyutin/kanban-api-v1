package service.task_manager;

import models.SubTask;
import models.Task;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.ManagersService;
import service.history_manager.HistoryManager;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Collections;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class FileBackedTasksManagerTest extends TaskManagerTest<FileBackedTasksManager> {

    @BeforeEach
    void setUp() {
        init();
        manager = ManagersService.getDefault(historyManager);
    }

    @AfterEach
    void clearTheHistoryFile() {
        String fileName = "src/main/resources/tasks_history.csv";

        try (BufferedWriter bf = Files.newBufferedWriter(Path.of(fileName),
                StandardOpenOption.TRUNCATE_EXISTING)) {
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    void loadFromFile_whenHistoryIsEmpty() {
        manager.createTask(task1);
        manager.createTask(task2);
        manager.createEpic(epic1);

        HistoryManager recoveryHistoryManager = ManagersService.getDefaultHistory();
        FileBackedTasksManager recoveryManager = FileBackedTasksManager.loadFromFile(
                new File("src/main/resources/tasks_history.csv"), recoveryHistoryManager);

        assertArrayEquals(List.of(task1, task2).toArray(), recoveryManager.getAllTasks().toArray());
        assertArrayEquals(List.of(epic1).toArray(), recoveryManager.getAllEpics().toArray());
        assertArrayEquals(Collections.emptyList().toArray(), recoveryManager.getHistory().toArray());
    }

    @Test
    void loadFromFile_whenFileIsEmpty() {
        assertThrows(RuntimeException.class, () -> FileBackedTasksManager.loadFromFile(
                new File("src/test/resources/tasks_history_test.csv"),
                historyManager));
    }

    @Test
    void loadFromFile_whenFileIsNotEmptyAndHistoryIsNotEmpty() {
        manager.createTask(task1);
        manager.createTask(task2);
        manager.createEpic(epic1);
        manager.getTask(task2.getId());

        HistoryManager recoveryHistoryManager = ManagersService.getDefaultHistory();
        FileBackedTasksManager recoveryManager = FileBackedTasksManager.loadFromFile(
                new File("src/main/resources/tasks_history.csv"), recoveryHistoryManager);

        assertArrayEquals(List.of(task1, task2).toArray(), recoveryManager.getAllTasks().toArray());
        assertArrayEquals(List.of(epic1).toArray(), recoveryManager.getAllEpics().toArray());
        assertArrayEquals(List.of(task2).toArray(), recoveryManager.getHistory().toArray());
    }

    @Test
    void loadFromFile_checkPrioritizedTasks() {
        manager.createTask(task1);
        manager.createTask(task3);
        manager.createTask(task5);
        manager.createEpic(epic1);
        subTask3 = new SubTask("Subtask 3", "Subtask 3 description", "2023.09.15, 11:00", 500, epic1.getId());
        manager.createSubTask(subTask3);
        subTask4 = new SubTask("Subtask 4", "Subtask 4 description", "2023.09.15, 06:00", 120, epic1.getId());
        manager.createSubTask(subTask4);

        HistoryManager recoveryHistoryManager = ManagersService.getDefaultHistory();
        FileBackedTasksManager recoveryManager = FileBackedTasksManager.loadFromFile(
                new File("src/main/resources/tasks_history.csv"), recoveryHistoryManager);

        List<Task> expectedPrioritizedTasks = List.of(task5, subTask4, subTask3, task3, task1);
        assertArrayEquals(expectedPrioritizedTasks.toArray(), recoveryManager.getPrioritizedTasks().toArray());
    }

    @Test
    void save_saveEpicWithoutSubtasks() {
        manager.createEpic(epic1);

        HistoryManager recoveryHistoryManager = ManagersService.getDefaultHistory();
        FileBackedTasksManager recoveryManager = FileBackedTasksManager.loadFromFile(
                new File("src/main/resources/tasks_history.csv"), recoveryHistoryManager);

        assertArrayEquals(List.of(epic1).toArray(), recoveryManager.getAllEpics().toArray());
    }
}