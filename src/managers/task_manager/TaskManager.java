package managers.task_manager;

import models.Epic;
import models.SubTask;
import models.Task;

import java.util.ArrayList;

public interface TaskManager {
    Task createTask(Task task);

    Task getTask(int id);

    ArrayList<Task> getAllTasks();

    void updateTask(Task updatedTask);

    void removeAllTasks();

    void removeTask(int id);

    Epic createEpic(Epic epic);

    void updateEpic(Epic updatedEpic);

    ArrayList<Epic> getAllEpics();

    Epic getEpic(int id);

    ArrayList<SubTask> getEpicsSubTasks(Epic epic);

    void removeEpicsSubtasks(Epic epic);

    void removeEpic(int id);

    SubTask createSubTask(SubTask subTask);

    void removeSubTask(SubTask subTask);

    void removeAllSubTasks();

    ArrayList<SubTask> getAllSubtasks();

    SubTask getSubtask(int id);

    SubTask updateSubTask(SubTask updatedSubTask);
}
