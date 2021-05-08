CREATE TABLE players
(
    PlayerID INT AUTO_INCREMENT NOT NULL,
    Username VARCHAR(255),

    UNIQUE (PlayerID),
    PRIMARY KEY (PlayerID)
);
