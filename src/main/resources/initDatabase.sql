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
        ('TEMPERATURE', 'active', null),--id=1
        ('TEMPERATURE', 'active', null),
        ('HUMIDITY', 'active', null),
        ('HUMIDITY', 'active', null),
        ('PRESSURE', 'active', null),
        ('PRESSURE', 'active', null),
        ('WIND', 'active', null),
        ('WIND', 'active', null),
        ('TEMPERATURE', 'deactivated', null),
        ('HUMIDITY', 'deactivated', null),
        ('PRESSURE', 'deactivated', null),
        ('WIND', 'deactivated', null);--id=12 ATTENZIONE: nel testing questo valore conta