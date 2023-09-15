package service.task_manager;

import models.Epic;
import models.SubTask;
import models.Task;
import java.util.List;

public interface TaskManager {
    List<Task> getHistory();

    List<Task> getPrioritizedTasks();

    Task createTask(Task task);

    Task getTask(int id);

    List<Task> getAllTasks();

    Task updateTask(Task updatedTask);

    void removeAllTasks();

    void removeTask(int id);

    Epic createEpic(Epic epic);

    Epic updateEpic(Epic updatedEpic);

    List<Epic> getAllEpics();

    Epic getEpic(int id);

    List<SubTask> getEpicsSubTasks(int id);

    void removeEpicSubtasks(int id);

    void removeEpic(int id);

    SubTask createSubTask(SubTask subTask);

    void removeSubTask(int id);

    void removeAllSubTasks();

    List<SubTask> getAllSubtasks();

    SubTask getSubtask(int id);

    SubTask updateSubTask(SubTask updatedSubTask);
}
