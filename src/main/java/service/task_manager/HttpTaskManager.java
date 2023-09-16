package service.task_manager;

import client.KVTaskClient;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import models.Epic;
import models.SubTask;
import models.Task;
import service.history_manager.HistoryManager;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class HttpTaskManager extends FileBackedTasksManager {
    private final KVTaskClient client;
    private final Gson gson = new Gson();
    private static final String TASKS_KEY = "tasks";
    private static final String SUBTASKS_KEY = "subtasks";
    private static final String EPICS_KEY = "epics";
    private static final String HISTORY_KEY = "history";
    public HttpTaskManager(HistoryManager historyManager, int port) {
        super(historyManager);
        client = new KVTaskClient(port);
    }

    @Override
    protected void save() {
        if (!tasks.isEmpty()) {
            String jsonTasks = gson.toJson(new ArrayList<>(tasks.values()));
            client.put(TASKS_KEY, jsonTasks);
        }
        if (!subTasks.isEmpty()) {
            String jsonSubtasks = gson.toJson(new ArrayList<>(subTasks.values()));
            client.put(SUBTASKS_KEY, jsonSubtasks);
        }
        if (!epics.isEmpty()) {
            String jsonEpics = gson.toJson(new ArrayList<>(epics.values()));
            client.put(EPICS_KEY, jsonEpics);
        }
        if (!historyManager.getHistory().isEmpty()) {
            String jsonHistory = gson.toJson(new ArrayList<>(historyManager.getHistory()));
            client.put(HISTORY_KEY, jsonHistory);
        }
    }

    public static HttpTaskManager loadFromKVServer(HistoryManager historyManager, int port) {
        HttpTaskManager manager = new HttpTaskManager(historyManager, port);
        Gson gson = new Gson();
        Type tasksType = new TypeToken<ArrayList<Task>>() {}.getType();
        Type subtasksType = new TypeToken<ArrayList<SubTask>>() {}.getType();
        Type epicsType = new TypeToken<ArrayList<Epic>>() {}.getType();

        String jsonTasks = manager.client.load(TASKS_KEY);
        if (jsonTasks != null) {
            List<Task> tasks = gson.fromJson(jsonTasks, tasksType);
            if (!tasks.isEmpty()) {
                for (Task task : tasks) {

                    manager.tasks.put(task.getId(), task);
                    manager.prioritizedTasks.add(task);
                }
            }
        }

        String jsonSubtasks = manager.client.load(SUBTASKS_KEY);
        if (jsonSubtasks != null) {
            List<SubTask> subtasks = gson.fromJson(jsonSubtasks, subtasksType);
            if (!subtasks.isEmpty()) {
                for (SubTask subtask : subtasks) {
                    manager.subTasks.put(subtask.getId(), subtask);
                    manager.prioritizedTasks.add(subtask);
                }
            }
        }

        String jsonEpics = manager.client.load(EPICS_KEY);
        if (jsonEpics != null) {
            List<Epic> epics = gson.fromJson(jsonEpics, epicsType);
            if (!epics.isEmpty()) {
                for (Epic epic : epics) {
                    manager.epics.put(epic.getId(), epic);
                }
            }
        }

        String jsonHistory = manager.client.load(HISTORY_KEY);
        if (jsonHistory != null) {
            List<Task> history = gson.fromJson(jsonHistory, tasksType);
            if (!history.isEmpty()) {
                for (Task task : history) {
                    if (manager.tasks.containsKey(task.getId())) {
                        historyManager.add(manager.tasks.get(task.getId()));
                    }
                    if (manager.subTasks.containsKey(task.getId())) {
                        historyManager.add(manager.subTasks.get(task.getId()));
                    }
                    if (manager.epics.containsKey(task.getId())) {
                        historyManager.add(manager.epics.get(task.getId()));
                    }
                }
            }
        }

        manager.setGeneratedId(getMaxId(manager));

        return manager;
    }

    @Override
    public List<Task> getHistory() {
        List<Task> history = super.getHistory();
        save();
        return history;
    }

    @Override
    public Task createTask(Task task) {
        Task newTask = super.createTask(task);
        save();
        return newTask;
    }

    @Override
    public Task getTask(int id) {
        Task task = super.getTask(id);
        save();
        return task;
    }

    @Override
    public Task updateTask(Task updatedTask) {
        Task task = super.updateTask(updatedTask);
        save();
        return task;
    }

    @Override
    public void removeAllTasks() {
        super.removeAllTasks();
        save();
    }

    @Override
    public void removeTask(int id) {
        super.removeTask(id);
        save();
    }

    @Override
    public Epic createEpic(Epic epic) {
        Epic newEpic = super.createEpic(epic);
        save();
        return newEpic;
    }

    @Override
    public Epic updateEpic(Epic updatedEpic) {
        Epic epic = super.updateEpic(updatedEpic);
        save();
        return epic;
    }

    @Override
    public Epic getEpic(int id) {
        Epic epic = super.getEpic(id);
        save();
        return epic;
    }

    @Override
    public void removeEpicSubtasks(int id) {
        super.removeEpicSubtasks(id);
        save();
    }

    @Override
    public void removeEpic(int id) {
        super.removeEpic(id);
        save();
    }

    @Override
    public SubTask createSubTask(SubTask subTask) {
        SubTask newSubtask = super.createSubTask(subTask);
        save();
        return newSubtask;
    }

    @Override
    public void removeSubTask(int id) {
        super.removeSubTask(id);
        save();
    }

    @Override
    public void removeAllSubTasks() {
        super.removeAllSubTasks();
        save();
    }

    @Override
    public SubTask getSubtask(int id) {
        SubTask subtask = super.getSubtask(id);
        save();
        return subtask;
    }

    @Override
    public SubTask updateSubTask(SubTask updatedSubTask) {
        SubTask subtask = super.updateSubTask(updatedSubTask);
        save();
        return subtask;
    }
}
