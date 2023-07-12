import services.history_manager.HistoryManager;
import services.task_manager.TaskManager;
import models.Epic;
import models.SubTask;
import models.Task;
import services.MangersService;
import utils.TaskStatus;


public class Main {
    public static void main(String[] args) {
        HistoryManager historyManager = MangersService.getDefaultHistory();
        TaskManager taskManager = MangersService.getDefault(historyManager);

        Task task1 = new Task("...", "...");
        taskManager.createTask(task1);
        taskManager.getTask(task1.getId());
        System.out.println(historyManager.getHistory());

        Epic epic1 = new Epic("...", "...");
        taskManager.createEpic(epic1);
        taskManager.getEpic(epic1.getId());
        System.out.println(historyManager.getHistory());

        SubTask subTask1 = new SubTask("...", "...", epic1.getId());
        subTask1.setStatus(TaskStatus.DONE);
        taskManager.createSubTask(subTask1);
        taskManager.updateSubTask(subTask1);
        taskManager.getSubtask(subTask1.getId());
        System.out.println(historyManager.getHistory());
    }
}
