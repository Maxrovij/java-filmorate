-- Exported from QuickDBD: https://www.quickdatabasediagrams.com/
-- Link to schema: https://app.quickdatabasediagrams.com/#/d/0V0mk2
-- NOTE! If you have used non-SQL datatypes in your design, you will have to change these here.


CREATE TABLE "Users" (
    "id" long   NOT NULL,
    "email" varchar   NOT NULL,
    "login" varchar   NOT NULL,
    "name" varchar   NOT NULL,
    "birthday" date   NOT NULL,
    CONSTRAINT "pk_Users" PRIMARY KEY (
        "id"
     ),
    CONSTRAINT "uc_Users_email" UNIQUE (
        "email"
    ),
    CONSTRAINT "uc_Users_login" UNIQUE (
        "login"
    )
);

CREATE TABLE "Friendship_table" (
    "first_user_id" long   NOT NULL,
    "second_user_id" long   NOT NULL,
    "friendship_status" boolean   NOT NULL
);

CREATE TABLE "Films" (
    "id" long   NOT NULL,
    "name" varchar   NOT NULL,
    "description" varchar(200)   NOT NULL,
    "release_date" date   NOT NULL,
    "duration" long   NOT NULL,
    "rating" string   NOT NULL,
    CONSTRAINT "pk_Films" PRIMARY KEY (
        "id"
     ),
    CONSTRAINT "uc_Films_name" UNIQUE (
        "name"
    )
);

CREATE TABLE "Genres" (
    "genre_id" long   NOT NULL,
    "name" string   NOT NULL,
    CONSTRAINT "pk_Genres" PRIMARY KEY (
        "genre_id"
     )
);

CREATE TABLE "Film-Genre" (
    "film_id" long   NOT NULL,
    "genre_id" long   NOT NULL
);

CREATE TABLE "Rating" (
    "rating_id" long   NOT NULL,
    "value" string   NOT NULL,
    CONSTRAINT "pk_Rating" PRIMARY KEY (
        "rating_id"
     )
);

CREATE TABLE "Film-Rating" (
    "film_id" long   NOT NULL,
    "rating_id" long   NOT NULL
);

CREATE TABLE "Film-Likes" (
    "film_id" long   NOT NULL,
    "user_id" long   NOT NULL
);

ALTER TABLE "Friendship_table" ADD CONSTRAINT "fk_Friendship_table_first_user_id" FOREIGN KEY("first_user_id")
REFERENCES "Users" ("id");

ALTER TABLE "Friendship_table" ADD CONSTRAINT "fk_Friendship_table_second_user_id" FOREIGN KEY("second_user_id")
REFERENCES "Users" ("id");

ALTER TABLE "Film-Genre" ADD CONSTRAINT "fk_Film-Genre_film_id" FOREIGN KEY("film_id")
REFERENCES "Films" ("id");

ALTER TABLE "Film-Genre" ADD CONSTRAINT "fk_Film-Genre_genre_id" FOREIGN KEY("genre_id")
REFERENCES "Genres" ("genre_id");

ALTER TABLE "Film-Rating" ADD CONSTRAINT "fk_Film-Rating_film_id" FOREIGN KEY("film_id")
REFERENCES "Films" ("id");

ALTER TABLE "Film-Rating" ADD CONSTRAINT "fk_Film-Rating_rating_id" FOREIGN KEY("rating_id")
REFERENCES "Rating" ("rating_id");

ALTER TABLE "Film-Likes" ADD CONSTRAINT "fk_Film-Likes_film_id" FOREIGN KEY("film_id")
REFERENCES "Films" ("id");

ALTER TABLE "Film-Likes" ADD CONSTRAINT "fk_Film-Likes_user_id" FOREIGN KEY("user_id")
REFERENCES "Users" ("id");

