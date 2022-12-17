package ru.yandex.practicum.filmorate.storage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.controllers.UserController;
import ru.yandex.practicum.filmorate.errors.httpExceptions.HttpRequestUserException;
import ru.yandex.practicum.filmorate.errors.httpExceptions.StorageException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.services.UserService;
import ru.yandex.practicum.filmorate.services.UserServiceInterface;
import ru.yandex.practicum.filmorate.util.FILMS_ENUM;
import ru.yandex.practicum.filmorate.util.Genre;
import ru.yandex.practicum.filmorate.util.MPA;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class DbFilmStorage implements FilmStorageInterface {
    private final JdbcTemplate jdbcTemplate;
    private final UserServiceInterface userService;

    private static final org.slf4j.Logger log = UserController.getLogger();

    @Autowired
    public DbFilmStorage(JdbcTemplate jdbcTemplate, UserService userService) {
        this.jdbcTemplate = jdbcTemplate;
        this.userService = userService;
    }

    @Override
    public Integer getSize() {
        String sql = "select count(*) from FILM_TABLE";
        return jdbcTemplate.queryForObject(sql, Integer.class);
    }


    @Override
    public Boolean isEmpty() {
        return getSize() == 0;
    }

    @Override
    public Boolean contains(Film film) {
        String sql = "select count(*) from FILM_TABLE where id=?";
        return jdbcTemplate.queryForObject(sql, Integer.class, film.getId()) != 0;
    }

    @Override
    public Boolean contains(Integer filmId) {
        String sql = "select count(*) from FILM_TABLE where id=?";
        return jdbcTemplate.queryForObject(sql, Integer.class, filmId) != 0;
    }

    //In database
    private void addGenres(Film film) {
        if (film.getGenres() == null || film.getGenres().isEmpty()) return;
        LinkedHashSet<Genre> hset = new LinkedHashSet<>();
        for (Genre g : film.getGenres()) hset.add(g);

        String upd = "INSERT INTO genre_film_table (id_film, id_genre) VALUES(?, ?) ";
        jdbcTemplate.batchUpdate(upd, hset, hset.size(), (query, gen) -> {
            query.setInt(1, film.getId());
            query.setInt(2, gen.getId());
        });
    }

    //In program
    private void loadGenres(Film film) {
        if (film == null) return;
        String sql = "select g.ID as id_g, g.name as name_g " +
                "from genre_film_table as f " +
                "LEFT JOIN GENRES_TABLE as g on f.ID_GENRE=g.ID " +
                "WHERE f.ID_FILM=? ";

        jdbcTemplate.query(sql, (rs) -> {
            film.addGenre(new Genre(rs.getInt("id_g"), rs.getString("name_g")));
            int f = rs.getInt("id_g");
            double re = 8.9;
        }, film.getId());
    }

    //In program
    private void loadGenres(List<Film> films) {
        if (films == null || films.isEmpty()) return;
        final List<Integer> ids = films.stream().map(Film::getId).collect(Collectors.toList());
        final Map<Integer, Film> filmMap = films.stream().collect(
                Collectors.toMap(film -> film.getId(), film -> film, (a, b) -> b));
        String sql = "select g.ID as id_g, g.name as name_g, f.ID_FILM as id_f " +
                "from genre_film_table as f " +
                "LEFT JOIN GENRES_TABLE as g on f.ID_GENRE=g.ID ";

        jdbcTemplate.query(sql, (rs) -> {
            Film f = filmMap.get(rs.getInt("id_f"));
            if (f != null) f.addGenre(new Genre(rs.getInt("id_g"), rs.getString("name_g")));
        });
    }

    private List<Film> getFs(FILMS_ENUM v) {
        String sql = null;
        switch (v) {
            case POPULAR:
                sql = "select fid, f.name as name, description, releaseDate, duration, mid, mpa " +
                        "from FILMS_VIEW_POPULAR as f ";
                break;
            case ALL:
                sql = "select f.id as fid, f.name as name, description, releaseDate, duration, m.id as mid, m.NAME as mpa " +
                        "from FILM_TABLE as f " +
                        "LEFT JOIN MPA_TABLE as m on f.ID_MPA=m.ID ";
                break;
        }

        List<Film> rezult = new ArrayList<>();
        jdbcTemplate.query(sql, (rs) -> {
            rezult.add(new Film(rs.getInt("fid"),
                    rs.getString("name"),
                    rs.getString("description"),
                    rs.getDate("releaseDate").toLocalDate(),
                    rs.getInt("duration"),
                    new LinkedHashSet<>(),
                    new MPA(rs.getInt("mid"), rs.getString("mpa"))
            ));
        });
        loadGenres(rezult);
        return rezult;
    }

    @Override
    public List<Film> getFilms() {
        return getFs(FILMS_ENUM.ALL);
    }

    @Override
    public Film getFilm(int id) {
        String sql = "select f.id as fid, f.name as name, f.description as description, f.releaseDate as releaseDate, f.duration as duration, m.id as mid, m.NAME as mpa " +
                "from FILM_TABLE AS f " +
                "LEFT JOIN MPA_TABLE as m on f.ID_MPA=m.ID " +
                "where f.id=? ";
        Film rezult =
                jdbcTemplate.query(sql, (rs) -> {
                    if (rs.next()) return new Film(rs.getInt("fid"),
                            rs.getString("name"),
                            rs.getString("description"),
                            rs.getDate("releaseDate").toLocalDate(),
                            rs.getInt("duration"),
                            new LinkedHashSet<>(),
                            new MPA(rs.getInt("mid"), rs.getString("mpa"))
                    );
                    return null;
                }, id);

        loadGenres(rezult);
        return rezult;
    }


    @Override
    public Film addFilm(Film film) {
        String sqlQuery = "insert into FILM_TABLE(NAME, DESCRIPTION, RELEASEDATE, DURATION, ID_MPA) " +
                "values (?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sqlQuery, new String[]{"id"});
            stmt.setString(1, film.getName());
            stmt.setString(2, film.getDescription());
            stmt.setDate(3, Date.valueOf(film.getReleaseDate()));
            stmt.setInt(4, film.getDuration());
            stmt.setInt(5, film.getMpa().getId());
            return stmt;
        }, keyHolder);
        film.setId(keyHolder.getKey().intValue());
        addGenres(film);

        return getFilm(film.getId());
    }

    @Override
    public Film updateFilm(Film film) {
        String sql = "DELETE FROM GENRE_FILM_TABLE " +
                "WHERE ID_FILM=? ";
        jdbcTemplate.update(sql, film.getId());

        sql = "update film_table SET NAME=?, DESCRIPTION=?, RELEASEDATE=?, DURATION=?, ID_MPA=? WHERE id=?";
        jdbcTemplate.update(sql, film.getName(), film.getDescription(), film.getReleaseDate(),
                film.getDuration(), film.getMpa().getId(), film.getId());
        addGenres(film);
        return getFilm(film.getId());
    }

    private List<User> getFriendsLike(int idFilm) {
        String sql = "select id, email, login, name, birthday from USER_TABLE " +
                "where id in (" +
                "SELECT id_user " +
                "FROM like_film_table " +
                "WHERE id_film=? " +
                ")";
        return jdbcTemplate.query(sql, (rs, rn) -> {
            return new User(rs.getInt("id"),
                    rs.getString("email"),
                    rs.getString("login"),
                    rs.getString("name"),
                    rs.getDate("birthday").toLocalDate()
            );
        }, idFilm);
    }

    @Override
    public List<User> addLike(Integer idFilm, Integer idUser) {
        if (!userService.contains(idUser)) throw new StorageException("user with id=" + idUser + " doesn't exist");
        if (!contains(idFilm)) throw new StorageException("film with id=" + idFilm + " doesn't exist");
        String sql = "insert into like_film_table (id_film, id_user)" +
                "VALUES(?, ?)";
        jdbcTemplate.update(sql, idFilm, idUser);
        return getFriendsLike(idFilm);
    }

    @Override
    public List<User> removeLike(Integer idFilm, Integer idUser) {
        String sql = "delete from like_film_table " +
                "where ID_FILM=? AND ID_USER=?";
        jdbcTemplate.update(sql, idFilm, idUser);
        return getFriendsLike(idFilm);
    }

    @Override
    public List<Film> getPopularFilms(Optional<Integer> count) {
        List<Film> rezult = getFs(FILMS_ENUM.POPULAR);
        return rezult.stream().limit(count.orElse(Integer.MAX_VALUE)).collect(Collectors.toList());
    }

    @Override
    public List<Genre> getGenres() {
        String sql = "select id, name " +
                "from genres_table ";

        return jdbcTemplate.query(sql, (rs, s) -> {
            return new Genre(rs.getInt("id"), rs.getString("name"));
        });
    }

    @Override
    public Genre getGenre(int id) {
        String sql = "select id, name " +
                "from genres_table " +
                "where id=? ";

        return jdbcTemplate.query(sql, (rs) -> {
            if (rs.next()) return new Genre(rs.getInt("id"), rs.getString("name"));
            throw new HttpRequestUserException(404, "film genre id=" + id + " doesn't exist");
        }, id);
    }

    @Override
    public List<MPA> getMPAs() {
        String sql = "select id, name " +
                "from mpa_table ";

        return jdbcTemplate.query(sql, (rs, s) -> {
            return new MPA(rs.getInt("id"), rs.getString("name"));
        });
    }

    @Override
    public MPA getMPA(int id) {
        String sql = "select id, name " +
                "from mpa_table " +
                "where id=? ";

        return jdbcTemplate.query(sql, (rs) -> {
            if (rs.next()) return new MPA(rs.getInt("id"), rs.getString("name"));
            throw new HttpRequestUserException(404, "rating mpa id=" + id + " doesn't exist");
        }, id);
    }
}
