package service.task_manager;

import models.Epic;
import models.SubTask;
import models.Task;

import java.io.IOException;
import java.util.List;

public interface TaskManager {
    List<Task> getHistory() throws IOException;

    Task createTask(Task task) throws IOException;

    Task getTask(int id) throws IOException;

    List<Task> getAllTasks();

    void updateTask(Task updatedTask) throws IOException;

    void removeAllTasks() throws IOException;

    void removeTask(int id) throws IOException;

    Epic createEpic(Epic epic) throws IOException;

    void updateEpic(Epic updatedEpic) throws IOException;

    List<Epic> getAllEpics();

    Epic getEpic(int id) throws IOException;

    List<SubTask> getEpicsSubTasks(Epic epic);

    void removeEpicsSubtasks(Epic epic) throws IOException;

    void removeEpic(int id) throws IOException;

    SubTask createSubTask(SubTask subTask) throws IOException;

    void removeSubTask(int id) throws IOException;

    void removeAllSubTasks() throws IOException;

    List<SubTask> getAllSubtasks();

    SubTask getSubtask(int id) throws IOException;

    SubTask updateSubTask(SubTask updatedSubTask) throws IOException;
}
