package services.task_manager;

import services.history_manager.HistoryManager;
import models.Epic;
import utils.TaskStatus;
import models.SubTask;
import models.Task;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryTaskManager implements TaskManager {
    private int generatedId = 0;
    protected final HistoryManager historyManager;
    protected final Map<Integer, Task> tasks;
    protected final Map<Integer, Epic> epics;
    protected final Map<Integer, SubTask> subTasks;

    public InMemoryTaskManager(HistoryManager historyManager) {
        tasks = new HashMap<>();
        epics = new HashMap<>();
        subTasks = new HashMap<>();
        this.historyManager = historyManager;
    }

    protected void setGeneratedId(int generatedId) {
        this.generatedId = generatedId;
    }

    protected int generateId() {
        return ++generatedId;
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    @Override
    public Task createTask(Task task) {
        task.setId(generateId());
        tasks.put(task.getId(), task);
        return task;
    }

    @Override
    public Task getTask(int id) {
        if (tasks.containsKey(id)) {
            historyManager.add(tasks.get(id));
            return tasks.get(id);
        }
        return null;
    }

    @Override
    public List<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public void updateTask(Task updatedTask) {
        if (tasks.get(updatedTask.getId()) == null) {
            return;
        }

        tasks.put(updatedTask.getId(), updatedTask);
    }

    @Override
    public void removeAllTasks() {
        for (Task task : tasks.values()) {
            historyManager.remove(task.getId());
        }

        tasks.clear();
    }

    @Override
    public void removeTask(int id) {
        tasks.remove(id);
        historyManager.remove(id);
    }

    @Override
    public Epic createEpic(Epic epic) {
        epic.setId(generateId());
        epics.put(epic.getId(), epic);
        return epic;
    }

    @Override
    public void updateEpic(Epic updatedEpic) {
        if (epics.get(updatedEpic.getId()) == null) {
            return;
        }

        epics.put(updatedEpic.getId(), updatedEpic);
    }

    @Override
    public List<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public Epic getEpic(int id) {
        if (epics.containsKey(id)) {
            historyManager.add(epics.get(id));
            return epics.get(id);
        }
        return null;
    }

    @Override
    public List<SubTask> getEpicsSubTasks(Epic epic) {
        List<SubTask> epicsSubTasks = new ArrayList<>();

        for (SubTask subTask : epic.getSubTasks()) {
            if (subTasks.containsKey(subTask.getId())) {
                epicsSubTasks.add(subTasks.get(subTask.getId()));
            }
        }
        return epicsSubTasks;
    }

    @Override
    public void removeEpicsSubtasks(Epic epic) {
        for (SubTask subTask : epic.getSubTasks()) {
            subTasks.remove(subTask.getId());
            historyManager.remove(subTask.getId());
        }

        epic.removeEpicsSubTasks();
        updateEpicStatus(epic);
    }

    @Override
    public void removeEpic(int id) {
        if (epics.containsKey(id)) {
            removeEpicsSubtasks(epics.get(id));
            epics.remove(id);

            historyManager.remove(id);
        }
    }

    protected void updateEpicStatus(Epic epic) {
        if (epic.getSubTasks().isEmpty()) {
            epic.setStatus(TaskStatus.NEW);
            return;
        }

        boolean isContainsNewTasks = false;
        boolean isContainsInProgressTasks = false;
        boolean isContainsDoneTasks = false;

        for (SubTask subTask : epic.getSubTasks()) {
            if (subTasks.get(subTask.getId()).getStatus() == TaskStatus.NEW) {
                isContainsNewTasks = true;
            } else if (subTasks.get(subTask.getId()).getStatus() == TaskStatus.IN_PROGRESS) {
                isContainsInProgressTasks = true;
            } else if (subTasks.get(subTask.getId()).getStatus() == TaskStatus.DONE) {
                isContainsDoneTasks = true;
            }
        }

        if (isContainsNewTasks && !isContainsInProgressTasks && !isContainsDoneTasks) {
            epic.setStatus(TaskStatus.NEW);
        } else if (!isContainsNewTasks && !isContainsInProgressTasks && isContainsDoneTasks) {
            epic.setStatus(TaskStatus.DONE);
        } else {
            epic.setStatus(TaskStatus.IN_PROGRESS);
        }
    }


    @Override
    public SubTask createSubTask(SubTask subTask) {
        subTask.setId(generateId());
        subTasks.put(subTask.getId(), subTask);

        Epic epic = epics.get(subTask.getEpicId());
        epic.addSubtask(subTask);
        updateEpicStatus(epic);

        return subTask;
    }

    @Override
    public void removeSubTask(int id) {
        if (subTasks.containsKey(id)) {
            SubTask subTask = subTasks.get(id);
            Epic epic = epics.get(subTask.getEpicId());
            epic.removeSubtask(subTask);
            subTasks.remove(subTask.getId());
            updateEpicStatus(epic);

            historyManager.remove(id);
        }
    }

    @Override
    public void removeAllSubTasks() {
        for (Epic epic : epics.values()) {
            epic.removeEpicsSubTasks();
        }
        subTasks.clear();
    }

    @Override
    public List<SubTask> getAllSubtasks() {
        return new ArrayList<>(subTasks.values());
    }

    @Override
    public SubTask getSubtask(int id) {
        if (subTasks.containsKey(id)) {
            historyManager.add(subTasks.get(id));
            return subTasks.get(id);
        }
        return null;
    }

    @Override
    public SubTask updateSubTask(SubTask updatedSubTask) {
        if (subTasks.get(updatedSubTask.getId()) == null) {
            return null;
        }

        Epic epic = epics.get(updatedSubTask.getEpicId());
        updateEpicStatus(epic);

        subTasks.put(updatedSubTask.getId(), updatedSubTask);
        return updatedSubTask;
    }
}
