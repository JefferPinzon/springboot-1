CREATE TABLE productos
(
    id     BIGINT AUTO_INCREMENT NOT NULL,
    nombre VARCHAR(255) NOT NULL,
    precio DOUBLE NOT NULL,
    CONSTRAINT pk_productos PRIMARY KEY (id)
);