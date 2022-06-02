# java-filmorate
Template repository for Filmorate project.

![FilmorateDBD](/images/FilmorateDBD.png)
![](/images/Filmorate DataBase Documentation.pdf)

__Данные о пользователях хранятся в таблице --Users--__. В ней записываются личные данные.

Поля email и login отмечены как уникальные и не могут повторяться у разных пользователей.

_Пример запроса на получение списка с информацией обо всех пользователях_:

SELECT *

FROM Users

_Запрос на получение информации о пользователе по id_:

SELECT*

FROM Users

WHERE id = {id пользователя}
___________________________________________________________________________________________

__Информация о добавлении в друзья хранится в таблице --Friendship_table--__. Она имеет два поля с id пользователей и одно поле,
идентифицирующее статус дружбы. Порядок добавления id пользователей указывает на то, кто отправил заявку на добавление 
в друзья, а значение поля friendship_status содержит значение true, если заявка принята или false, если еще не принята. 
Если же заявка отклонена, то запись из таблицы удаляется.
Первичным ключом в этой таблице будет сочетание id пользователей.

Запрос на получение списка id друзей пользователя:

SELECT second_user_id AS user_friends

FROM Friendship_table 

WHERE first_user_id = {id пользователя} 

AND friendship_status = true

UNION

SELECT first_user_id AS user_friends

FROM Friendship_table

WHERE second_user_id = {id пользователя}

AND friendship_status = true;
___________________________________________________________________________________________

_Данные о фильмах хранятся в таблице --**Films**--_. В ней записывается информация, относящаяся непосредственно к файлу с фильмом.
Поле name отмечено как уникальное и не может повторяться.

_Запрос на получение фильма по id:_

SELECT *

FROM Films

WHERE id = {id фильма}

___________________________________________________________________________________________

Таблица Film-Genre хранит соответствие фильм-жанр. Первичный ключ этой таблицы - сочетание
id фильма и id жанра.
___________________________________________________________________________________________

_Таблица --**Film-Likes**-- содержит соответствие id фильма и id пользователей, поставивших лайк фильму._

_Пример запроса на получение количества лайков к каждому фильму:_

SELECT f.name AS Film_Name,

COUNT(fl.user_id) AS likes_count

FROM Films AS f

LEFT OUTER JOIN Film-Likes AS fl ON f.id = fl.film_id

GROUP BY f.id
______________________________________________________________________________________________