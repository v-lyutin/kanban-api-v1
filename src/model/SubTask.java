package model;

public class SubTask extends Task {
    private final int epicId;

    public SubTask(String title, String description, int epicId) {
        super(title, description);
        this.epicId = epicId;
    }

    public int getEpicId() {
        return epicId;
    }
}
