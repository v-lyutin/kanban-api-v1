package service.task_manager;

import exceptions.DateTimeFormatException;
import exceptions.TaskValidateDateTimeException;
import models.Epic;
import models.SubTask;
import models.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import utils.DateTimeFormatHandler;
import utils.TaskStatus;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {
    @BeforeEach
    void setUp() {
        init();
        manager = new InMemoryTaskManager(historyManager);
    }

    @Test
    void createTask_whenTaskIsNull() {
        assertNull(manager.createTask(null));
    }

    @Test
    void createTask_whenTaskIsNotNull() {
        assertNotNull(manager.createTask(task1));
        assertNotNull(manager.createTask(task2));
        assertEquals(1, task1.getId());
        assertEquals(2, task2.getId());
    }

    @Test
    void createTask_validate_whenIsAnIntersectionInTime() {
        //"Task 3", "Task 3 description", "2023.09.29, 10:00", 120);
        manager.createTask(task3);

        assertThrows(TaskValidateDateTimeException.class, () ->
                manager.createTask(new Task("...", "...", "2024.09.29, 09:00", 60)));

        assertThrows(TaskValidateDateTimeException.class, () ->
                manager.createTask(new Task("...", "...", "2024.09.29, 09:00", 120)));
        assertThrows(TaskValidateDateTimeException.class, () ->
                manager.createTask(new Task("...", "...", "2024.09.29, 10:30", 30)));
        assertThrows(TaskValidateDateTimeException.class, () ->
                manager.createTask(new Task("...", "...", "2024.09.29, 12:00", 500)));
    }

    @Test
    void createTask_validate_whenDateIsEarlierThanCurrentTime() {
        assertThrows(TaskValidateDateTimeException.class, () ->
                manager.createTask(new Task("...", "...", "2023.09.01, 10:30", 30)));
    }

    @Test
    void createTask_DateTimeFormatException() {
        assertDoesNotThrow(() -> manager.createTask(
                new Task("...", "...", "2099-01-01, 10:00", 120)
        ));
        assertDoesNotThrow(() -> manager.createTask(
                new Task("...", "...", "2099.01.02, 10:00", 120)
        ));
        assertDoesNotThrow(() -> manager.createTask(
                new Task("...", "...", "03.01.2099, 10:00", 120)
        ));
        assertDoesNotThrow(() -> manager.createTask(
                new Task("...", "...", "01/04/2099, 10:00", 120)
        ));
        assertDoesNotThrow(() -> manager.createTask(
                new Task("...", "...", "05.01.2099 - 10:00", 120)
        ));
        assertThrows(DateTimeFormatException.class, () -> manager.createTask(
                new Task("...", "...", "2023.11/05, 10:00", 120)
        ));
    }

    @Test
    void createEpic_whenEpicIsNull() {
        assertNull(manager.createTask(null));
    }

    @Test
    void createEpic_whenEpicIsNotNull() {
        assertNotNull(manager.createEpic(epic1));
        assertNotNull(manager.createEpic(epic2));
        assertEquals(1, epic1.getId());
        assertEquals(2, epic2.getId());
    }

    @Test
    void createSubtask_whenSubtaskIsNull() {
        assertNull(manager.createSubTask(subTask1));
    }

    @Test
    void createSubtask_whenTheWrongEpicsId() {
        SubTask subTask = new SubTask("Subtask", "Description", 666);
        assertNull(manager.createSubTask(subTask));
    }

    @Test
    void createSubtask_whenSubTaskIsNotNull() {
        manager.createEpic(epic1);
        subTask1 = new SubTask("Subtask1", "Description", epic1.getId());
        subTask2 = new SubTask("Subtask2", "Description", epic1.getId());

        assertNotNull(manager.createSubTask(subTask1));
        assertNotNull(manager.createSubTask(subTask2));
        assertEquals(1, subTask1.getEpicId());
        assertEquals(1, subTask2.getEpicId());
    }

    @Test
    void createSubtask_validate_whenIsAnIntersectionInTime() {
        manager.createEpic(epic1);
        subTask3 = new SubTask("Subtask 3", "Subtask 3 description", "2023.09.15, 11:00", 500, epic1.getId());
        manager.createSubTask(subTask3);

        assertThrows(TaskValidateDateTimeException.class, () ->
                manager.createSubTask(
                        new SubTask("...", "...", "2023.09.15, 10:00", 60, epic1.getId())));
        assertThrows(TaskValidateDateTimeException.class, () ->
                manager.createSubTask(
                        new SubTask("...", "...", "2023.09.15, 11:30", 60, epic1.getId())));
        assertThrows(TaskValidateDateTimeException.class, () ->
                manager.createSubTask(
                        new SubTask("...", "...", "2023.09.15, 19:00", 60, epic1.getId())));

    }

    @Test
    void createSubtask_validate_whenDateIsEarlierThanCurrentTime() {
        manager.createEpic(epic1);
        assertThrows(TaskValidateDateTimeException.class, () ->
                manager.createSubTask(new SubTask("...", "...", "2023.09.01, 10:00", 120,  epic1.getId())));
    }

    @Test
    void createSubtask_DateTimeFormatException() {
        manager.createEpic(epic1);

        assertDoesNotThrow(() -> manager.createSubTask(
                new SubTask("...", "...", "2023-12-12, 12:12", 120, epic1.getId())
        ));
        assertDoesNotThrow(() -> manager.createSubTask(
                new SubTask("...", "...", "2023.12.13, 12:12", 120, epic1.getId())
        ));
        assertDoesNotThrow(() -> manager.createSubTask(
                new SubTask("...", "...", "14-12-2023, 12:12", 120, epic1.getId())
        ));
        assertDoesNotThrow(() -> manager.createSubTask(
                new SubTask("...", "...", "15.12.2023, 12:12", 120, epic1.getId())
        ));
        assertDoesNotThrow(() -> manager.createSubTask(
                new SubTask("...", "...", "12/16/2023, 12:12", 120, epic1.getId())
        ));
        assertDoesNotThrow(() -> manager.createSubTask(
                new SubTask("...", "...", "17.12.2023 - 12:12", 120, epic1.getId())
        ));
        assertThrows(DateTimeFormatException.class, () -> manager.createSubTask(
                new SubTask("...", "...", "18 января 2023 года, 12:12", 120, epic1.getId())
        ));
    }

    @Test
    void updateEpicStatus_whenThereAreNoSubTasks() {
        manager.createEpic(epic1);

        assertEquals(TaskStatus.NEW, epic1.getStatus());
    }

    @Test
    void updateEpicStatus_whenAllSubTasksAreNew() {
        manager.createEpic(epic1);
        subTask1 = new SubTask("Subtask1", "Description", epic1.getId());
        subTask2 = new SubTask("Subtask2", "Description", epic1.getId());
        manager.createSubTask(subTask1);
        manager.createSubTask(subTask2);

        assertEquals(TaskStatus.NEW, epic1.getStatus());
    }

    @Test
    void updateEpicStatus_whenAllSubTasksAreDone() {
        manager.createEpic(epic1);
        subTask1 = new SubTask("Subtask1", "Description", epic1.getId());
        subTask2 = new SubTask("Subtask2", "Description", epic1.getId());
        subTask1.setStatus(TaskStatus.DONE);
        subTask2.setStatus(TaskStatus.DONE);
        manager.createSubTask(subTask1);
        manager.createSubTask(subTask2);

        assertEquals(TaskStatus.DONE, epic1.getStatus());
    }

    @Test
    void updateEpicStatus_whenAllSubTasksAreInProgress() {
        manager.createEpic(epic1);
        subTask1 = new SubTask("Subtask1", "Description", epic1.getId());
        subTask2 = new SubTask("Subtask2", "Description", epic1.getId());
        subTask1.setStatus(TaskStatus.IN_PROGRESS);
        subTask2.setStatus(TaskStatus.IN_PROGRESS);
        manager.createSubTask(subTask1);
        manager.createSubTask(subTask2);

        assertEquals(TaskStatus.IN_PROGRESS, epic1.getStatus());
    }

    @Test
    void updateEpicStatus_whenSubTasksAreInProgressAndDone() {
        manager.createEpic(epic1);
        subTask1 = new SubTask("Subtask1", "Description", epic1.getId());
        subTask2 = new SubTask("Subtask2", "Description", epic1.getId());
        subTask1.setStatus(TaskStatus.IN_PROGRESS);
        subTask2.setStatus(TaskStatus.DONE);
        manager.createSubTask(subTask1);
        manager.createSubTask(subTask2);

        assertEquals(TaskStatus.IN_PROGRESS, epic1.getStatus());
    }

    @Test
    void updateEpicStatus_whenSubtasksAreAddedAndDeleted() {
        manager.createEpic(epic1);
        subTask1 = new SubTask("Subtask1", "Description", epic1.getId());
        subTask2 = new SubTask("Subtask2", "Description", epic1.getId());
        subTask1.setStatus(TaskStatus.DONE);
        subTask2.setStatus(TaskStatus.DONE);
        manager.createSubTask(subTask1);
        manager.createSubTask(subTask2);
        assertEquals(TaskStatus.DONE, epic1.getStatus());
        manager.removeSubTask(subTask1.getId());
        subTask2.setStatus(TaskStatus.IN_PROGRESS);
        manager.updateSubTask(subTask2);
        assertEquals(TaskStatus.IN_PROGRESS, epic1.getStatus());
        manager.removeSubTask(subTask2.getId());
        assertEquals(TaskStatus.NEW, epic1.getStatus());
    }

    @Test
    void getTask_whenTaskHasWrongId() {
        assertNull(manager.getTask(666));
        assertEquals(0, manager.getHistory().size());
    }

    @Test
    void getTask_whenTaskHasCorrectId() {
        task1 = manager.createTask(task1);
        assertNotNull(manager.getTask(task1.getId()));
        List<Task> expectedHistory = List.of(task1);

        assertArrayEquals(expectedHistory.toArray(), manager.getHistory().toArray());
    }

    @Test
    void getEpic_whenEpicHasWrongId() {
        assertNull(manager.getEpic(666));
        assertEquals(0, manager.getHistory().size());
    }

    @Test
    void getEpic_whenEpicHasCorrectId() {
        epic1 = manager.createEpic(epic1);
        List<Task> expectedHistory = List.of(epic1);

        assertNotNull(manager.getEpic(epic1.getId()));
        assertArrayEquals(expectedHistory.toArray(), manager.getHistory().toArray());
    }

    @Test
    void getSubtask_whenSubtaskHasWrongId() {
        assertNull(manager.getSubtask(666));
        assertEquals(0, manager.getHistory().size());
    }

    @Test
    void getSubtask_whenSubtaskHasCorrectId() {
        manager.createEpic(epic1);
        subTask1 = new SubTask("Subtask1", "Description", epic1.getId());
        manager.createSubTask(subTask1);
        List<Task> expectedHistory = List.of(subTask1);

        assertNotNull(manager.getSubtask(subTask1.getId()));
        assertArrayEquals(expectedHistory.toArray(), manager.getHistory().toArray());
    }

    @Test
    void getAllTasks_whenThereAreNoTasks() {
        assertEquals(0, manager.getAllTasks().size());
    }

    @Test
    void getAllTasks_whenTasksAreExists() {
        manager.createTask(task1);
        manager.createTask(task2);

        List<Task> expectedValue = List.of(task1, task2);

        assertArrayEquals(expectedValue.toArray(), manager.getAllTasks().toArray());
    }

    @Test
    void getAllEpics_whenThereAreNoEpics() {
        assertEquals(0, manager.getAllEpics().size());
    }

    @Test
    void getAllEpics_whenEpicsAreExists() {
        manager.createEpic(epic1);
        manager.createEpic(epic2);

        List<Task> expectedValue = List.of(epic1, epic2);

        assertArrayEquals(expectedValue.toArray(), manager.getAllEpics().toArray());
    }

    @Test
    void getAllSubtasks_whenThereAreNoSubtasks() {
        assertEquals(0, manager.getAllSubtasks().size());
    }

    @Test
    void getAllSubtasks_whenSubtasksAreExists() {
        manager.createEpic(epic1);
        subTask1 = new SubTask("Subtask1", "Description", epic1.getId());
        subTask2 = new SubTask("Subtask2", "Description", epic1.getId());
        manager.createSubTask(subTask1);
        manager.createSubTask(subTask2);

        List<Task> expectedValue = List.of(subTask1, subTask2);

        assertArrayEquals(expectedValue.toArray(), manager.getAllSubtasks().toArray());
    }

    @Test
    void getEpicsSubtasks_whenEpicHasNoSubtasks() {
        manager.createEpic(epic1);
        assertEquals(0, manager.getEpicsSubTasks(epic1.getId()).size());
    }

    @Test
    void getEpicsSubtasks_whenEpicHasWrongId() {
        assertNull(manager.getEpicsSubTasks(666));
    }

    @Test
    void getEpicsSubtasks_whenEpicHasSubtasks() {
        manager.createEpic(epic1);
        subTask1 = new SubTask("Subtask1", "Description", epic1.getId());
        subTask2 = new SubTask("Subtask2", "Description", epic1.getId());
        manager.createSubTask(subTask1);
        manager.createSubTask(subTask2);

        List<Task> expectedValue = List.of(subTask1, subTask2);

        assertArrayEquals(expectedValue.toArray(), manager.getEpicsSubTasks(epic1.getId()).toArray());
    }

    @Test
    void updateTask_whenUpdatedTasksIdIsNotExist() {
        assertNull(manager.updateTask(task1));
    }

    @Test
    void updateTask_whenUpdatedTasksIdIsExist() {
        manager.createTask(task1);
        task1.setStatus(TaskStatus.IN_PROGRESS);
        task1.setDescription("Writing tests is a nightmare...");
        Task expectedTask = task1;

        assertEquals(expectedTask, manager.updateTask(task1));
    }

    @Test
    void updateEpic_whenUpdatedEpicsIdIsNotExist() {
        assertNull(manager.updateEpic(epic1));
    }

    @Test
    void updateEpic_whenUpdatedEpicsIdIsExist() {
        manager.createEpic(epic1);
        epic1.setTitle("Updated title");
        Epic expectedEpic = epic1;

        assertEquals(expectedEpic, manager.updateEpic(epic1));
    }

    @Test
    void updateSubtask_whenUpdatedSubtasksIdIsNotExist() {
        assertNull(manager.updateSubTask(subTask1));
    }

    @Test
    void updateSubtask_whenUpdatedSubtasksIdIsExist() {
        manager.createEpic(epic1);
        subTask1 = new SubTask("Subtask1", "Description", epic1.getId());
        manager.createSubTask(subTask1);
        subTask1.setStatus(TaskStatus.DONE);
        SubTask expectedSubtask = subTask1;

        assertEquals(expectedSubtask, manager.updateSubTask(subTask1));
    }

    @Test
    void removeTask_whenTaskHasWrongId() {
        manager.createTask(task1);
        manager.createTask(task2);

        List<Task> expectedValue = List.of(task1, task2);
        manager.removeTask(666);

        assertArrayEquals(expectedValue.toArray(), manager.getAllTasks().toArray());
    }

    @Test
    void removeTask_whenTaskHasCorrectId() {
        manager.createTask(task1);
        manager.createTask(task2);

        List<Task> expectedValue = List.of(task2);
        manager.removeTask(task1.getId());

        assertArrayEquals(expectedValue.toArray(), manager.getAllTasks().toArray());

        manager.removeTask(task2.getId());

        assertArrayEquals(Collections.emptyList().toArray(), manager.getAllTasks().toArray());
    }

    @Test
    void removeEpic_whenEpicHasWrongId() {
        manager.createEpic(epic1);
        manager.createEpic(epic2);

        List<Task> expectedValue = List.of(epic1, epic2);
        manager.removeEpic(666);

        assertArrayEquals(expectedValue.toArray(), manager.getAllEpics().toArray());
    }

    @Test
    void removeEpic_whenEpicHasCorrectId() {
        manager.createEpic(epic1);
        manager.createEpic(epic2);

        List<Task> expectedValue = List.of(epic1);
        manager.removeEpic(epic2.getId());

        assertArrayEquals(expectedValue.toArray(), manager.getAllEpics().toArray());

        manager.removeEpic(epic1.getId());

        assertArrayEquals(Collections.emptyList().toArray(), manager.getAllEpics().toArray());
    }

    @Test
    void removeSubtask_whenSubtaskHasWrongId() {
        manager.createEpic(epic1);
        subTask1 = new SubTask("Subtask1", "Description", epic1.getId());
        manager.createSubTask(subTask1);
        manager.removeSubTask(666);

        assertArrayEquals(List.of(subTask1).toArray(), manager.getAllSubtasks().toArray());
    }

    @Test
    void removeSubtask_whenSubtaskHasCorrectId() {
        manager.createEpic(epic1);
        subTask1 = new SubTask("Subtask1", "Description", epic1.getId());
        manager.createSubTask(subTask1);
        manager.removeSubTask(subTask1.getId());

        assertArrayEquals(Collections.emptyList().toArray(), manager.getAllSubtasks().toArray());
    }

    @Test
    void removeAllTasks_whenThereAreNoTasks() {
        assertDoesNotThrow(() -> manager.removeAllTasks());
    }

    @Test
    void removeAllTasks_whenThereAreTasks() {
        manager.createTask(task1);
        manager.createTask(task2);
        manager.removeAllTasks();

        assertArrayEquals(Collections.emptyList().toArray(), manager.getAllTasks().toArray());
    }

    @Test
    void removeAllSubtasks_whenThereAreNoSubtasks() {
        assertDoesNotThrow(() -> manager.removeAllSubTasks());
    }

    @Test
    void removeAllSubtasks_whenThereAreSubtasks() {
        manager.createEpic(epic1);
        manager.createEpic(epic2);
        subTask1 = new SubTask("Subtask1", "Description", epic1.getId());
        subTask2 = new SubTask("Subtask2", "Description", epic2.getId());
        manager.createSubTask(subTask1);
        manager.createSubTask(subTask2);
        manager.removeAllSubTasks();

        assertArrayEquals(Collections.emptyList().toArray(), manager.getAllSubtasks().toArray());
    }

    @Test
    void removeEpicsSubtasks_whenEpicHasWrongId() {
        assertDoesNotThrow(() -> manager.removeEpicsSubtasks(666));
    }

    @Test
    void removeEpicsSubtasks_whenEpicHasCorrectId() {
        manager.createEpic(epic1);
        subTask1 = new SubTask("Subtask1", "Description", epic1.getId());
        subTask2 = new SubTask("Subtask2", "Description", epic1.getId());
        manager.createSubTask(subTask1);
        manager.createSubTask(subTask2);
        manager.removeEpicsSubtasks(epic1.getId());

        assertArrayEquals(Collections.emptyList().toArray(), manager.getEpicsSubTasks(epic1.getId()).toArray());
    }

    @Test
    void getHistory_whenHistoryIsEmpty() {
        assertEquals(0, manager.getHistory().size());
    }

    @Test
    void getHistory_whenHistoryIsNotEmpty() {
        manager.createTask(task1);
        manager.createTask(task2);
        manager.createEpic(epic1);
        manager.createEpic(epic2);
        subTask1 = new SubTask("Subtask1", "Description", epic1.getId());
        manager.createSubTask(subTask1);

        manager.getSubtask(subTask1.getId());
        manager.getTask(task2.getId());
        manager.getEpic(epic2.getId());
        manager.getSubtask(subTask1.getId());
        manager.getEpic(epic1.getId());
        manager.getTask(task1.getId());

        List<Task> expectedHistory = List.of(task2, epic2, subTask1, epic1, task1);
        assertArrayEquals(expectedHistory.toArray(), manager.getHistory().toArray());
    }

    @Test
    void getPrioritizedTasks_whenThereIsNoTasks() {
        assertEquals(0, manager.getPrioritizedTasks().size());
    }

    @Test
    void getPrioritizedTasks_whenAllTasksWithDate() {
        manager.createTask(task3);
        manager.createTask(task4);
        manager.createTask(task5);
        manager.createEpic(epic1);
        subTask3 = new SubTask("Subtask 3", "Subtask 3 description", "2024.09.15, 11:00", 500, epic1.getId());
        manager.createSubTask(subTask3);
        subTask4 = new SubTask("Subtask 4", "Subtask 4 description", "2024.09.15, 06:00", 120, epic1.getId());
        manager.createSubTask(subTask4);
        subTask5 = new SubTask("Subtask 5", "Subtask 5 description", "2024.09.20, 11:00", 120, epic1.getId());
        manager.createSubTask(subTask5);

        List<Task> expectedValue = List.of(task5, task4, subTask4, subTask3, subTask5, task3);
        assertArrayEquals(expectedValue.toArray(), manager.prioritizedTasks.toArray());
    }

    @Test
    void getPrioritizedTasks_whenTasksWithDateAndWithoutDate() {
        manager.createTask(task1);
        manager.createTask(task3);
        manager.createTask(task5);
        manager.createEpic(epic1);
        subTask3 = new SubTask("Subtask 3", "Subtask 3 description", "2024.09.15, 11:00", 500, epic1.getId());
        manager.createSubTask(subTask3);
        subTask1 = new SubTask("Subtask1", "Description", epic1.getId());
        manager.createSubTask(subTask1);

        List<Task> expectedValue = List.of(task5, subTask3, task3, task1, subTask1);
        assertArrayEquals(expectedValue.toArray(), manager.prioritizedTasks.toArray());
    }

    @Test
    void getPrioritizedTasks_whenAllTasksWithoutDate() {
        manager.createTask(task1);
        manager.createTask(task2);
        manager.createEpic(epic1);
        subTask1 = new SubTask("Subtask1", "Description", epic1.getId());
        manager.createSubTask(subTask1);
        subTask2 = new SubTask("Subtask2", "Description", epic1.getId());
        manager.createSubTask(subTask2);

        List<Task> expectedValue = List.of(task1, task2, subTask1, subTask2);
        assertArrayEquals(expectedValue.toArray(), manager.prioritizedTasks.toArray());
    }

    @Test
    void getEndTime_getStartTime_whenTaskDoesntHaveTime() {
        manager.createTask(task1);
        assertNull(task1.getEndTime());
        assertNull(task1.getStartTime());
    }

    @Test
    void getEndTime_getStartTime_whenTaskHasTime() {
        manager.createTask(task3);
        LocalDateTime expectedStartTime = DateTimeFormatHandler.parseDateFromString("2024.09.29, 10:00");
        LocalDateTime expectedEndTime = DateTimeFormatHandler.parseDateFromString("29.09.2024 - 12:00");
        assertEquals(expectedStartTime, task3.getStartTime());
        assertEquals(expectedEndTime, task3.getEndTime());
    }

    @Test
    void getEndTime_getStartTime_whenEpicsSubtasksDoesntHaveTime() {
        manager.createEpic(epic1);
        subTask1 = new SubTask("Subtask1", "Description", epic1.getId());
        manager.createSubTask(subTask1);
        subTask2 = new SubTask("Subtask2", "Description", epic1.getId());
        manager.createSubTask(subTask2);
        assertNull(epic1.getStartTime());
        assertNull(epic1.getEndTime());
    }

    @Test
    void getEndTime_getStartTime_whenEpicsSubtasksHaveTime() {
        manager.createEpic(epic1);
        subTask1 = new SubTask("Subtask1", "Description", "2023.10.10, 10:00", 120, epic1.getId());
        manager.createSubTask(subTask1);
        subTask2 = new SubTask("Subtask2", "Description", "2023.10.11, 11:00", 120, epic1.getId());
        manager.createSubTask(subTask2);

        LocalDateTime expectedStartTime = DateTimeFormatHandler.parseDateFromString("2023.10.10, 10:00");
        LocalDateTime expectedEndTime = DateTimeFormatHandler.parseDateFromString("2023.10.11, 13:00");

        assertEquals(expectedStartTime, epic1.getStartTime());
        assertEquals(expectedEndTime, epic1.getEndTime());
    }

   @Test
   void getDuration_whenEpicDoesntHasTime() {
        manager.createEpic(epic1);
        assertNull(epic1.getDuration());
    }

    @Test
    void getDuration_whenEpicHasTime() {
        manager.createEpic(epic1);
        subTask1 = new SubTask("Subtask1", "Description", "2023.10.10, 10:00", 120, epic1.getId());
        manager.createSubTask(subTask1);
        subTask2 = new SubTask("Subtask2", "Description", "2023.10.11, 11:00", 120, epic1.getId());
        manager.createSubTask(subTask2);

        Duration expectedDuration = Duration.ofMinutes(240);
        assertEquals(expectedDuration, epic1.getDuration());
    }
}