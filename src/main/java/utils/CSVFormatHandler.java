package utils;

import models.Epic;
import models.SubTask;
import models.Task;
import services.history_manager.HistoryManager;
import java.util.ArrayList;
import java.util.List;

public class CSVFormatHandler {
    private static final byte MIN_STRING_ARRAY_SIZE = 5;
    private static final byte MAX_STRING_ARRAY_SIZE = 6;
    private static final byte ID = 0;
    private static final byte TYPE = 1;
    private static final byte NAME = 2;
    private static final byte STATUS = 3;
    private static final byte DESCRIPTION = 4;
    private static final byte EPIC_ID = 5;

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

        if (!(validateSize(taskContent.length) && validateType(taskContent[TYPE]) && validateId(taskContent[ID]))) {
            return null;
        }

        TaskType type = TaskType.valueOf(taskContent[TYPE]);
        switch (type) {
            case SUBTASK: {
                SubTask subTask = new SubTask(
                        taskContent[NAME],
                        taskContent[DESCRIPTION],
                        Integer.parseInt(taskContent[EPIC_ID]));
                subTask.setId(Integer.parseInt(taskContent[ID]));
                subTask.setStatus(TaskStatus.valueOf(taskContent[STATUS]));
                return subTask;
            }
            case EPIC: {
                Epic epic = new Epic(taskContent[NAME], taskContent[DESCRIPTION]);
                epic.setId(Integer.parseInt(taskContent[ID]));
                epic.setStatus(TaskStatus.valueOf(taskContent[STATUS]));
                return epic;
            }
            case TASK: {
                Task task = new Task(taskContent[NAME], taskContent[DESCRIPTION]);
                task.setId(Integer.parseInt(taskContent[ID]));
                task.setStatus(TaskStatus.valueOf(taskContent[STATUS]));
                return task;
            }
        }
        return null;
    }

    private static boolean validateSize(int length) {
        return length == MIN_STRING_ARRAY_SIZE || length == MAX_STRING_ARRAY_SIZE;
    }

    private static boolean validateType(String type) {
        return type.equals("TASK") || type.equals("SUBTASK") || type.equals("EPIC");
    }

    private static boolean validateId(String id) {
        try {
            Integer.parseInt(id);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
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
