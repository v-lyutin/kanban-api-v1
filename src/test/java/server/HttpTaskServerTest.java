package server;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import models.Epic;
import models.SubTask;
import models.Task;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class HttpTaskServerTest {
    private HttpTaskServer httpTaskServer;
    private KVServer kvServer;
    private final Gson gson = new Gson();
    private Task task1;
    private Task task2;
    private Task task3;
    private Epic epic1;
    private Epic epic2;

    @BeforeEach
    void init() throws IOException {
        kvServer = new KVServer();
        kvServer.start();
        httpTaskServer = new HttpTaskServer();
        httpTaskServer.start();

        task1 = new Task("Task 1", "Task 1 description");
        task2 = new Task("Task 2", "Task 2 description", "2099.01.01, 10:00", 120);
        task3 = new Task("", "");
        epic1 = new Epic("Epic 1", "Epic 1 description");
        epic2 = new Epic("Epic 2", "Epic 2 description");

        task1.setId(1);
        task2.setId(2);
    }

    @AfterEach
    void tearDown() {
        httpTaskServer.stop(0);
        kvServer.stop(0);
    }

    @Test
    void createTask_status201() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/tasks/task/");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(task1)))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());
    }

    @Test
    void createTask_status400() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/tasks/task/");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(task3)))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(400, response.statusCode());
    }

    @Test
    void getTasks() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/tasks/task/");

        HttpRequest createTask1Request = HttpRequest.newBuilder()
                .uri(uri)
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(task1)))
                .build();
        client.send(createTask1Request, HttpResponse.BodyHandlers.ofString());

        HttpRequest createTask2Request = HttpRequest.newBuilder()
                .uri(uri)
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(task2)))
                .build();
        client.send(createTask2Request, HttpResponse.BodyHandlers.ofString());

        HttpRequest request = HttpRequest.newBuilder().uri(uri).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Type taskType = new TypeToken<ArrayList<Task>>() {}.getType();
        List<Task> actual = gson.fromJson(response.body(), taskType);
        List<Task> expected = List.of(task1, task2);

        assertNotNull(actual, "Задачи не возвращаются");
        assertEquals(2, actual.size());
        assertArrayEquals(expected.toArray(), actual.toArray());
    }
}