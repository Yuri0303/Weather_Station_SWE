CREATE TABLE IF NOT EXISTS "User"
(
    id        SERIAL PRIMARY KEY,
    firstName VARCHAR(50) NOT NULL,
    lastName  VARCHAR(50) NOT NULL,
    email     VARCHAR(50) NOT NULL UNIQUE,
    password  VARCHAR(255) NOT NULL ,
    isBlocked BOOLEAN NOT NULL
);

CREATE TABLE IF NOT EXISTS "Admin"
(
    id        SERIAL PRIMARY KEY,
    firstName VARCHAR(50) NOT NULL,
    lastName  VARCHAR(50) NOT NULL,
    email     VARCHAR(50) NOT NULL UNIQUE,
    password  VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS "Maintainer"
(
    id        SERIAL PRIMARY KEY,
    firstName VARCHAR(50) NOT NULL,
    lastName  VARCHAR(50) NOT NULL,
    email     VARCHAR(50) NOT NULL UNIQUE,
    password  VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS "Notification"
(
    id       SERIAL PRIMARY KEY,
    message  VARCHAR(255) NOT NULL,
    isRead   BOOLEAN      NOT NULL,
    userId   INT          NOT NULL,
    dateTime TIMESTAMP    NOT NULL,
    FOREIGN KEY (userId) REFERENCES "User" (id)
);

CREATE TABLE IF NOT EXISTS "AlertRule"
(
    id         SERIAL PRIMARY KEY,
    sensorType VARCHAR(15) NOT NULL,
    lowerBound FLOAT,
    upperBound FLOAT,
    userId INT NOT NULL,
    FOREIGN KEY (userId) REFERENCES "User" (id),
    CONSTRAINT atLeastOneBound CHECK ( lowerBound IS NOT NULL OR upperBound IS NOT NULL ),
    CONSTRAINT rightBoundOrder CHECK ( lowerBound < upperBound )
);

CREATE TABLE IF NOT EXISTS "Measurement"
(
    id       SERIAL PRIMARY KEY,
    value    FLOAT NOT NULL,
    dateTime TIMESTAMP NOT NULL,
    sensorId INT NOT NULL
);

CREATE TABLE IF NOT EXISTS "Sensor"
(
    id                SERIAL PRIMARY KEY,
    sensorType        VARCHAR(15) NOT NULL,
    sensorState       VARCHAR(15) NOT NULL,
    lastMeasurementId INT UNIQUE,
    FOREIGN KEY (lastMeasurementId) REFERENCES "Measurement" (id)
);

CREATE TABLE IF NOT EXISTS "Ticket"
(
    id            SERIAL PRIMARY KEY,
    isOpen        BOOLEAN NOT NULL,
    closeDateTime TIMESTAMP,
    isTaken       BOOLEAN NOT NULL,
    maintainerId  INT,
    sensorId      INT NOT NULL,
    FOREIGN KEY (maintainerId) REFERENCES "Maintainer" (id),
    FOREIGN KEY (sensorId) REFERENCES "Sensor" (id)
);

CREATE UNIQUE INDEX unique_open_ticket_per_sensor --per un sensor id ci può essere un solo ticket aperto
ON "Ticket" (sensorId)
WHERE (isOpen = TRUE);

ALTER TABLE "Measurement"
ADD CONSTRAINT fk_sensorId
FOREIGN KEY (sensorId) REFERENCES "Sensor" (id);