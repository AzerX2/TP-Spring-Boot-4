insert into coffee (name, price)
values ('Granador', 10),
       ('Tres Puntas', 70),
       ('Estrellas', 4);

insert into review (author, comment, rating, coffee_id)
select 'Ana', 'Intense', 5, id from coffee where name = 'Granador';

insert into review (author, comment, rating, coffee_id)
select 'Beto', 'Equilibre', 4, id from coffee where name = 'Granador';

insert into review (author, comment, rating, coffee_id)
select 'Cleo', 'Trop fort', 2, id from coffee where name = 'Tres Puntas';
