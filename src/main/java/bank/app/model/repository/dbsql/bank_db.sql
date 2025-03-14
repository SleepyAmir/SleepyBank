CREATE TABLE USERS
(
    USER_ID    NUMBER PRIMARY KEY,
    FIRSTNAME  NVARCHAR2(15) NOT NULL,
    LASTNAME   NVARCHAR2(30) NOT NULL,
    EMAIL      NVARCHAR2(30) NOT NULL,
    PHONE      NUMBER(12)    NOT NULL,
    ADDRESS    NVARCHAR2(30) NOT NULL,
    BIRTH_DATE DATE,
    USERNAME   VARCHAR2(30) UNIQUE,
    PASSWORD   VARCHAR2(16)  NOT NULL,
    ROLE_NAME  VARCHAR2(10),
    IS_ACTIVE  NUMBER(1) DEFAULT 1
);
CREATE SEQUENCE USER_SEQ START WITH 1 INCREMENT BY 1;

CREATE TABLE CARD
(
    ID           NUMBER PRIMARY KEY,
    ACCOUNT_TYPE NVARCHAR2(15) NOT NULL,
    BALANCE      NUMBER,
    CREATED_AT   TIMESTAMP,
    CARD_NUMBER  NVARCHAR2(18) NOT NULL UNIQUE,
    CVV2         NVARCHAR2(4),
    EXPIRY_DATE  DATE,
    U_ID         REFERENCES USERS
);
CREATE SEQUENCE CARD_SEQ START WITH 1 INCREMENT BY 1;

CREATE TABLE CHEQUES
(
    ID            NUMBER PRIMARY KEY,
    ACCOUNT_TYPE  NVARCHAR2(15) NOT NULL,
    BALANCE       NUMBER,
    CREATED_AT    TIMESTAMP,
    CHEQUE_NUMBER NVARCHAR2(16) NOT NULL UNIQUE,
    PASS_DATE     DATE,
    AMOUNT        NUMBER,
    RECEIVER      NVARCHAR2(30),
    DESCRIPTION   NVARCHAR2(100),
    U_ID          REFERENCES USERS
);
CREATE SEQUENCE CHEQUE_SEQ START WITH 1 INCREMENT BY 1;

CREATE TABLE TRANSACTIONS
(
    ID                  NUMBER PRIMARY KEY,
    SOURCE_ACCOUNT      NUMBER REFERENCES CARD(ID),
    DESTINATION_ACCOUNT NUMBER REFERENCES CARD(ID),
    AMOUNT              NUMBER,
    TRANSACTION_TYPE    NVARCHAR2(12) NOT NULL,
    TRANSACTION_TIME    TIMESTAMP,
    DESCRIPTION         NVARCHAR2(100)
);
CREATE SEQUENCE TRANSACTION_SEQ START WITH 6 INCREMENT BY 1;