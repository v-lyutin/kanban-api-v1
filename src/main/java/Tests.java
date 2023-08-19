import models.Task;
import services.history_manager.HistoryManager;
import services.task_manager.FileBackedTasksManager;
import models.Epic;
import models.SubTask;
import services.ManagersService;
import utils.TaskStatus;
import utils.TaskType;

import java.io.File;


public class Tests {
    public static void main(String[] args) {
        HistoryManager historyManager = ManagersService.getDefaultHistory();
        FileBackedTasksManager fileManager = FileBackedTasksManager.loadFromFile(new File("src/main/resources/tasks_history.csv"));
        System.out.println(fileManager.getAllEpics());
        System.out.println(fileManager.getHistory());

//        FileBackedTasksManager fileManager = new FileBackedTasksManager(historyManager);
//
//        Task task1 = new Task(":)", "...");
//        fileManager.createTask(task1);
//        Epic epic1 = new Epic(":0", "...");
//        fileManager.createEpic(epic1);
//        SubTask subTask1 = new SubTask("...", "...", epic1.getId());
//        fileManager.createSubTask(subTask1);
//        SubTask subTask2 = new SubTask("...", "...", epic1.getId());
//        fileManager.createSubTask(subTask2);
//        subTask2.setStatus(TaskStatus.DONE);
//        fileManager.updateSubTask(subTask2);
//
//        fileManager.getEpic(epic1.getId());
//        fileManager.getTask(task1.getId());

    }
}
