drop table if exists bookings;
drop table if exists comments;
drop table if exists items;
drop table if exists item_requests;
drop table if exists users;

create table if not exists users
(
    id
    BIGINT
    GENERATED
    ALWAYS AS
    IDENTITY
    primary
    key,
    email
    CHARACTER
    VARYING
(
    200
) not null unique,
    name CHARACTER VARYING
(
    50
)
    );


create table if not exists item_requests
(
    id
    BIGINT
    GENERATED
    ALWAYS AS
    IDENTITY
    primary
    key,
    owner_id
    bigint
    not
    null
    constraint
    owner
    references
    users,
    description
    varchar
(
    200
) not null,
    create_date_time timestamp without time zone not null
    );


create table if not exists items
(
    id
    BIGINT
    GENERATED
    ALWAYS AS
    IDENTITY
    primary
    key,
    name
    varchar
(
    50
) not null,
    description varchar
(
    200
) not null,
    available boolean not null,
    owner_id bigint not null,
    item_request_id bigint
    constraint item_request
    references item_requests
    );


create table if not exists bookings
(
    id
    BIGINT
    GENERATED
    ALWAYS AS
    IDENTITY
    primary
    key,
    start_date_time
    timestamp
    not
    null,
    status
    varchar
(
    20
) not null,
    item_id bigint not null
    constraint "bookings_itemId_fkey"
    references items,
    booker_id bigint not null
    constraint "bookings_bookerId_fkey"
    references users,
    end_date_time timestamp not null
    );

create table if not exists comments
(
    id
    BIGINT
    GENERATED
    ALWAYS AS
    IDENTITY
    primary
    key,
    text
    varchar
(
    500
) not null,
    item_id bigint not null
    constraint "comments_itemId_fkey"
    references items
    on update cascade
    on delete cascade,
    author_id bigint not null
    constraint "comments_authorId_fkey"
    references users
    on update cascade
    on delete cascade,
    created timestamp not null
    );


