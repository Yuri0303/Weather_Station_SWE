INSERT INTO "Admin" (firstName, lastName, email, password)
    VALUES
        ('Giulio', 'Nencini', 'giulionencini@test.it', '123'),
        ('Yuri', 'Bartoletti', 'yuribartoletti@test.it', '123');

INSERT INTO "Maintainer" (firstName, lastName, email, password)
    VALUES
        ('Damiano', 'Nencini', 'damianonencini@test.it', '123'),
        ('Dario', 'Bartoletti','dariobartoletti@test.it', '123');

INSERT INTO "User" (firstName, lastName, email, password, isBlocked)
    VALUES
        ('Samuele', 'Zanieri', 'samuelezanieri@test.it', '123', true),
        ('Roberto', 'Chiesi', 'robertochiesi@test.it', '123', false),
        ('Gianluca', 'Taddei', 'gianlucataddei@test.it', '123', false),
        ('Riccardo', 'Cappellini', 'riccardocappellini@test.it', '123', false),
        ('Sasha', 'Bartoletti', 'sashabartoletti@test.it', '123', true);

INSERT INTO "AlertRule" (sensorType, lowerBound, upperBound, userId)
    VALUES
        ('TEMPERATURE', 10, 40, 1),
        ('HUMIDITY', 35, 95, 1),
        ('WIND', null, 65, 2),
        ('HUMIDITY', null, 85, 3),
        ('PRESSURE', 900, 1000, 4),
        ('TEMPERATURE', 15, null, 5);--ATTENZIONE: id=6 nel testing questo valore conta


INSERT INTO "Sensor" (sensorType, sensorState, lastMeasurementId)
    VALUES
        ('TEMPERATURE', 'ACTIVE', null),--id=1
        ('TEMPERATURE', 'ACTIVE', null),
        ('HUMIDITY', 'ACTIVE', null),
        ('HUMIDITY', 'ACTIVE', null),
        ('PRESSURE', 'ACTIVE', null),
        ('PRESSURE', 'ACTIVE', null),
        ('WIND', 'ACTIVE', null),
        ('WIND', 'ACTIVE', null),
        ('TEMPERATURE', 'DEACTIVATED', null),
        ('HUMIDITY', 'DEACTIVATED', null),
        ('PRESSURE', 'DEACTIVATED', null),
        ('WIND', 'DEACTIVATED', null);--id=12 ATTENZIONE: nel testing questo valore conta

INSERT INTO "Notification" (message, isRead, userId, datetime)
    VALUES
        ('messaggio di prova 1', true, 2, '2026-06-01 00:00:00'),
        ('messaggio di prova 2', true, 2, '2026-06-01 00:00:03');

INSERT INTO "Measurement" (value, datetime, sensorId)
    VALUES
        (33, '2026-06-01 00:00:00', 1),
        (81, '2026-06-01 00:00:00', 3),
        (1050, '2026-06-01 00:00:00', 5),
        (35, '2026-06-01 00:00:00', 7);