package ru.yandex.practicum.filmorate.storage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.controllers.UserController;
import ru.yandex.practicum.filmorate.errors.httpExceptions.StorageException;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.util.List;

@Component
public class DbUserStorage implements UserStorageInterface {
    private final JdbcTemplate jdbcTemplate;
    private static final org.slf4j.Logger log = UserController.getLogger();

    @Autowired
    public DbUserStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Integer getSize() {
        String sql = "select count(*) from user_table";
        return jdbcTemplate.queryForObject(sql, Integer.class);
    }

    @Override
    public Boolean isEmpty() {
        return getSize() == 0;
    }

    @Override
    public Boolean contains(Integer userId) {
        String sql = "select count(*) from user_table where id=?";
        return jdbcTemplate.queryForObject(sql, Integer.class, userId) != 0;
    }

    @Override
    public Boolean contains(User user) {
        return contains(user.getId());
    }

    @Override
    public List<User> getUsers() {
        String sql = "select * from USERS_VIEW";
        return jdbcTemplate.query(sql, (rs, rn) -> {
            return new User(rs.getInt("id"),
                    rs.getString("email"),
                    rs.getString("login"),
                    rs.getString("name"),
                    rs.getDate("birthday").toLocalDate()
            );
        });
    }

    @Override
    public User getUser(int id) {
        String sql = "select * from USER_TABLE where id=?";
        return jdbcTemplate.queryForObject(sql, (rs, q) -> {
            return new User(rs.getInt("id"),
                    rs.getString("email"),
                    rs.getString("login"),
                    rs.getString("name"),
                    rs.getDate("birthday").toLocalDate()
            );
        }, id);

    }

    @Override
    public User addUser(User user) {
        String sqlQuery = "insert into USER_TABLE(EMAIL, LOGIN, NAME, BIRTHDAY) " +
                "values (?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sqlQuery, new String[]{"id"});
            stmt.setString(1, user.getEmail());
            stmt.setString(2, user.getLogin());
            stmt.setString(3, user.getName());
            stmt.setDate(4, Date.valueOf(user.getBirthday()));
            return stmt;
        }, keyHolder);
        user.setId(keyHolder.getKey().intValue());
        return user;
    }

    @Override
    public User updateUser(User user) {
        String sql = "update user_table SET EMAIL=?, LOGIN=?, NAME=?, BIRTHDAY=? WHERE id=?";
        jdbcTemplate.update(sql, user.getEmail(), user.getLogin(), user.getName(), user.getBirthday(), user.getId());
        return user;
    }

    private boolean isFriendOf(int idFriend, int idUser) {
        String sql = "select count(*) " +
                "from user_friend_table as uf1 " +
                "where uf1.ID_USER=? AND uf1.ID_FRIEND=? ";
        return jdbcTemplate.queryForObject(sql, Integer.class, idUser, idFriend) != 0;
    }

    @Override
    public User addFriend(int idUser, int idFriend) {
        if (!contains(idUser)) throw new StorageException("user with id=" + idUser + " doesn't exist");
        if (!contains(idFriend)) throw new StorageException("user with id=" + idFriend + " doesn't exist");
        String sql = "insert into user_friend_table (id_user, id_friend)" +
                "VALUES(?, ?)";
        jdbcTemplate.update(sql, idUser, idFriend);
        return getUser(idFriend);
    }

    @Override
    public List<User> getFriends(int idUser) {
        String sql = "select id, email, login, name, birthday from USER_TABLE " +
                "where id in (" +
                "SELECT id_friend " +
                "FROM user_friend_table " +
                "WHERE id_user=? " +
                ")";
        return jdbcTemplate.query(sql, (rs, rn) -> {
            return new User(rs.getInt("id"),
                    rs.getString("email"),
                    rs.getString("login"),
                    rs.getString("name"),
                    rs.getDate("birthday").toLocalDate()
            );
        }, idUser);
    }

    @Override
    public List<User> deleteFriend(int idUser, int idFriend) {
        String sql = "delete from user_friend_table " +
                "where ID_USER=? AND ID_FRIEND=?";
        jdbcTemplate.update(sql, idUser, idFriend);
        return getFriends(idUser);
    }

    @Override
    public List<User> getCommonFriends(Integer idUser1, Integer idUser2) {
        if (idUser1 == null || idUser2 == null ||
                !contains(idUser1) || !contains(idUser2)) return List.of();
        String sql = "select id, email, login, name, birthday from USER_TABLE " +
                "where id in (" +
                "            select t1.ID_FRIEND from USER_FRIEND_TABLE as t1" +
                "            LEFT JOIN USER_FRIEND_TABLE as t2 on t1.ID_FRIEND=t2.ID_FRIEND " +
                "            WHERE t1.ID_USER=? AND t2.ID_USER=?) ";
        return jdbcTemplate.query(sql, (rs, rn) -> {
            return new User(rs.getInt("id"),
                    rs.getString("email"),
                    rs.getString("login"),
                    rs.getString("name"),
                    rs.getDate("birthday").toLocalDate()
            );
        }, idUser1, idUser2);
    }
}
