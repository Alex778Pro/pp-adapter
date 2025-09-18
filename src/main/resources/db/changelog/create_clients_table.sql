CREATE TABLE clients
(
    id SERIAL PRIMARY KEY,
    FullName VARCHAR(255),
    phone VARCHAR(255),
    birthday DATE,
    messageSend BOOLEAN
);
