CREATE TABLE USER
(
    id				SERIAL			NOT NULL,
    username		VARCHAR(50)		NOT NULL,
    password		VARCHAR(128)	NOT NULL,
    firstname		VARCHAR(128)	NOT NULL,
    lastname		VARCHAR(128)	NOT NULL,
    phone_number    VARCHAR(20),
    city            VARCHAR(20),
    country         VARCHAR(50),
    enabled			INTEGER         NOT NULL,
    gender			INTEGER         NOT NULL,
    language		INTEGER,
    occupation		INTEGER,
   	education		VARCHAR(20),
    blood_type		INTEGER,
    marital_status	VARCHAR(20),
    religion        VARCHAR(20),
    birth_date      DATE
);

ALTER TABLE USER ADD CONSTRAINT PK_USER PRIMARY KEY
(
     id
);

CREATE TABLE ROLE
(
    id 				SERIAL 			NOT NULL,
    name			VARCHAR(100)	NOT NULL
);

ALTER TABLE ROLE ADD CONSTRAINT PK_ROLE PRIMARY KEY
(
     id
);

CREATE TABLE PERMISSION
(
    id				SERIAL			NOT NULL,
    name			VARCHAR(100)	NOT NULL
);

ALTER TABLE PERMISSION ADD CONSTRAINT PK_PERMISSION PRIMARY KEY
(
     id
);


CREATE TABLE CREDIT_CARD_INFO
(
    id					SERIAL			NOT NULL,
    credit_card_number	VARCHAR(20)		NOT NULL,
    security_code		INTEGER			NOT NULL,
    expiration_date		DATE			NOT NULL
);

ALTER TABLE CREDIT_CARD_INFO ADD CONSTRAINT PK_CREDIT_CARD_INFO PRIMARY KEY
(
     id
);

CREATE TABLE USER_ROLE
(
    user_id			INTEGER			NOT NULL,
    role_id			INTEGER         NOT NULL
);


ALTER TABLE USER_ROLE ADD CONSTRAINT PK_USER_ROLE PRIMARY KEY
(
     user_id,
     role_id
);

CREATE TABLE ROLE_PERMISSION
(
    role_id			INTEGER 		NOT NULL,
    permission_id	INTEGER			NOT NULL
);

ALTER TABLE ROLE_PERMISSION ADD CONSTRAINT PK_ROLE_PERMISSION PRIMARY KEY
(
     role_id,
     permission_id
);

CREATE TABLE USER_CREDIT_CARD_INFO
(
    user_id					INTEGER			NOT NULL,
    credit_card_info_id		INTEGER         NOT NULL
);


ALTER TABLE USER_CREDIT_CARD_INFO ADD CONSTRAINT PK_USER_CREDIT_CARD_INFO PRIMARY KEY
(
     user_id,
     credit_card_info_id
);

CREATE TABLE USER_SECONDARY_CREDIT_CARD_INFO
(
    user_id					INTEGER			NOT NULL,
    credit_card_info_id		INTEGER         NOT NULL
);


ALTER TABLE USER_SECONDARY_CREDIT_CARD_INFO ADD CONSTRAINT PK_USER_SECONDARY_CREDIT_CARD_INFO PRIMARY KEY
(
     user_id,
     credit_card_info_id
);
