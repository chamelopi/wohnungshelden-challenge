insert into applications (id, email, last_name, number_of_persons, wbs_present, property_id, status, creation_source)
values
    (1, 'blah@blub.de', 'blah', null, false, 12, 0, 0),
    (2, 'blah@blub.de', 'hello', 1, false, 13, 2, 1),
    (3, 'user@blub.de', 'user', 2, true, 12, 0, 1),
    (4, 'user@blub.de', 'user', 2, false, 10, 0, 0),
    (5, 'user@blub.de', 'user', 3, false, 11, 0, 1),
    (6, 'user2@blub.de', 'user2', 2, true, 13, 1, 0),
    (7, 'user3@blub.de', 'user3', 2, false, 20, 2, 0);