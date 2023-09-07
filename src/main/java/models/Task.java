package models;

import utils.DateTimeFormatHandler;
import utils.TaskStatus;
import utils.TaskType;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

public class Task {
    protected int id;
    protected String title;
    protected String description;
    protected TaskStatus taskStatus;
    protected TaskType type;
    protected LocalDateTime startTime;
    protected Duration duration;

    public Task(String title, String description) {
        this.title = title;
        this.description = description;
        this.taskStatus = TaskStatus.NEW;
        this.type = TaskType.TASK;
    }

    public Task(String title, String description, String startTime, long duration) {
        this.title = title;
        this.description = description;
        this.startTime = DateTimeFormatHandler.parseDateFromString(startTime);
        this.duration = Duration.ofMinutes(duration);
        this.taskStatus = TaskStatus.NEW;
        this.type = TaskType.TASK;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public Duration getDuration() {
        return duration;
    }

    public LocalDateTime getEndTime() {
        if ((this.startTime != null) && (this.duration != null)) {
            return startTime.plus(duration);
        }

        return null;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public TaskStatus getStatus() {
        return taskStatus;
    }

    public void setStatus(TaskStatus taskStatus) {
        this.taskStatus = taskStatus;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public TaskType getType() {
        return type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return id == task.id && Objects.equals(title, task.title) &&
                Objects.equals(description, task.description) &&
                taskStatus == task.taskStatus && type == task.type &&
                Objects.equals(startTime, task.startTime) &&
                Objects.equals(duration, task.duration);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, description, taskStatus, type, startTime, duration);
    }

    @Override
    public String toString() {
        String result = "Task{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", taskStatus=" + taskStatus;

        if (startTime != null && getEndTime() != null && duration != null) {
            result += ", startTime=" + startTime.format(DateTimeFormatHandler.DEFAULT_DATE_TIME_FORMAT) +
                    ", endTime=" + getEndTime().format(DateTimeFormatHandler.DEFAULT_DATE_TIME_FORMAT) +
                    ", duration=" + duration +
                    "}";
        } else {
            result += "}";
        }

        return result;
    }
}
