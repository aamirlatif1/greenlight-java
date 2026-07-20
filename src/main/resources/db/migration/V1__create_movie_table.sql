CREATE TABLE movie (
    id      bigserial PRIMARY KEY,
    title   text NOT NULL,
    year    integer NOT NULL,
    runtime text NOT NULL,
    genres  text[] NOT NULL DEFAULT '{}',
    version integer NOT NULL DEFAULT 1
);
