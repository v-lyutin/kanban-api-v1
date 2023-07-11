package managers.history_manager;

import models.Task;

import java.util.ArrayList;

public class InMemoryHistoryManager implements HistoryManager {
    private final ArrayList<Task> history;
    private static final int HISTORY_SIZE = 10;

    public InMemoryHistoryManager() {
        history = new ArrayList<>();
    }

    private boolean isDuplicate(int id) {
        boolean isDuplicate = false;

        for (Task task : history) {
            if (task.getId() == id) {
                isDuplicate = true;
                return isDuplicate;
            }
        }
        return isDuplicate;
    }

    @Override
    public void add(Task task) {
        if (isDuplicate(task.getId())) {
            return;
        }

        if (history.size() >= HISTORY_SIZE) {
            history.remove(history.get(0));
            history.add(task);
        } else {
            history.add(task);
        }
    }

    @Override
    public ArrayList<Task> getHistory() {
        return new ArrayList<>(history);
    }
}
