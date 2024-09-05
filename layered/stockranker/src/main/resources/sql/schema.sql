DROP TABLE IF EXISTS STOCK;
DROP TABLE IF EXISTS RANK;

CREATE TABLE STOCK
(
    id                   BIGINT AUTO_INCREMENT PRIMARY KEY,
    code                 VARCHAR(20)           NOT NULL UNIQUE,
    name                 VARCHAR(50)           NOT NULL,
    price                DECIMAL(19, 2)        NOT NULL,
    previous_close_price DECIMAL(19, 2)        NOT NULL,
    price_change_ratio   DECIMAL(19, 2)        NOT NULL,
    volume               BIGINT                NOT NULL,
    views                BIGINT                NOT NULL,
    created_at           DATETIME(6)           NOT NULL,
    updated_at           DATETIME(6)           NOT NULL
);

CREATE TABLE RANK
(
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    tag        VARCHAR(10)           NOT NULL,
    rank       INT                   NOT NULL,
    stock_id   BIGINT                NOT NULL,
    created_at DATETIME(6)           NOT NULL,
    updated_at DATETIME(6)           NOT NULL
);

CREATE INDEX idx_rank_tag_rank ON RANK (tag, rank);
