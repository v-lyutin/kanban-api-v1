package model;

import java.util.ArrayList;

public class Epic extends Task {
    private final ArrayList<SubTask> subTasks;

    public Epic(String title, String description) {
        super(title, description);
        subTasks = new ArrayList<>();
    }

    public ArrayList<SubTask> getSubTasks() {
        return subTasks;
    }

    public void addSubtask(SubTask subTask) {
        subTasks.add(subTask);
    }

    public void deleteSubtask(SubTask subTask) {
        subTasks.remove(subTask);
    }

    @Override
    public String toString() {
        String result = "Epic{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status;
        if (!subTasks.isEmpty())
            result += ", subTasks=" + subTasks + "}";
        else
            result += ", subTasks=null}";
        return result;
    }
}
