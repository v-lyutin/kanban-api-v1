package utils;

import models.Epic;
import models.SubTask;
import models.Task;
import service.history_manager.HistoryManager;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class CSVFormatHandler {
    private static final byte MIN_STRING_ARRAY_SIZE = 5;
    private static final byte MAX_STRING_ARRAY_SIZE = 9;
    private static final byte ID = 0;
    private static final byte TYPE = 1;
    private static final byte NAME = 2;
    private static final byte STATUS = 3;
    private static final byte DESCRIPTION = 4;
    private static final byte EPIC_ID = 5;
    private static final byte START_TIME = 6;
    private static final byte END_TIME = 7;
    private static final byte DURATION = 8;

    public static String toString(Task task) {
        StringBuilder result = new StringBuilder(String.format("%s,%s,%s,%s,%s",
                task.getId(),
                task.getType(),
                task.getTitle(),
                task.getStatus(),
                task.getDescription())
        );

        if (task instanceof SubTask) {
            result.append(",").append(((SubTask) task).getEpicId());
        }

        if (task.getStartTime() != null && task.getEndTime() != null && task.getDuration() != null) {
            result.append(",")
                    .append(task.getStartTime().format(DateTimeFormatHandler.DEFAULT_DATE_TIME_FORMAT))
                    .append(",")
                    .append(task.getEndTime().format(DateTimeFormatHandler.DEFAULT_DATE_TIME_FORMAT))
                    .append(",")
                    .append(task.getDuration());
        }

        return result.toString();
    }

    public static Task fromString(String value) {
        String[] taskContent = value.split(",");

        if (!(validateSize(taskContent.length) && validateType(taskContent[TYPE]) && validateId(taskContent[ID]))) {
            return null;
        }

        TaskType type = TaskType.valueOf(taskContent[TYPE]);
        switch (type) {
            case SUBTASK: {
                SubTask subTask;
                if (taskContent.length == MAX_STRING_ARRAY_SIZE) {
                    subTask = new SubTask(
                            taskContent[NAME],
                            taskContent[DESCRIPTION],
                            taskContent[START_TIME],
                            Duration.parse(taskContent[DURATION]).toMinutes(),
                            Integer.parseInt(taskContent[EPIC_ID])
                    );
                } else {
                    subTask = new SubTask(
                            taskContent[NAME],
                            taskContent[DESCRIPTION],
                            Integer.parseInt(taskContent[EPIC_ID]));
                }
                subTask.setId(Integer.parseInt(taskContent[ID]));
                subTask.setStatus(TaskStatus.valueOf(taskContent[STATUS]));
                return subTask;
            }
            case EPIC: {
                if (taskContent.length == MAX_STRING_ARRAY_SIZE - 1) {
                    Epic epic = new Epic(taskContent[NAME], taskContent[DESCRIPTION]);
                    epic.setId(Integer.parseInt(taskContent[ID]));
                    epic.setStatus(TaskStatus.valueOf(taskContent[STATUS]));
                    epic.setStartTime(DateTimeFormatHandler.parseDateFromString(taskContent[START_TIME - 1]));
                    epic.setEndTime(DateTimeFormatHandler.parseDateFromString(taskContent[END_TIME - 1]));
                    epic.setDuration(Duration.parse(taskContent[DURATION - 1]));
                    return epic;
                } else {
                    Epic epic = new Epic(taskContent[NAME], taskContent[DESCRIPTION]);
                    epic.setId(Integer.parseInt(taskContent[ID]));
                    epic.setStatus(TaskStatus.valueOf(taskContent[STATUS]));
                    return epic;
                }
            }
            case TASK: {
                Task task;
                if (taskContent.length == MAX_STRING_ARRAY_SIZE - 1) {
                    task = new Task(
                            taskContent[NAME],
                            taskContent[DESCRIPTION],
                            taskContent[START_TIME - 1],
                            Duration.parse(taskContent[DURATION - 1]).toMinutes()
                    );
                } else {
                    task = new Task(taskContent[NAME], taskContent[DESCRIPTION]);
                }
                task.setId(Integer.parseInt(taskContent[ID]));
                task.setStatus(TaskStatus.valueOf(taskContent[STATUS]));
                return task;
            }
        }
        return null;
    }

    private static boolean validateSize(int length) {
        return (length >= MIN_STRING_ARRAY_SIZE && length <= MAX_STRING_ARRAY_SIZE);
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
        return "id,type,name,status,description,epic_id,startTime,endTime,duration";
    }
}
