create table if not exists users (
    id bigint not null primary key,
    email varchar(64) unique not null,
    login varchar(64) unique not null,
    name varchar(64),
    birthdate date not null
);

create table if not exists mpa_rating (
    id int not null primary key,
    name varchar(30) not null
);

create table if not exists genres (
    id int not null primary key,
    name varchar(50) not null
);

create table if not exists friendship_table (
    first_user_id bigint not null references users(id),
    second_user_id bigint not null references users(id),
    status boolean,
    unique (first_user_id, second_user_id)
);

create table if not exists films (
    id bigint not null primary key auto_increment,
    name varchar(100) unique not null,
    description varchar(1000) not null,
    release_date date not null,
    duration bigint not null,
    rate bigint,
    mpa_rating_id bigint not null references mpa_rating(id)
);

create table if not exists film_likes (
    film_id bigint not null references films(id),
    user_id bigint not null references users(id)
);

create table if not exists film_genre (
    film_id bigint not null references films(id),
    genre_id bigint not null references genres(id)
);

merge into MPA_RATING key (id)
    VALUES (1, 'G'),
           ( 2, 'PG'),
           ( 3, 'PG-13') ,
           ( 4, 'R'),
           ( 5, 'NC-17');

merge into GENRES key (ID)
    VALUES  (1, 'Комедия'),
            (2, 'Драма'),
            (3, 'Мультфильм'),
            (4, 'Ужасы'),
            (5, 'Триллер'),
            (6, 'Детектив');