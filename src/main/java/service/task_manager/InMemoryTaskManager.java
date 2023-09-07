package service.task_manager;

import exceptions.TaskValidateDateTimeException;
import service.history_manager.HistoryManager;
import models.Epic;
import utils.TaskStatus;
import models.SubTask;
import models.Task;
import java.time.LocalDateTime;
import java.util.*;

public class InMemoryTaskManager implements TaskManager {
    private int generatedId = 0;
    protected final HistoryManager historyManager;
    protected final Map<Integer, Task> tasks;
    protected final Map<Integer, Epic> epics;
    protected final Map<Integer, SubTask> subTasks;
    protected final Set<Task> prioritizedTasks;
    private final Comparator<Task> comparator = (t1, t2) -> {
        if (t1 == null || t2 == null) {
            return 0;
        }

        if (t1.getStartTime() != null && t2.getStartTime() != null) {
            if (t1.getStartTime().isBefore(t2.getStartTime())) {
                return -1;
            } else if (t1.getStartTime().isAfter(t2.getStartTime())) {
                return 1;
            } else {
                return 0;
            }
        } else if (t1.getStartTime() == null && t2.getStartTime() != null) {
            return 1;
        } else if (t1.getStartTime() != null && t2.getStartTime() == null) {
            return -1;
        } else {
            return t1.getId() - t2.getId();
        }
    };

    public InMemoryTaskManager(HistoryManager historyManager) {
        tasks = new HashMap<>();
        epics = new HashMap<>();
        subTasks = new HashMap<>();
        this.historyManager = historyManager;
        prioritizedTasks = new TreeSet<>(comparator);
    }

    protected void setGeneratedId(int generatedId) {
        this.generatedId = generatedId;
    }

    protected int generateId() {
        return ++generatedId;
    }

    @Override
    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(prioritizedTasks);
    }

    private void validate(Task task) {
        if (task.getStartTime() == null) {
            return;
        }

        if (task.getStartTime().isBefore(LocalDateTime.now())) {
            throw new TaskValidateDateTimeException("[!] Начало задачи не должно быть раньше текущего времени");
        }

        LocalDateTime startTime = task.getStartTime();
        LocalDateTime endTime = task.getEndTime();

        boolean isThereAnIntersectionInTime = prioritizedTasks.stream()
                .filter(t1 -> t1.getStartTime() != null)
                .allMatch(t1 ->
                        startTime.isBefore(t1.getStartTime()) && endTime.isBefore(t1.getStartTime()) ||
                        startTime.isAfter(t1.getEndTime()) && endTime.isAfter(t1.getEndTime()));


        if (!isThereAnIntersectionInTime) {
            throw new TaskValidateDateTimeException("[!] Задачи не должны пересекаться по времени");
        }
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    @Override
    public Task createTask(Task task) {
        if (task == null) {
            return null;
        }

        validate(task);

        task.setId(generateId());
        tasks.put(task.getId(), task);
        prioritizedTasks.add(task);
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
    public Task updateTask(Task updatedTask) {
        if (tasks.get(updatedTask.getId()) == null) {
            return null;
        }

        validate(updatedTask);

        tasks.put(updatedTask.getId(), updatedTask);
        prioritizedTasks.add(updatedTask);

        return updatedTask;
    }

    @Override
    public void removeAllTasks() {
        for (Task task : tasks.values()) {
            historyManager.remove(task.getId());
            prioritizedTasks.remove(task);
        }

        tasks.clear();
    }

    @Override
    public void removeTask(int id) {
        if (!tasks.containsKey(id)) {
            return;
        }

        tasks.remove(id);
        historyManager.remove(id);
        prioritizedTasks.remove(tasks.get(id));
    }

    @Override
    public Epic createEpic(Epic epic) {
        if (epic == null) {
            return null;
        }

        epic.setId(generateId());
        epics.put(epic.getId(), epic);
        return epic;
    }

    @Override
    public Epic updateEpic(Epic updatedEpic) {
        if (epics.get(updatedEpic.getId()) == null) {
            return null;
        }

        epics.put(updatedEpic.getId(), updatedEpic);
        return updatedEpic;
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
    public List<SubTask> getEpicsSubTasks(int id) {
        List<SubTask> epicsSubTasks = new ArrayList<>();

        if (!epics.containsKey(id)) {
            return null;
        }

        Epic epic = epics.get(id);
        for (SubTask subTask : epic.getSubTasks()) {
            if (subTasks.containsKey(subTask.getId())) {
                epicsSubTasks.add(subTasks.get(subTask.getId()));
            }
        }
        return epicsSubTasks;
    }

    @Override
    public void removeEpicsSubtasks(int id) {
        if (!epics.containsKey(id)) {
            return;
        }

        Epic epic = epics.get(id);

        for (SubTask subTask : epic.getSubTasks()) {
            subTasks.remove(subTask.getId());
            historyManager.remove(subTask.getId());
            prioritizedTasks.remove(subTask);
        }

        epic.removeEpicsSubTasks();
        updateEpicStatus(epic);
    }

    @Override
    public void removeEpic(int id) {
        if (epics.containsKey(id)) {
            removeEpicsSubtasks(id);
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
        if (subTask == null) {
            return null;
        }

        validate(subTask);

        if (epics.containsKey(subTask.getEpicId())) {
            subTask.setId(generateId());
            subTasks.put(subTask.getId(), subTask);
            prioritizedTasks.add(subTask);

            Epic epic = epics.get(subTask.getEpicId());
            epic.addSubtask(subTask);
            updateEpicStatus(epic);

            return subTask;
        }

        return null;
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
            prioritizedTasks.remove(subTasks.get(id));
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

        validate(updatedSubTask);

        Epic epic = epics.get(updatedSubTask.getEpicId());
        updateEpicStatus(epic);

        subTasks.put(updatedSubTask.getId(), updatedSubTask);
        prioritizedTasks.add(updatedSubTask);
        return updatedSubTask;
    }
}
