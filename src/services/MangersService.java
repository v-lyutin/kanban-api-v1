package services;

import services.history_manager.HistoryManager;
import services.history_manager.InMemoryHistoryManager;
import services.task_manager.InMemoryTaskManager;
import services.task_manager.TaskManager;

public class MangersService {
    private MangersService() {}
    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }

    public static TaskManager getDefault(HistoryManager historyManager) {
        return new InMemoryTaskManager(historyManager);
    }
}
