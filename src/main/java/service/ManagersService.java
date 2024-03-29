package service;

import service.history_manager.HistoryManager;
import service.history_manager.InMemoryHistoryManager;
import service.task_manager.FileBackedTasksManager;
import service.task_manager.HttpTaskManager;
import service.task_manager.TaskManager;

public class ManagersService {
    private ManagersService() {}

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }

    public static FileBackedTasksManager getDefault(HistoryManager historyManager) {
        return new FileBackedTasksManager(historyManager);
    }

    public static HttpTaskManager getHttpManager(HistoryManager historyManager) {
        return new HttpTaskManager(historyManager, 8078);
    }
}
