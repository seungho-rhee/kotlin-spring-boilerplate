CREATE TABLE users (
    id           INT PRIMARY KEY NOT NULL AUTO_INCREMENT,
    name         VARCHAR(255)             NOT NULL,
    birth        VARCHAR(255)             NOT NULL,
    gender       VARCHAR(255)             NOT NULL,
    phone_number VARCHAR(255) NULL,
    joined_at    DATETIME(3) NOT NULL,
    left_at      DATETIME(3) NULL,
    created_at   DATETIME(3) DEFAULT CURRENT_TIMESTAMP(3) NOT NULL,
    updated_at   DATETIME(3) DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) NOT NULL
);
