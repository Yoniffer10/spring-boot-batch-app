USE people;

DROP TABLE IF EXISTS people;

CREATE TABLE people  (
    person_id BIGINT AUTO_INCREMENT NOT NULL,
    first_name VARCHAR(20) NOT NULL,
    last_name VARCHAR(20),
    PRIMARY KEY (person_id)
);