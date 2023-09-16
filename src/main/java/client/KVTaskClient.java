package client;

import exceptions.RequestFailedException;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class KVTaskClient {
    private final String url;
    private final String apiToken;
    private final HttpClient client;

    public KVTaskClient(int port) {
        url = "http://localhost:" + port + "/";
        client = HttpClient.newHttpClient();
        apiToken = register(url);
    }

    private String register(String url) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url + "register"))
                    .GET()
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                return response.body();
            } else {
                throw new RequestFailedException("Не удалось получить токен, статус " + response.statusCode());
            }
        } catch (IOException | InterruptedException e) {
            throw new RequestFailedException("Не удалось сделать запрос по адресу " + url);
        }
    }

    public void put(String key, String value) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url + "save/" + key + "?API_TOKEN=" + apiToken))
                    .POST(HttpRequest.BodyPublishers.ofString(value))
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                throw new RequestFailedException("Не удалось сохранить данные на сервер " + response.statusCode());
            }
        } catch (IOException | InterruptedException e) {
            throw new RequestFailedException("Не удалось сделать запрос по адресу " + url);
        }
    }

    public String load(String key) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url + "load/" + key + "?API_TOKEN=" + apiToken))
                    .GET()
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                //throw new RequestFailedException("Не удалось получить данные с сервера " + response.statusCode());
                return null;
            } else {
                return response.body();
            }
        } catch (IOException | InterruptedException e) {
            throw new RequestFailedException("Не удалось сделать запрос по адресу " + url);
        }
    }
}
