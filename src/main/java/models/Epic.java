package models;

import utils.DateTimeFormatHandler;
import utils.TaskType;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class Epic extends Task {
    private final List<SubTask> subTasks;

    public Epic(String title, String description) {
        super(title, description);
        subTasks = new ArrayList<>();
        this.type = TaskType.EPIC;
    }

    public List<SubTask> getSubTasks() {
        return new ArrayList<>(subTasks);
    }

    public void addSubtask(SubTask subTask) {
        subTasks.add(subTask);
        updateEpic();
    }

    public void removeSubtask(SubTask subTask) {
        subTasks.remove(subTask);
        updateEpic();
    }

    public void removeEpicsSubTasks() {
        subTasks.clear();
        updateEpic();
    }

    @Override
    public TaskType getType() {
        return TaskType.EPIC;
    }

    @Override
    public LocalDateTime getEndTime() {
        if (subTasks.isEmpty()) {
            return null;
        }

        LocalDateTime endTime = LocalDateTime.MIN;

        List<SubTask> subTasks = getSubTasks().stream()
                .filter(subTask -> subTask.getStartTime() != null && subTask.getDuration() != null)
                .collect(Collectors.toList());

        if (subTasks.isEmpty()) {
            return null;
        }

        for (SubTask subTask : subTasks) {
            if (subTask.getEndTime().isAfter(endTime)) {
                endTime = subTask.getEndTime();
            }
        }

        return endTime;
    }

    @Override
    public LocalDateTime getStartTime() {
        if (subTasks.isEmpty()) {
            return null;
        }

        LocalDateTime startTime = LocalDateTime.MAX;

        List<SubTask> subTasks = getSubTasks().stream()
                .filter(subTask -> subTask.getStartTime() != null && subTask.getDuration() != null)
                .collect(Collectors.toList());

        if (subTasks.isEmpty()) {
            return null;
        }

        for (SubTask subTask : subTasks) {
            if (subTask.getStartTime().isBefore(startTime)) {
                startTime = subTask.getStartTime();
            }
        }

        return startTime;
    }

    @Override
    public Duration getDuration() {
        if (subTasks.isEmpty()) {
            return null;
        }

        Optional<Duration> totalEpicDuration = getSubTasks().stream()
                .filter(subTask -> subTask.getStartTime() != null && subTask.getDuration() != null)
                .map(subTask -> Duration.between(subTask.getStartTime(), subTask.getEndTime()))
                .reduce(Duration::plus);

        return totalEpicDuration.orElse(null);
    }

    private void updateEpic() {
        setStartTime(getStartTime());
        setEndTime(getEndTime());
        setDuration(getDuration());
    }

    @Override
    public String toString() {
        String result = "Epic{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", status=" + taskStatus;

        if (getStartTime() != null && getEndTime() != null && getDuration() != null) {
            result += ", startTime=" + getStartTime().format(DateTimeFormatHandler.DEFAULT_DATE_TIME_FORMAT) +
                    ", endTime=" + getEndTime().format(DateTimeFormatHandler.DEFAULT_DATE_TIME_FORMAT) +
                    ", duration=" + getDuration();
        }

        if (!subTasks.isEmpty()) {
            result += ", subTasks=" + subTasks + "}";
        } else {
            result += ", subTasks=null}";
        }

        return result;
    }
}
