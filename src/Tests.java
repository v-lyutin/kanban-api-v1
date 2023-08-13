import services.history_manager.HistoryManager;
import services.task_manager.TaskManager;
import models.Epic;
import models.SubTask;
import services.MangersService;


public class Tests {
    public static void main(String[] args) {
        HistoryManager historyManager = MangersService.getDefaultHistory();
        TaskManager taskManager = MangersService.getDefault(historyManager);


        Epic epic1 = new Epic("...", "...");
        taskManager.createEpic(epic1);

        SubTask subTask1 = new SubTask("...", "...", epic1.getId());
        taskManager.createSubTask(subTask1);

        SubTask subTask2 = new SubTask("...", "...", epic1.getId());
        taskManager.createSubTask(subTask2);

        SubTask subTask3 = new SubTask("...", "...", epic1.getId());
        taskManager.createSubTask(subTask3);

        Epic epic2 = new Epic("...", "...");
        taskManager.createEpic(epic2);


        taskManager.getEpic(epic2.getId());
        System.out.println(taskManager.getHistory());
        taskManager.getSubtask(subTask3.getId());
        System.out.println(historyManager.getHistory());
        taskManager.getEpic(epic1.getId());
        System.out.println(historyManager.getHistory());
        taskManager.getSubtask(subTask3.getId());
        System.out.println(historyManager.getHistory());
        taskManager.removeEpic(epic2.getId());
        System.out.println(historyManager.getHistory());
        taskManager.removeEpic(epic1.getId());
        System.out.println(historyManager.getHistory());
    }
}
