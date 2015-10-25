# --- !Ups
CREATE TABLE CAR_ADVERT (
    id varchar PRIMARY KEY,
    title varchar NOT NULL,
    fuel varchar NOT NULL,
    price varchar NOT NULL,
    new boolean NOT NULL,
    mileage numeric,
    first_registration varchar
);

# --- !Downs
DROP TABLE CAR_ADVERT;
