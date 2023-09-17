package server;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import models.Epic;
import models.SubTask;
import models.Task;
import service.task_manager.TaskManager;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.regex.Pattern;

public class TaskHandler implements HttpHandler {
    private final TaskManager manager;
    private final Gson gson = new Gson();
    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;


    public TaskHandler(TaskManager manager) {
        this.manager = manager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().toString();
        String method = exchange.getRequestMethod();
        Endpoint endpoint = Endpoint.getEndpoint(path, method);

        switch (endpoint) {
            case GET_PRIORITIZED_TASKS: {
                handlePrioritizedTasks(exchange);
                break;
            }
            case GET_HISTORY: {
                handleGetHistory(exchange);
                break;
            }
            case GET_TASKS: {
                handleGetTasks(exchange);
                break;
            }
            case GET_SUBTASKS: {
                handleGetSubtasks(exchange);
                break;
            }
            case GET_EPICS: {
                handleGetEpics(exchange);
                break;
            }
            case GET_TASK_BY_ID: {
                handleGetTaskById(exchange, path);
                break;
            }
            case GET_SUBTASK_BY_ID: {
                handleGetSubtaskById(exchange, path);
                break;
            }
            case GET_EPIC_BY_ID: {
                handleGetEpicById(exchange, path);
                break;
            }
            case GET_EPIC_SUBTASKS: {
                handleGetEpicSubtasks(exchange, path);
                break;
            }
            case ADD_TASK: {
                handleAddTask(exchange);
                break;
            }
            case ADD_EPIC: {
                handleAddEpic(exchange);
                break;
            }
            case ADD_SUBTASK: {
                handleAddSubtask(exchange);
                break;
            }
            case UPDATE_TASK: {
                handleUpdateTask(exchange, path);
                break;
            }
            case UPDATE_EPIC: {
                handleUpdateEpic(exchange, path);
                break;
            }
            case UPDATE_SUBTASK: {
                handleUpdateSubtask(exchange, path);
                break;
            }
            case DELETE_ALL_TASKS: {
                handleDeleteAllTasks(exchange);
                break;
            }
            case DELETE_ALL_SUBTASKS: {
                handleDeleteAllSubtasks(exchange);
                break;
            }
            case DELETE_TASK_BY_ID: {
                handleDeleteTaskById(exchange, path);
                break;
            }
            case DELETE_SUBTASK_BY_ID: {
                handleDeleteSubtaskById(exchange, path);
                break;
            }
            case DELETE_EPIC_BY_ID: {
                handleDeleteEpicById(exchange, path);
                break;
            }
            case DELETE_EPIC_SUBTASKS: {
                handleDeleteEpicSubtasks(exchange, path);
                break;
            }
            default: {
                String response = "Такого эндпоинта не существует";
                writeResponse(exchange, response, 405);
            }
        }
    }

    private void handlePrioritizedTasks(HttpExchange exchange) throws IOException {
        String response = gson.toJson(manager.getPrioritizedTasks());
        writeResponse(exchange, response, 200);
    }

    private void handleGetHistory(HttpExchange exchange) throws IOException {
        String response = gson.toJson(manager.getHistory());
        writeResponse(exchange, response, 200);
    }

    private void handleGetTasks(HttpExchange exchange) throws IOException {
        writeResponse(exchange, gson.toJson(manager.getAllTasks()), 200);
    }

    private void handleGetSubtasks(HttpExchange exchange) throws IOException {
        writeResponse(exchange, gson.toJson(manager.getAllSubtasks()), 200);
    }

    private void handleGetEpics(HttpExchange exchange) throws IOException {
        writeResponse(exchange, gson.toJson(manager.getAllEpics()), 200);
    }

    private void handleGetTaskById(HttpExchange exchange, String path) throws IOException {
        int id = parseId(exchange, path.replaceFirst("/tasks/task/\\?id=", ""));
        Task task = manager.getTask(id);
        if (task == null) {
            String response = String.format("Задача с идентификатором %d не найдена", id);
            writeResponse(exchange, response, 404);
            return;
        }
        writeResponse(exchange, gson.toJson(task), 200);
    }

    private void handleGetSubtaskById(HttpExchange exchange, String path) throws IOException {
        int id = parseId(exchange, path.replaceFirst("/tasks/subtask/\\?id=", ""));
        SubTask subtask = manager.getSubtask(id);
        if (subtask == null) {
            String response = String.format("Подзадача с идентификатором %d не найдена", id);
            writeResponse(exchange, response, 404);
            return;
        }
        writeResponse(exchange, gson.toJson(subtask), 200);
    }

    private void handleGetEpicById(HttpExchange exchange, String path) throws IOException {
        int id = parseId(exchange, path.replaceFirst("/tasks/epic/\\?id=", ""));
        Epic epic = manager.getEpic(id);
        if (epic == null) {
            String response = String.format("Эпик с идентификатором %d не найден", id);
            writeResponse(exchange, response, 404);
            return;
        }
        writeResponse(exchange, gson.toJson(epic), 200);
    }

