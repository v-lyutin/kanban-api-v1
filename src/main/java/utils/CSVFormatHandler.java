package utils;

import models.Epic;
import models.SubTask;
import models.Task;
import services.history_manager.HistoryManager;

import java.util.ArrayList;
import java.util.List;

public class CSVFormatHandler {
    public static String toString(Task task) {
        String result = String.format("%s,%s,%s,%s,%s",
                task.getId(),
                task.getType(),
                task.getTitle(),
                task.getStatus(),
                task.getDescription());

        if (task instanceof SubTask) {
            return String.format("%s,%s", result, ((SubTask) task).getEpicId());
        }
        return result;
    }

    public static Task fromString(String value) {
        String[] taskContent = value.split(",");

        if (taskContent[1].equals(TaskType.SUBTASK.toString())) {
            SubTask task = new SubTask(taskContent[2], taskContent[4], Integer.parseInt(taskContent[5]));
            task.setId(Integer.parseInt(taskContent[0]));
            task.setStatus(TaskStatus.valueOf(taskContent[3]));
            return task;
        }

        if (taskContent[1].equals(TaskType.EPIC.toString())) {
            Epic task = new Epic(taskContent[2], taskContent[4]);
            task.setId(Integer.parseInt(taskContent[0]));
            task.setStatus(TaskStatus.valueOf(taskContent[3]));
            return task;
        }

        Task task = new Task(taskContent[2], taskContent[4]);
        task.setId(Integer.parseInt(taskContent[0]));
        task.setStatus(TaskStatus.valueOf(taskContent[3]));
        return task;

    }

    public static String historyToString(HistoryManager historyManager) {
        List<String> result = new ArrayList<>();

        for (Task task : historyManager.getHistory()) {
            result.add(String.valueOf(task.getId()));
        }

        return String.join(",", result);
    }

    public static List<Integer> historyFromString(String value) {
        String[] values = value.split(",");
        List<Integer> identifiers = new ArrayList<>();
        
        for (String id : values) {
            identifiers.add(Integer.parseInt(id));
        }

        return identifiers;
    }

    public static String getHeader() {
        return "id,type,name,status,description,epic_id";
    }
}
