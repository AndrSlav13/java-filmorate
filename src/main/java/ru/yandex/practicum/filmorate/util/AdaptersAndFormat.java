package ru.yandex.practicum.filmorate.util;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.io.IOException;
import java.lang.reflect.Type;
import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class AdaptersAndFormat {
    public static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    public static final Type FilmListType = new TypeToken<ArrayList<Film>>() {
    }.getType();
    public static final Type UserListType = new TypeToken<ArrayList<User>>() {
    }.getType();

    public static final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .registerTypeAdapter(LocalDate.class, new AdaptersAndFormat.LocalDateAdapter())
            //.registerTypeAdapter(Duration.class, new AdaptersAndFormat.DurationAdapter())
            .registerTypeAdapter(Film.class, new AdaptersAndFormat.FilmSerializer())
            .registerTypeAdapter(User.class, new AdaptersAndFormat.UserSerializer())
            .create();

    public static class LocalDateAdapter extends TypeAdapter<LocalDate> {

        @Override
        public void write(final JsonWriter jsonWriter, final LocalDate localDate) throws IOException {
            jsonWriter.value(localDate.format(formatter));
        }

        @Override
        public LocalDate read(final JsonReader jsonReader) throws IOException {
            return LocalDate.parse(jsonReader.nextString(), formatter);
        }
    }

    public static class DurationAdapter extends TypeAdapter<Duration> {
        @Override
        public void write(final JsonWriter jsonWriter, final Duration duration) throws IOException {
            jsonWriter.value("" + duration.toMinutes());
        }

        @Override
        public Duration read(final JsonReader jsonReader) throws IOException {
            return Duration.ofMinutes(Integer.parseInt(jsonReader.nextString()));
        }
    }


    public static class FilmSerializer implements JsonSerializer<Film>, JsonDeserializer<Film> {
        @Override
        public JsonElement serialize(Film film, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject el = new JsonObject();
            el.add("id", new JsonPrimitive(film.getId()));
            el.add("name", new JsonPrimitive(film.getName()));
            el.add("description", new JsonPrimitive(film.getDescription()));
            if (film.getReleaseDate() != null)
                el.add("releaseDate", context.serialize(film.getReleaseDate(), LocalDate.class));
            if (film.getDuration() != null) el.add("duration", new JsonPrimitive(film.getDuration()));
            return el;
        }

        @Override
        public Film deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            Film film = new Film();

            JsonObject data = json.getAsJsonObject();
            film.setId(data.get("id").getAsInt());

            String name = data.get("name").getAsString();
            film.setName(name);

            String description = data.get("description").getAsString();
            film.setDescription(description);

            LocalDate date = gson.fromJson(data.get("releaseDate").getAsString(), LocalDate.class);
            film.setReleaseDate(date);

            Integer dur = data.get("duration").getAsInt();
            film.setDuration(dur);

            return film;
        }
    }


    public static class UserSerializer implements JsonSerializer<User>, JsonDeserializer<User> {
        @Override
        public JsonElement serialize(User user, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject el = new JsonObject();
            el.add("id", new JsonPrimitive(user.getId()));
            el.add("email", new JsonPrimitive(user.getEmail()));
            el.add("login", new JsonPrimitive(user.getLogin()));
            if (user.getName() != null) el.add("name", new JsonPrimitive(user.getName()));
            if (user.getBirthday() != null) el.add("birthday", context.serialize(user.getBirthday(), LocalDate.class));
            return el;
        }

        @Override
        public User deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            User user = new User();

            JsonObject data = json.getAsJsonObject();
            user.setId(data.get("id").getAsInt());

            String email = data.get("email").getAsString();
            user.setEmail(email);

            String login = data.get("login").getAsString();
            user.setLogin(login);

            String name = data.get("name").getAsString();
            user.setName(name);

            LocalDate date = gson.fromJson(data.get("birthday").getAsString(), LocalDate.class);
            user.setBirthday(date);

            return user;
        }
    }


}
