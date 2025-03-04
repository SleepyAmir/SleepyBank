create table users
(
    user_id    number primary key,
    firstname  nvarchar2(15) not null,
    lastname   nvarchar2(30) not null,
    email      nvarchar2(30) not null,
    phone      number(12)    not null,
    address    nvarchar2(30) not null,
    birth_date date,
    username   varchar2(30) unique,
    password   varchar2(16)  not null,
    role_name  varchar2(10),
    is_active  number(1) default 1
);

create sequence user_seq start with 1 increment by 1;


create table card
(
    id           number primary key,
    account_type nvarchar2(15) not null,
    balance      number,
    created_at   timestamp,
    card_number  nvarchar2(16) not null unique,
    cvv2         nvarchar2(4),
    expiry_date  date,
    u_id references users
);
create sequence card_seq start with 1 increment by 1;


create table cheques
(
    id            number primary key,
    account_type  nvarchar2(15) not null,
    balance       number,
    created_at    timestamp,
    cheque_number nvarchar2(16) not null unique,
    pass_date     date,
    amount        number,
    receiver      nvarchar2(30),
    description   nvarchar2(100),
    u_id references users
);
create sequence cheque_seq start with 1 increment by 1;


CREATE TABLE transactions
(
    id                  number primary key,
    source_account      number,
    destination_account number,
    amount              number,
    transaction_type    nvarchar2(12) not null,
    transaction_time    timestamp,
    description nvarchar2(100)
);

