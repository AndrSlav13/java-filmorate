CREATE DOMAIN IF NOT EXISTS EMAIL_TYPE AS VARCHAR(100);
CREATE DOMAIN IF NOT EXISTS LOGIN_TYPE AS VARCHAR(100);

CREATE DOMAIN IF NOT EXISTS NAME_TYPE AS VARCHAR(100);

CREATE DOMAIN IF NOT EXISTS DATE_TYPE AS DATE;


CREATE TABLE IF NOT EXISTS user_table (
                                          id       integer GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
                                          email    EMAIL_TYPE,
                                          login    LOGIN_TYPE UNIQUE ,
                                          name     NAME_TYPE DEFAULT 'John Doe',
                                          birthday DATE_TYPE
);

CREATE TABLE IF NOT EXISTS film_table (
                                          id          int GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
                                          name        NAME_TYPE,
                                          description varchar(100) DEFAULT '',
                                          releaseDate date,
                                          duration    int check (duration > 0),
                                          id_mpa      int
);

CREATE TABLE IF NOT EXISTS friendship_table (
                                                id            integer GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
                                                friendship    text
);

CREATE TABLE IF NOT EXISTS mpa_table (
                                         id               integer GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
                                         name             text
);

CREATE TABLE IF NOT EXISTS genres_table (
                                            id            integer GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
                                            name          text
);

CREATE TABLE IF NOT EXISTS genre_film_table (
                                                id_film          integer,
                                                id_genre         integer,
                                                constraint p1 FOREIGN KEY(id_genre) REFERENCES genres_table(id) on delete cascade,
                                                constraint p2 FOREIGN KEY(id_film) REFERENCES film_table(id) on delete cascade,
                                                primary key (id_film, id_genre)
);

CREATE TABLE IF NOT EXISTS user_friend_table (
                                                 id_user          integer,
                                                 id_friend        integer,
                                                 constraint r1 FOREIGN KEY(id_user) REFERENCES USER_TABLE(id) on delete cascade,
                                                 constraint r2 FOREIGN KEY(id_friend) REFERENCES USER_TABLE(id) on delete cascade,
                                                 PRIMARY KEY (id_user, id_friend)
);


CREATE TABLE IF NOT EXISTS like_film_table (
                                               id_film          integer,
                                               id_user          integer,
                                               FOREIGN KEY(id_user) REFERENCES user_table(id) on delete cascade,
                                               FOREIGN KEY(id_film) REFERENCES film_table(id) on delete cascade,
                                               primary key (id_user, id_film)
);


CREATE VIEW IF NOT EXISTS USERS_VIEW AS (
                                        SELECT usrs.id as id, usrs.name as name, usrs.login as login, usrs.email as email, usrs.birthday as birthday, count(*) as likes_quantity
                                        FROM user_table as usrs
                                                 LEFT JOIN like_film_table as likeTab on usrs.id = likeTab.id_user
                                        GROUP BY usrs.id
                                        ORDER BY count(*));

CREATE VIEW IF NOT EXISTS FILMS_VIEW_POPULAR AS (
                                                select f.id as fid, f.name as name, description, releaseDate, duration, m.id as mid, m.NAME as mpa
                                                from FILM_TABLE as f
                                                         LEFT JOIN MPA_TABLE as m on f.ID_MPA=m.ID
                                                         LEFT JOIN like_film_table as lk on f.id=lk.ID_FILM
                                                GROUP BY f.id
                                                ORDER BY count(lk.id_user) desc);

