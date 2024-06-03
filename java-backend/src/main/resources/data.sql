-- USERS --
insert into users(deleted, email, first_name, is_admin, last_login, last_name, password, username, display_name, description)
    values (false, 'pera@mail.com', 'Pera', true, null, 'Peric', '$2a$12$6LRoZ4kDywW7WnK9bg16A.XXVHgKXxpi6YZ5JYptFnwW3y97DZGju', 'pera',
            '-pera-', 'Admin you will find in every project');
insert into users(deleted, email, first_name, is_admin, last_login, last_name, password, username, display_name)
    values (false, 'mika@mail.com', 'Mika', false, null, 'Mikic', '$2a$12$15ymkpdnVT1DGRfGjjqIY.SnwcaMTyiIUb71f3r3Be8i3zHuNRM.i', 'mika',
            'mika-mika');
insert into users(deleted, email, first_name, is_admin, last_login, last_name, password, username)
    values (false, 'ana@mail.com', 'Ana', false, null, 'Anic', '$2a$12$uVuGNCVu62e8v7YtlF9yZurtYkvWgOj9N5UEdb51eB1EM959We.v.', 'ana');
insert into users(deleted, email, first_name, is_admin, last_login, last_name, password, username)
    values (true, 'zika@mail.com', 'Zika', false, null, 'Zikic', '$2a$12$TeQF.oCNjgTsl9rFWA9Tb.zA3716nzJZ5wwxONeu1tTzHoqBkk7FK', 'zika');

-- USERS FRIENDS --
insert into user_friends(user_id, friend_id) values (2, 3);

-- FRIEND REQUESTS --
insert into friend_requests (approved, at, created_at, deleted, from_user_id, to_user_id)
    values (null, null, '2023-05-13', false, 1, 3);

-- BANNED --
insert into banned (deleted, timestamp, by_user_id, group_id, towards_user_id)
    values (false, '2023-05-13', 1, null, 4);

-- REPORTS --
insert into reports (accepted, deleted, reason, timestamp, by_user_id, on_comment_id, on_post_id, on_user_id)
    values (true, false, 'HARASSMENT', '2023-05-12', 3, null, null, 4);

-- POSTS --
insert into posts (title, content, creation_date, deleted, posted_by_user_id)
    values ('Title number 1', 'This is content intended for a test. If you see it, good for you',
            '2023-05-12 17:05:00', false, 1);
insert into posts (title, content, creation_date, deleted, posted_by_user_id)
    values ('Another title', 'This is another content intended for a test. Say hi to everyone reading this',
            '2023-05-13 13:02:30', true, 2);
insert into posts (title, content, creation_date, deleted, posted_by_user_id)
    values ('Sun and no clouds', 'This is a post in a group. If you see it, you are inside a group',
            '2023-05-14 15:23:35', false, 1);
insert into posts (title, content, creation_date, deleted, posted_by_user_id)
    values ('Clouds and rain', 'This is another post for a group. Say hi to everyone in this group',
            '2023-05-15 14:56:55', false, 2);
insert into posts (title, content, creation_date, deleted, posted_by_user_id)
    values ('Sea travel', 'This is test post for second group. Say hi to everyone in this group',
            '2023-06-13 12:12:12', false, 3);
insert into posts (title, content, creation_date, deleted, posted_by_user_id)
    values ('Ana love cooking', 'This is Ana\'s global post. Friends can see it',
        '2023-06-20 05:22:12', false, 3);
insert into posts (title, content, creation_date, deleted, posted_by_user_id)
    values ('Zika was banned for this post', 'This is Mika\'s global post. Friends can see it',
            '2023-06-27 01:12:39', false, 2);

-- COMMENTS --
insert into comments (deleted, text, timestamp, belongs_to_post_id, belongs_to_user_id, replies_to_comment_id)
    values (false, 'Nice message', '2023-05-13', 1, 2, null);
insert into comments (deleted, text, timestamp, belongs_to_post_id, belongs_to_user_id, replies_to_comment_id)
    values (false, 'Nice answer', '2023-06-22', 1, 1, 1);
insert into comments (deleted, text, timestamp, belongs_to_post_id, belongs_to_user_id, replies_to_comment_id)
    values (false, 'Another nice message', '2023-06-22', 1, 3, null);

-- REACTIONS --
insert into reactions (deleted, timestamp, type, made_by_user_id, on_comment_id, on_post_id)
    values (false, '2023-05-12', 'HEART', 3, null, 1);
insert into reactions (deleted, timestamp, type, made_by_user_id, on_comment_id, on_post_id)
    values (false, '2023-05-13', 'LIKE', 3, 1, null);

-- IMAGES --
insert into images (deleted, path, belongs_to_post_id, belongs_to_user_id)
    values (false, '../../assets/images/profile.png', 1, null);
insert into images (deleted, path, belongs_to_post_id, belongs_to_user_id)
    values (false, '../../assets/images/profile-p.png', null, 1);
insert into images (deleted, path, belongs_to_post_id, belongs_to_user_id)
    values (false, '../../assets/images/profile-m.png', null, 2);

-- GROUPS --
insert into groupss (creation_date, deleted, description, is_suspended, name, suspended_reason, rules)
    values ('2023-05-11', false, 'Test group for testing purposes', false, 'Test Group 1', null, 'No politics');
insert into groupss (creation_date, deleted, description, is_suspended, name, suspended_reason, rules)
    values ('2023-05-11', false, 'Another test group for testing', false, 'Test Group 2', null, 'No politics or animals');
insert into groupss (creation_date, deleted, description, is_suspended, name, suspended_reason, rules)
    values ('2023-05-12', true, 'Deleted test group for testing', true, 'Test Group 3', 'Un-moderated', 'No testing');

-- GROUP MEMBERS --
insert into group_members (group_id, member_id)
    values (1, 1);
insert into group_members (group_id, member_id)
    values (1, 2);
insert into group_members (group_id, member_id)
    values (2, 3);

-- GROUP ADMINS --
insert into group_admins (group_id, admin_id)
    values (1, 1);
insert into group_admins (group_id, admin_id)
    values (2, 3);

-- GROUP POSTS --
insert into group_posts (group_id, post_id)
    values (1, 3);
insert into group_posts (group_id, post_id)
    values (1, 4);
insert into group_posts (group_id, post_id)
    values (2, 5);

-- GROUP REQUESTS --
insert into group_requests (approved, at, created_at, deleted, created_by_user_id, for_group_id)
    values (null, null, '2023-05-13', false, 3, 1);
