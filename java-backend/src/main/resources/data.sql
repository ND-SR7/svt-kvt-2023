-- USERS --
insert into users(deleted, email, first_name, is_admin, last_login, last_name, password, username)
    values (false, 'pera@mail.com', 'Pera', true, null, 'Peric', 'pera123', 'pera');
insert into users(deleted, email, first_name, is_admin, last_login, last_name, password, username)
    values (false, 'mika@mail.com', 'Mika', false, null, 'Mikic', 'mika123', 'mika');
insert into users(deleted, email, first_name, is_admin, last_login, last_name, password, username)
    values (true, 'zika@mail.com', 'Zika', false, null, 'Zikic', 'zika123', 'zika');

-- USERS FRIENDS --
insert into users_friends(user_id, friend_id) values (1, 2)
