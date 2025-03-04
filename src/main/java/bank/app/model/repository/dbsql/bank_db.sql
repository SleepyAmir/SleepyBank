create table accounts
(
    id number primary key,
    user nvarchar2(30)not null ,
    account_type  nvarchar2(15) not null,
    balance number,
    creation_date date
);

create table card
(
    id number primary key ,
    user nvarchar2(30)not null ,
    account_type  nvarchar2(15) not null,
    balance number,
    card_number number,
    cvv2 number,
    expiry_date date
);
create table cheques
(
    id number primary key,
    user nvarchar2(30)not null ,
    account_type  nvarchar2(15) not null,
    balance number,
    cheque_address nvarchar2(12),
    status nvarchar2(12)

);


CREATE TABLE transactions
(
    id number primary key,
    source_acc number,
    destination_acc number,
    amount number,
    transaction_type nvarchar2(12) not null ,
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

