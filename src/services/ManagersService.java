package services;

import services.history_manager.HistoryManager;
import services.history_manager.InMemoryHistoryManager;
import services.task_manager.FileBackedTasksManager;
import services.task_manager.TaskManager;

public class ManagersService {
    private ManagersService() {}

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }

    public static TaskManager getDefault(HistoryManager historyManager) {
        return new FileBackedTasksManager(historyManager);
    }
}
