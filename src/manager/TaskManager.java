package manager;

import model.Epic;
import model.Status;
import model.SubTask;
import model.Task;
import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {
    private int generatedId = 0;
    private final HashMap<Integer, Task> tasks = new HashMap<>();
    private final HashMap<Integer, Epic> epics = new HashMap<>();
    private final HashMap<Integer, SubTask> subTasks = new HashMap<>();

    private int generateId() {
        return ++generatedId;
    }

    public Task createTask(Task task) {
        task.setId(generateId());
        tasks.put(task.getId(), task);
        return task;
    }

    public Task getTaskById(int id) {
        return tasks.getOrDefault(id, null);
    }

    public ArrayList<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    public void updateTask(Task updatedTask) {
        if (tasks.get(updatedTask.getId()) == null) {
            return;
        }

        tasks.put(updatedTask.getId(), updatedTask);
    }

    public void removeAllTasks() {
        tasks.clear();
    }

    public void removeTask(int id) {
        tasks.remove(id);
    }

    public Epic createEpic(Epic epic) {
        epic.setId(generateId());
        epics.put(epic.getId(), epic);
        return epic;
    }

    public void updateEpic(Epic updatedEpic) {
        if (epics.get(updatedEpic.getId()) == null) {
            return;
        }

        epics.put(updatedEpic.getId(), updatedEpic);
    }

    public ArrayList<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    public Epic getEpicById(int id) {
        return epics.getOrDefault(id, null);
    }

    public ArrayList<SubTask> getAllEpicsSubTasks(Epic epic) {
        ArrayList<SubTask> epicsSubTasks = new ArrayList<>();

        for (SubTask subTask : epic.getSubTasks()) {
            if (subTasks.containsKey(subTask.getId())) {
                epicsSubTasks.add(subTasks.get(subTask.getId()));
            }
        }
        return epicsSubTasks;
    }

    public void removeEpicsSubtasks(Epic epic) {
        epic.getSubTasks().clear();
        updateEpicStatus(epic);
    }

    public void removeEpic(int id) {
        epics.remove(id);
    }

    private void updateEpicStatus(Epic epic) {
        if (epic.getSubTasks().isEmpty()) {
            epic.setStatus(Status.NEW);
            return;
        }

        boolean isContainsNewTasks = false;
        boolean isContainsInProgressTasks = false;
        boolean isContainsDoneTasks = false;

        for (SubTask subTask : epic.getSubTasks()) {
            if (subTasks.get(subTask.getId()).getStatus() == Status.NEW) {
                isContainsNewTasks = true;
            }
            else if (subTasks.get(subTask.getId()).getStatus() == Status.IN_PROGRESS) {
                isContainsInProgressTasks = true;
            }
            else if (subTasks.get(subTask.getId()).getStatus() == Status.DONE) {
                isContainsDoneTasks = true;
            }
        }

        if (isContainsNewTasks && !isContainsInProgressTasks && !isContainsDoneTasks) {
            epic.setStatus(Status.NEW);
        }
        else if (!isContainsNewTasks && !isContainsInProgressTasks && isContainsDoneTasks) {
            epic.setStatus(Status.DONE);
        }
        else {
            epic.setStatus(Status.IN_PROGRESS);
        }
    }

    public SubTask createSubTask(SubTask subTask) {
        subTask.setId(generateId());
        subTasks.put(subTask.getId(), subTask);

        Epic epic = epics.get(subTask.getEpicId());
        epic.addSubtask(subTask);
        updateEpicStatus(epic);

        return subTask;
    }

    public void removeSubTask(SubTask subTask) {
        if (subTasks.containsKey(subTask.getId())) {
            Epic epic = epics.get(subTask.getEpicId());
            epic.deleteSubtask(subTask);
            epics.remove(subTask.getId());
            updateEpicStatus(epic);
        }
    }

    public void removeAllSubTasks() {
        subTasks.clear();
    }

    public ArrayList<SubTask> getAllSubtasks() {
        return new ArrayList<>(subTasks.values());
    }

    public SubTask getSubtaskById(int id) {
        return subTasks.get(id);
    }

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
