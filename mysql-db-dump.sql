
-- Users Table
CREATE TABLE users (
    id CHAR(36) PRIMARY KEY,
    country VARCHAR(50) NOT NULL,
    level INT NOT NULL,
    coins INT NOT NULL
);

-- Tournaments Table
CREATE TABLE tournaments (
    id CHAR(36) PRIMARY KEY,
    start_time DATETIME NOT NULL,
    end_time DATETIME NOT NULL,
    status VARCHAR(20) NOT NULL CHECK (status IN ('ACTIVE', 'FINISHED')),
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- Tournament Participation Table
CREATE TABLE tournament_participation (
                                          id CHAR(36) PRIMARY KEY,
                                          user_id CHAR(36) NOT NULL,
                                          tournament_id CHAR(36) NOT NULL,
                                          group_id CHAR(36) NOT NULL,
                                          score INT DEFAULT 0,
                                          reward_eligible INT DEFAULT 0,
                                          FOREIGN KEY (user_id) REFERENCES users(id),
                                          FOREIGN KEY (tournament_id) REFERENCES tournaments(id)
);


-- Indexes for Optimization
CREATE INDEX idx_user_country ON users (country);
CREATE INDEX idx_tournament_user ON tournament_participation (user_id);
CREATE INDEX idx_group_user ON tournament_participation (group_id);
CREATE INDEX idx_tournament ON tournament_participation (tournament_id);
