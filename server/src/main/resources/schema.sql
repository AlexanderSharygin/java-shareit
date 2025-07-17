CREATE TABLE IF NOT EXISTS users
(
    id
    BIGINT
    GENERATED
    BY
    DEFAULT AS
    IDENTITY
    NOT
    NULL,
    name
    VARCHAR
(
    255
) NOT NULL,
    email VARCHAR
(
    512
) NOT NULL,
    CONSTRAINT pk_user PRIMARY KEY
(
    id
),
    CONSTRAINT UQ_USER_EMAIL UNIQUE
(
    email
)
    );

CREATE TABLE IF NOT EXISTS item_requests
(
    id
    BIGINT
    GENERATED
    BY
    DEFAULT AS
    IDENTITY
    NOT
    NULL,
    description
    VARCHAR
(
    200
),
    owner_id INTEGER NOT NULL REFERENCES users
(
    id
),
    create_date_time TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    CONSTRAINT pk_item_requests PRIMARY KEY
(
    id
)
    );

CREATE TABLE IF NOT EXISTS items
(
    id
    BIGINT
    GENERATED
    BY
    DEFAULT AS
    IDENTITY
    NOT
    NULL,
    name
    VARCHAR
(
    255
) NOT NULL,
    description VARCHAR
(
    255
),
    is_available BOOLEAN NOT NULL DEFAULT TRUE,
    owner_id BIGINT NOT NULL,
    request_id BIGINT,
    CONSTRAINT pk_item PRIMARY KEY
(
    id
),
    CONSTRAINT fk_items_owner FOREIGN KEY
(
    owner_id
) REFERENCES users
(
    id
),
    CONSTRAINT fk_items_request FOREIGN KEY
(
    request_id
) REFERENCES item_requests
(
    id
)
    );

CREATE TABLE IF NOT EXISTS bookings
(
    id
    BIGINT
    GENERATED
    BY
    DEFAULT AS
    IDENTITY
    NOT
    NULL,
    create_date_time
    TIMESTAMP
    WITHOUT
    TIME
    ZONE
    NOT
    NULL,
    end_date_time
    TIMESTAMP
    WITHOUT
    TIME
    ZONE
    NOT
    NULL,
    status
    VARCHAR
(
    255
) NOT NULL,
    item_id BIGINT NOT NULL,
    booker_id BIGINT NOT NULL,
    CONSTRAINT pk_booking PRIMARY KEY
(
    id
),
    CONSTRAINT fk_booking_item FOREIGN KEY
(
    item_id
) REFERENCES items
(
    id
),
    CONSTRAINT fk_booking_booker FOREIGN KEY
(
    booker_id
) REFERENCES users
(
    id
)
    );

CREATE TABLE IF NOT EXISTS comments
(
    id
    BIGINT
    GENERATED
    BY
    DEFAULT AS
    IDENTITY
    NOT
    NULL,
    text
    VARCHAR
(
    255
) NOT NULL,
    item_id BIGINT NOT NULL,
    author_id BIGINT NOT NULL,
    created_time TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    CONSTRAINT pk_comment PRIMARY KEY
(
    id
),
    CONSTRAINT fk_comment_item FOREIGN KEY
(
    item_id
) REFERENCES items
(
    id
),
    CONSTRAINT fk_comment_author FOREIGN KEY
(
    author_id
) REFERENCES users
(
    id
)
    );