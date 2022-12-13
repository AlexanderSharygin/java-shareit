create table if not exists users
(
    id    bigint generated always as identity
        primary key,
    name  varchar(50),
    email varchar(200)
        unique
);

alter table if exists users
    owner to postgres;

create table if not exists item_requests
(
    id               bigint generated always as identity
        primary key,
    owner_id         bigint                        not null
        constraint owner
            references users,
    description      varchar(200)                  not null,
    create_date_time timestamp without time zone[] not null
);

alter table if exists item_requests
    owner to postgres;

create table if not exists items
(
    id              bigint generated always as identity
        primary key,
    name            varchar(50)  not null,
    description     varchar(200) not null,
    available       boolean      not null,
    owner_id        bigint       not null
        constraint owner
            references users
            on update cascade on delete cascade,
    item_request_id bigint
        constraint item_request
            references item_requests
);

alter table if exists items
    owner to postgres;

create table if not exists bookings
(
    id              bigint generated always as identity
        primary key,
    start_date_time timestamp   not null,
    status          varchar(20) not null,
    item_id         bigint      not null
        constraint "bookings_itemId_fkey"
            references items,
    booker_id       bigint      not null
        constraint "bookings_bookerId_fkey"
            references users,
    end_date_time   timestamp   not null
);

alter table if exists bookings
    owner to postgres;

create table if not exists comments
(
    id        bigint generated always as identity
        primary key,
    text      varchar(500) not null,
    item_id   bigint       not null
        constraint "comments_itemId_fkey"
            references items
            on update cascade on delete cascade,
    author_id bigint       not null
        constraint "comments_authorId_fkey"
            references users
            on update cascade on delete cascade,
    created   timestamp    not null
);

alter table if exists comments
    owner to postgres;

