create table if not exists application (
    -- bigserial allows us to easily generate PKs, but it is a postgres-only feature
    id bigserial not null primary key,
    email varchar(50) not null,
    -- storing enums by their ordinal value for efficiency. drawback: cannot reorder the enum values in Java
    -- without breaking existing rows.
    saluation int,
    first_name varchar(50) not null,
    last_name varchar(50) not null,
    number_of_persons int,
    -- if we needed portability with another dbms, we should use sth. like tinyint here
    wbs_present boolean default false,
    earliest_move_in_date timestamptz,
    pets boolean default false,
    -- enum
    status int not null,
    applicant_comment varchar(1000),
    user_comment varchar(1000),
    creation_timestamp timestamptz,
    -- enum
    creation_source int not null,
    property_id bigint not null
);