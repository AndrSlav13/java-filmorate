/*
Клиент для проведения тестов
**/
package ru.yandex.practicum.filmorate.client;

import ru.yandex.practicum.filmorate.errors.httpExceptions.HttpRequestUserException;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class ClientForTests {
    private final URI uriToServer;

    public ClientForTests(String uri) {
        try {
            uriToServer = new URI(uri);
        } catch (URISyntaxException ex) {
            ex.printStackTrace();
            throw new RuntimeException("unable to create client for tests");
        }
    }

    public HttpResponse<String> post(String param, String json) {
        HttpResponse<String> response = null;
        try {
            URI uri = URI.create(uriToServer.toString() + param);
            HttpRequest.Builder requestBuilder = HttpRequest.newBuilder();
            HttpRequest request = requestBuilder.POST(HttpRequest.BodyPublishers.ofString(json))    // указываем HTTP-метод запроса
                    .uri(uri) // указываем адрес ресурса
                    .version(HttpClient.Version.HTTP_1_1) // указываем версию протокола HTTP
                    .header("Accept", "application/json") // указываем заголовок Accept
                    .header("Accept", "text/plain") // указываем заголовок Accept
                    .header("Content-Type", "application/json") // указываем заголовок Accept
                    .build(); // заканчиваем настройку и создаём ("строим") HTTP-запрос
            HttpClient client = HttpClient.newHttpClient();
            // получаем стандартный обработчик тела запроса с конвертацией содержимого в строку
            HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
            // отправляем запрос и получаем ответ от сервера
            response = client.send(request, handler);

        } catch (IOException | InterruptedException ex) {
            ex.printStackTrace();
            throw new HttpRequestUserException("Unable to create client");
        } catch (HttpRequestUserException ex) {
            System.out.println(ex.getMessage());
        }
        return response;
    }

    public HttpResponse<String> put(String param, String json) {
        HttpResponse<String> response = null;
        try {
            URI uri = URI.create(uriToServer.toString() + param);
            HttpRequest.Builder requestBuilder = HttpRequest.newBuilder();
            HttpRequest request = requestBuilder.PUT(HttpRequest.BodyPublishers.ofString(json))    // указываем HTTP-метод запроса
                    .uri(uri) // указываем адрес ресурса
                    .version(HttpClient.Version.HTTP_1_1) // указываем версию протокола HTTP
                    .header("Accept", "application/json") // указываем заголовок Accept
                    .header("Accept", "text/plain") // указываем заголовок Accept
                    .header("Content-Type", "application/json") // указываем заголовок Accept
                    .build(); // заканчиваем настройку и создаём ("строим") HTTP-запрос
            HttpClient client = HttpClient.newHttpClient();
            // получаем стандартный обработчик тела запроса с конвертацией содержимого в строку
            HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
            // отправляем запрос и получаем ответ от сервера
            response = client.send(request, handler);

        } catch (IOException | InterruptedException ex) {
            ex.printStackTrace();
            throw new HttpRequestUserException("Unable to create client");
        } catch (HttpRequestUserException ex) {
            System.out.println(ex.getMessage());
        }
        return response;
    }


    public HttpResponse<String> get(String param) {
        HttpResponse<String> response = null;
        try {
            URI uri = URI.create(uriToServer.toString() + param);
            HttpRequest.Builder requestBuilder = HttpRequest.newBuilder();
            HttpRequest request = requestBuilder.GET()    // указываем HTTP-метод запроса
                    .uri(uri) // указываем адрес ресурса
                    .version(HttpClient.Version.HTTP_1_1) // указываем версию протокола HTTP
                    .header("Accept", "application/json") // указываем заголовок Accept
                    .header("Content-Type", "application/json") // указываем заголовок Accept
                    .build(); // заканчиваем настройку и создаём ("строим") HTTP-запрос
            HttpClient client = HttpClient.newHttpClient();
            // получаем стандартный обработчик тела запроса с конвертацией содержимого в строку
            HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
            // отправляем запрос и получаем ответ от сервера
            response = client.send(request, handler);

        } catch (IOException | InterruptedException ex) {
            ex.printStackTrace();
            throw new HttpRequestUserException("Unable to create client");
        } catch (HttpRequestUserException ex) {
            System.out.println(ex.getMessage());
        }
        return response;
    }

}

