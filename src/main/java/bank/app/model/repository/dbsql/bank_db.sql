create table accounts
(
    id number primary key,
    account_type  nvarchar2(15) not null,
    balance number
);


create table cheques
(
    id number primary key,
    cheque_address nvarchar2(12),
    status nvarchar2(12)

);


CREATE TABLE transactions
(
    id number primary key,
    sender_id number,
    receive_id number,
    amount number,
    timestamp date

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
);

create table card
(
    card_number number,
    cvv2 number,
    expiry_date date
);