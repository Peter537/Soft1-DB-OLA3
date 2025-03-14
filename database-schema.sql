CREATE TABLE players (
    player_id SERIAL PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    ranking INT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE tournaments (
    tournament_id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    game VARCHAR(50) NOT NULL,
    max_players INT NOT NULL,
    start_date TIMESTAMP NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE tournament_registrations (
    registration_id SERIAL PRIMARY KEY,
    tournament_id INT NOT NULL,
    player_id INT NOT NULL,
    registered_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (tournament_id) REFERENCES tournaments(tournament_id) ON DELETE CASCADE,
    FOREIGN KEY (player_id) REFERENCES players(player_id) ON DELETE CASCADE
);

CREATE TABLE matches (
    match_id SERIAL PRIMARY KEY,
    tournament_id INT NOT NULL,
    player1_id INT NOT NULL,
    player2_id INT NOT NULL,
    winner_id INT NULL,
    match_date TIMESTAMP NOT NULL,
    FOREIGN KEY (tournament_id) REFERENCES tournaments(tournament_id) ON DELETE CASCADE,
    FOREIGN KEY (player1_id) REFERENCES players(player_id) ON DELETE CASCADE,
    FOREIGN KEY (player2_id) REFERENCES players(player_id) ON DELETE CASCADE,
    FOREIGN KEY (winner_id) REFERENCES players(player_id) ON DELETE SET NULL
);

-- Exercise 1
ALTER TABLE tournaments ADD COLUMN version INT NOT NULL DEFAULT 1;

-- Exercise 4
CREATE OR REPLACE PROCEDURE update_ranking(playerID INT)
LANGUAGE plpgsql
AS $$
BEGIN
    UPDATE players SET ranking = ranking + 10 WHERE player_id = playerID;
END;
$$;

-- Default data til exercises
INSERT INTO players (username, email, ranking)
VALUES
    ('odo-gaming', 'odo@gmail.com', 100),
    ('stylized-ace', 'ace@gmail.com', 200),
    ('player3', 'player3@gmail.com', 150),
    ('player4', 'player4@gmail.com', 120),
    ('player5', 'player5@gmail.com', 180),
    ('player6', 'player6@gmail.com', 170),
    ('player7', 'player7@gmail.com', 160),
    ('player8', 'player8@gmail.com', 140),
    ('player9', 'player9@gmail.com', 130),
    ('player10', 'player10@gmail.com', 110);

INSERT INTO tournaments (name, game, max_players, start_date)
VALUES
    ('Tournament 1', 'CS2', 4, NOW() + INTERVAL '7 days'),
    ('Tournament 2', 'CS2', 4, NOW() + INTERVAL '14 days');

INSERT INTO tournament_registrations (tournament_id, player_id)
VALUES
    (1, 1),
    (1, 2),
    (1, 3),
    (1, 4);

INSERT INTO matches (tournament_id, player1_id, player2_id, winner_id, match_date)
VALUES
    (1, 1, 3, 1, NOW() + INTERVAL '8 days'),
    (1, 2, 4, 2, NOW() + INTERVAL '8 days'),
    (1, 1, 2, NULL, NOW() + INTERVAL '9 days');
