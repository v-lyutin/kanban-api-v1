package service;

import managers.history_manager.HistoryManager;
import managers.history_manager.InMemoryHistoryManager;
import managers.task_manager.InMemoryTaskManager;
import managers.task_manager.TaskManager;

public class TaskService {
    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }

    public static TaskManager getDefault(HistoryManager historyManager) {
        return new InMemoryTaskManager(historyManager);
    }
}
