CREATE TABLE session_player
(
    SessionID   CHAR(32)    NOT NULL, /* A random string of 32 characters should do the trick. */
    PlayerID    INT         NOT NULL,
    ExpireDate  TIMESTAMP   NOT NULL,

    UNIQUE (SessionID),
    PRIMARY KEY (SessionID),
    FOREIGN KEY (PlayerID) REFERENCES players(PlayerID)
);