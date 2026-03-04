CREATE TABLE users
(
    id       BIGSERIAL PRIMARY KEY,
    name     VARCHAR(50)         NOT NULL,
    email    VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(100)        NOT NULL,
    role     VARCHAR(50)         NOT NULL CHECK (role IN ('ADMIN'))
);

CREATE TABLE persistent_logins
(
    username  VARCHAR(64) NOT NULL,
    series    VARCHAR(64) PRIMARY KEY,
    token     VARCHAR(64) NOT NULL,
    last_used TIMESTAMP   NOT NULL
);