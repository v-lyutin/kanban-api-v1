package models;

import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {
    private final List<SubTask> subTasks;

    public Epic(String title, String description) {
        super(title, description);
        subTasks = new ArrayList<>();
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
        if (!subTasks.isEmpty())
            result += ", subTasks=" + subTasks + "}";
        else
            result += ", subTasks=null}";
        return result;
    }
}