    private void handleGetEpicSubtasks(HttpExchange exchange, String path) throws IOException {
        int id = parseId(exchange, path.replaceFirst("/tasks/subtask/epic/\\?id=", ""));
        boolean isEpicExist = manager.getAllEpics().stream().anyMatch(epic -> epic.getId() == id);

        if (isEpicExist) {
            List<SubTask> subtasks = manager.getEpicsSubTasks(id);
            writeResponse(exchange, gson.toJson(subtasks), 200);
            return;
        }
        String response = String.format("Эпик с идентификатором %d не удалось найти", id);
        writeResponse(exchange, response, 404);
    }

    private void handleAddTask(HttpExchange exchange) throws IOException {
        String body = getResponseBody(exchange);

        try {
            Task jsonTask = gson.fromJson(body, Task.class);

            if (jsonTask.getTitle().isBlank() || jsonTask.getDescription().isBlank()) {
                writeResponse(exchange, "Поля не могут быть пустыми", 400);
                return;
            }

            Task task = new Task(jsonTask.getTitle(), jsonTask.getDescription());
            task.setStartTime(jsonTask.getStartTime());
            task.setDuration(jsonTask.getDuration());
            manager.createTask(task);
            writeResponse(exchange, "Задача добавлена", 201);

        } catch (JsonSyntaxException e) {
            writeResponse(exchange, "Получен некорректный JSON", 400);
        }
    }

    private void handleAddEpic(HttpExchange exchange) throws IOException {
        String body = getResponseBody(exchange);

        try {
            Epic jsonEpic = gson.fromJson(body, Epic.class);

            if (jsonEpic.getTitle().isBlank() || jsonEpic.getDescription().isBlank()) {
                writeResponse(exchange, "Поля не могут быть пустыми", 400);
                return;
            }

            Epic epic = new Epic(jsonEpic.getTitle(), jsonEpic.getDescription());
            manager.createEpic(epic);
            writeResponse(exchange, "Эпик добавлен", 201);

        } catch (JsonSyntaxException e) {
            writeResponse(exchange, "Получен некорректный JSON", 400);
        }
    }

    private void handleAddSubtask(HttpExchange exchange) throws IOException {
        String body = getResponseBody(exchange);

        try {
            SubTask jsonSubtask = gson.fromJson(body, SubTask.class);

            if (jsonSubtask.getTitle().isBlank() || jsonSubtask.getDescription().isBlank()) {
                writeResponse(exchange, "Поля не могут быть пустыми", 400);
                return;
            }

            SubTask subTask = new SubTask(jsonSubtask.getTitle(), jsonSubtask.getDescription(), jsonSubtask.getEpicId());
            subTask.setStartTime(jsonSubtask.getStartTime());
            subTask.setDuration(jsonSubtask.getDuration());

            if (manager.createSubTask(subTask) != null) {
                writeResponse(exchange, "Подзадача добавлена", 201);
            } else {
                String response = String.format("Эпика с идентификатором %d не существует", subTask.getEpicId());
                writeResponse(exchange, response, 400);
            }

        } catch (JsonSyntaxException e) {
            writeResponse(exchange, "Получен некорректный JSON", 400);
        }
    }

    private void handleUpdateTask(HttpExchange exchange, String path) throws IOException {
        int id = parseId(exchange, path.replaceFirst("/tasks/task/\\?id=", ""));

        String body = getResponseBody(exchange);

        Task jsonTask = gson.fromJson(body, Task.class);

        if (jsonTask.getTitle().isBlank() || jsonTask.getDescription().isBlank()) {
            writeResponse(exchange, "Поля не могут быть пустыми", 400);
            return;
        }

        try {
            Task task = new Task(jsonTask.getTitle(), jsonTask.getDescription());
            task.setStartTime(jsonTask.getStartTime());
            task.setDuration(jsonTask.getDuration());
            task.setId(id);

            if (manager.updateTask(task) != null) {
                writeResponse(exchange, "Задача обновлена", 200);
            } else {
                String response = String.format("Задачу с идентификатором %d не удалось найти", id);
                writeResponse(exchange, response, 400);
            }
        } catch (JsonSyntaxException e) {
            writeResponse(exchange, "Получен некорректный JSON", 400);
        }
    }

    private void handleUpdateEpic(HttpExchange exchange, String path) throws IOException {
        int id = parseId(exchange, path.replaceFirst("/tasks/epic/\\?id=", ""));

        String body = getResponseBody(exchange);

        Epic jsonEpic = gson.fromJson(body, Epic.class);

        if (jsonEpic.getTitle().isBlank() || jsonEpic.getDescription().isBlank()) {
            writeResponse(exchange, "Поля не могут быть пустыми", 400);
            return;
        }

        try {
            Epic epic = new Epic(jsonEpic.getTitle(), jsonEpic.getDescription());
            epic.setId(id);

            if (manager.updateEpic(epic) != null) {
                writeResponse(exchange, "Эпик обновлён", 200);
            } else {
                String response = String.format("Эпик с идентификатором %d не удалось найти", id);
                writeResponse(exchange, response, 400);
            }
        } catch (JsonSyntaxException e) {
            writeResponse(exchange, "Получен некорректный JSON", 400);
        }
    }

