package models;

import utils.DateTimeFormatHandler;
import utils.TaskType;

public class SubTask extends Task {
    private final int epicId;

    public SubTask(String title, String description, int epicId) {
        super(title, description);
        this.epicId = epicId;
        this.type = TaskType.SUBTASK;
    }

    public SubTask(String title, String description, String startTime, long duration, int epicId) {
        super(title, description, startTime, duration);
        this.epicId = epicId;
        this.type = TaskType.SUBTASK;
    }

    public int getEpicId() {
        return epicId;
    }

    @Override
    public TaskType getType() {
        return TaskType.SUBTASK;
    }

    @Override
    public String toString() {
        String result = "SubTask{" +
                "id=" + id +
                ", title='" + title +
                ", description='" + description +
                ", taskStatus=" + taskStatus;

        if (startTime != null && endTime != null && duration != null) {
            result += ", startTime=" + startTime.format(DateTimeFormatHandler.DEFAULT_DATE_TIME_FORMAT) +
                    ", endTime=" + endTime.format(DateTimeFormatHandler.DEFAULT_DATE_TIME_FORMAT) +
                    ", duration=" + duration +
                    ", epicId=" + epicId +
                    "}";
        } else {
            result += ", epicId=" + epicId + "}";
        }

        return result;
    }
}
