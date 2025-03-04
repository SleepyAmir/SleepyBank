CREATE TABLE Accounts
(
    id number primary key
);


CREATE TABLE Cheques
(
    id number primary key

);


CREATE TABLE transactions
(
    id number primary key

);

create table users
(
    user_id    number primary key,
    firstname  nvarchar2(15) not null,
    lastname   nvarchar2(30) not null,
    birth_date date,
    address    nvarchar2(30) not null,
    email      nvarchar2(30) not null,
    phone      number(12)    not null,
    username   varchar2(30) unique,
    password   varchar2(16)  not null,
    is_active  number(1) default 1
)