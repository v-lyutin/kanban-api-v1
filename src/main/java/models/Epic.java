package models;

import utils.DateTimeFormatHandler;
import utils.TaskType;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {
    private final List<SubTask> subTasks;
    private LocalDateTime endTime;

    public Epic(String title, String description) {
        super(title, description);
        subTasks = new ArrayList<>();
        this.type = TaskType.EPIC;
        this.endTime = null;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    @Override
    public LocalDateTime getEndTime() {
        return endTime;
    }

    public List<SubTask> getSubTasks() {
        return new ArrayList<>(subTasks);
    }

    public void addSubtask(SubTask subTask) {
        subTasks.add(subTask);
    }

    public void removeSubtask(SubTask subTask) {
        subTasks.remove(subTask);
    }

    public void removeEpicsSubTasks() {
        subTasks.clear();
    }

    @Override
    public String toString() {
        String result = "Epic{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", status=" + taskStatus;

        if (startTime != null && endTime != null && duration != null) {
            result += ", startTime=" + startTime.format(DateTimeFormatHandler.DEFAULT_DATE_TIME_FORMAT) +
                    ", endTime=" + endTime.format(DateTimeFormatHandler.DEFAULT_DATE_TIME_FORMAT) +
                    ", duration=" + duration;
        }

        if (!subTasks.isEmpty()) {
            result += ", subTasks=" + subTasks + "}";
        } else {
            result += ", subTasks=null}";
        }

        return result;
    }
}