    private void handleUpdateSubtask(HttpExchange exchange, String path) throws IOException {
        int id = parseId(exchange, path.replaceFirst("/tasks/subtask/\\?id=", ""));

        String body = getResponseBody(exchange);

        SubTask jsonSubtask = gson.fromJson(body, SubTask.class);

        if (jsonSubtask.getTitle().isBlank() || jsonSubtask.getDescription().isBlank()) {
            writeResponse(exchange, "Поля не могут быть пустыми", 400);
            return;
        }

        try {
            SubTask subtask = new SubTask(jsonSubtask.getTitle(), jsonSubtask.getDescription(), jsonSubtask.getEpicId());
            subtask.setStartTime(jsonSubtask.getStartTime());
            subtask.setDuration(jsonSubtask.getDuration());
            subtask.setId(id);

            if (manager.updateSubTask(subtask) != null) {
                writeResponse(exchange, "Подазадача обновлена", 200);
            } else {
                String response = String.format("Подзадачу с идентификатором %d не удалось найти", id);
                writeResponse(exchange, response, 400);
            }
        } catch (JsonSyntaxException e) {
            writeResponse(exchange, "Получен некорректный JSON", 400);
        }
    }

    private void handleDeleteAllTasks(HttpExchange exchange) throws IOException {
        manager.removeAllTasks();
        writeResponse(exchange, "Задачи удалены", 200);
    }

    private void handleDeleteAllSubtasks(HttpExchange exchange) throws IOException {
        manager.removeAllSubTasks();
        writeResponse(exchange, "Подзадачи удалены", 200);
    }

    private void handleDeleteTaskById(HttpExchange exchange, String path) throws IOException {
        int id = parseId(exchange, path.replaceFirst("/tasks/task/\\?id=", ""));
        boolean isTaskExist = manager.getAllTasks().stream().anyMatch(task -> task.getId() == id);

        if (isTaskExist) {
            manager.removeTask(id);
            String response = String.format("Задача с идентификатором %d удалена", id);
            writeResponse(exchange, response, 200);
            return;
        }
        String response = String.format("Задачу с идентификатором %d не удалось найти", id);
        writeResponse(exchange, response, 404);
    }

    private void handleDeleteSubtaskById(HttpExchange exchange, String path) throws IOException {
        int id = parseId(exchange, path.replaceFirst("/tasks/subtask/\\?id=", ""));
        boolean isSubtaskExist = manager.getAllSubtasks().stream().anyMatch(subtask -> subtask.getId() == id);

        if (isSubtaskExist) {
            manager.removeSubTask(id);
            String response = String.format("Подзадача с идентификатором %d удалена", id);
            writeResponse(exchange, response, 200);
            return;
        }
        String response = String.format("Подзадачу с идентификатором %d не удалось найти", id);
        writeResponse(exchange, response, 404);
    }

    private void handleDeleteEpicById(HttpExchange exchange, String path) throws IOException {
        int id = parseId(exchange, path.replaceFirst("/tasks/epic/\\?id=", ""));
        boolean isEpicExist = manager.getAllEpics().stream().anyMatch(epic -> epic.getId() == id);

        if (isEpicExist) {
            manager.removeEpic(id);
            String response = String.format("Эпик с идентификатором %d удален", id);
            writeResponse(exchange, response, 200);
            return;
        }
        String response = String.format("Эпик с идентификатором %d не удалось найти", id);
        writeResponse(exchange, response, 404);
    }

    private void handleDeleteEpicSubtasks(HttpExchange exchange, String path) throws IOException {
        int id = parseId(exchange, path.replaceFirst("/tasks/subtask/epic/\\?id=", ""));
        boolean isEpicExist = manager.getAllEpics().stream().anyMatch(epic -> epic.getId() == id);

        if (isEpicExist) {
            manager.removeEpicSubtasks(id);
            String response = String.format("Подзадачи эпика с идентификатором %d удалены", id);
            writeResponse(exchange, response, 200);
            return;
        }
        String response = String.format("Эпик с идентификатором %d не удалось найти", id);
        writeResponse(exchange, response, 404);
    }

    private Integer parseId(HttpExchange exchange, String pathId) throws IOException {
        try {
            Integer.parseInt(pathId);
        } catch (NumberFormatException e) {
            writeResponse(exchange, "Получен некорректный идентификатор", 400);
        }
        return Integer.parseInt(pathId);
    }

    private String getResponseBody(HttpExchange exchange) throws IOException {
        InputStream inputStream = exchange.getRequestBody();
        return new String(inputStream.readAllBytes(), DEFAULT_CHARSET);
    }

    private void writeResponse(HttpExchange exchange, String responseString, int responseCode) throws IOException {
        if (responseString.isBlank()) {
            exchange.sendResponseHeaders(responseCode, 0);
        } else {
            byte[] bytes = responseString.getBytes(DEFAULT_CHARSET);
            exchange.sendResponseHeaders(responseCode, bytes.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(bytes);
            }
        }
        exchange.close();
    }
}
