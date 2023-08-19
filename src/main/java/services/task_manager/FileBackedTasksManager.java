package services.task_manager;

import models.Epic;
import models.SubTask;
import models.Task;
import services.history_manager.HistoryManager;
import utils.CSVFormatHandler;
import utils.TaskType;
import java.io.*;
import java.util.List;

public class FileBackedTasksManager extends InMemoryTaskManager {
    private final File fileName;
    public FileBackedTasksManager(HistoryManager historyManager) {
        super(historyManager);
        this.fileName = new File("src/main/resources/tasks_history.csv");
    }

    private void save() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            writer.write(CSVFormatHandler.getHeader());
            writer.newLine();

            if (!tasks.isEmpty()) {
                for (Task task : tasks.values()) {
                    writer.write(CSVFormatHandler.toString(task));
                    writer.newLine();
                }
            }

            if (!epics.isEmpty()) {
                for (Task task : epics.values()) {
                    writer.write(CSVFormatHandler.toString(task));
                    writer.newLine();
                }
            }

            if (!subTasks.isEmpty()) {
                for (Task task : subTasks.values()) {
                    writer.write(CSVFormatHandler.toString(task));
                    writer.newLine();
                }
            }

            if (!historyManager.getHistory().isEmpty()) {
                writer.newLine();
                writer.write(CSVFormatHandler.historyToString(historyManager));
            }
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    public static FileBackedTasksManager loadFromFile(File file, HistoryManager historyManager) {
        FileBackedTasksManager manager = new FileBackedTasksManager(historyManager);

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            reader.readLine();

            skip_if_null:
            while (reader.ready() || !reader.readLine().isEmpty()) {
                String lineContent = reader.readLine();

                if (lineContent.isEmpty()) {
                    break;
                }

                Task task = CSVFormatHandler.fromString(lineContent);

                if (task == null) {
                    continue skip_if_null;
                }

                TaskType type = task.getType();
                switch (type) {
                    case TASK:
                        manager.tasks.put(task.getId(), task);
                        manager.generateId();
                        break;
                    case SUBTASK:
                        manager.subTasks.put(task.getId(), (SubTask) task);
                        manager.generateId();
                        break;
                    case EPIC:
                        manager.epics.put(task.getId(), (Epic) task);
                        manager.generateId();
                        break;
                }
            }

            if (!manager.subTasks.isEmpty()) {
                for (SubTask subTask : manager.subTasks.values()) {
                    Epic epic = manager.epics.get(subTask.getEpicId());
                    epic.addSubtask(subTask);
                }
            }

            for (Integer id : CSVFormatHandler.historyFromString(reader.readLine())) {
                if (manager.tasks.containsKey(id)) {
                    manager.historyManager.add(manager.tasks.get(id));
                }

                if (manager.epics.containsKey(id)) {
                    manager.historyManager.add(manager.epics.get(id));
                }

                if (manager.subTasks.containsKey(id)) {
                    manager.historyManager.add(manager.subTasks.get(id));
                }
            }

            return manager;

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Task> getHistory() {
        List<Task> history = super.getHistory();
        save();
        return history;
    }

    @Override
    public Task createTask(Task task) {
        Task createdTask = super.createTask(task);
        save();
        return createdTask;
    }

    @Override
    public Task getTask(int id) {
        Task task = super.getTask(id);
        save();
        return task;
    }

    @Override
    public void updateTask(Task updatedTask) {
        super.updateTask(updatedTask);
        save();
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
        Epic crearedEpic = super.createEpic(epic);
        save();
        return crearedEpic;
    }

    @Override
    public void updateEpic(Epic updatedEpic) {
        super.updateEpic(updatedEpic);
        save();
    }

    @Override
    public Epic getEpic(int id) {
        Epic epic = super.getEpic(id);
        save();
        return epic;
    }

    @Override
    public void removeEpicsSubtasks(Epic epic) {
        super.removeEpicsSubtasks(epic);
        save();
    }

    @Override
    public void removeEpic(int id) {
        super.removeEpic(id);
        save();
    }

    @Override
    public SubTask createSubTask(SubTask subTask) {
        SubTask createdSubTask = super.createSubTask(subTask);
        save();
        return createdSubTask;
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
        SubTask subTask = super.getSubtask(id);
        save();
        return subTask;
    }

    @Override
    public SubTask updateSubTask(SubTask updatedSubTask) {
        SubTask subTask = super.updateSubTask(updatedSubTask);
        save();
        return subTask;
    }
}
