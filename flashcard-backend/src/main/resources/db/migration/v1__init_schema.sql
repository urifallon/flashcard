-- USERS
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password_hash TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT now(),
    updated_at TIMESTAMP DEFAULT now()
);

-- FOLDERS
CREATE TABLE folders (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT REFERENCES users(id),
    name VARCHAR(100) NOT NULL,
    description TEXT,
    created_at TIMESTAMP DEFAULT now(),
    updated_at TIMESTAMP DEFAULT now()
);

-- FLASHCARDS
CREATE TABLE flashcards (
    id BIGSERIAL PRIMARY KEY,
    folder_id BIGINT REFERENCES folders(id),
    front TEXT NOT NULL,
    back TEXT NOT NULL,
    media JSONB,
    difficulty SMALLINT DEFAULT 3,
    quality SMALLINT DEFAULT 0,
    created_at TIMESTAMP DEFAULT now(),
    updated_at TIMESTAMP DEFAULT now()
);

-- TAGS
CREATE TABLE tags (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(50) UNIQUE NOT NULL
);

-- FLASHCARD_TAGS
CREATE TABLE flashcard_tags (
    flashcard_id BIGINT REFERENCES flashcards(id),
    tag_id BIGINT REFERENCES tags(id),
    PRIMARY KEY (flashcard_id, tag_id)
);

-- USER_FLASHCARD_STATS
CREATE TABLE user_flashcard_stats (
    user_id BIGINT REFERENCES users(id),
    flashcard_id BIGINT REFERENCES flashcards(id),
    times_studied INT DEFAULT 0,
    times_correct INT DEFAULT 0,
    times_wrong INT DEFAULT 0,
    last_studied TIMESTAMP,
    score INT DEFAULT 0,
    PRIMARY KEY (user_id, flashcard_id)
);

-- USER_FOLDER_STATS
CREATE TABLE user_folder_stats (
    user_id BIGINT REFERENCES users(id),
    folder_id BIGINT REFERENCES folders(id),
    times_studied INT DEFAULT 0,
    total_score INT DEFAULT 0,
    last_studied TIMESTAMP,
    PRIMARY KEY (user_id, folder_id)
);

-- STUDY_SESSION_RUNS
CREATE TABLE study_session_runs (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT REFERENCES users(id),
    folder_id BIGINT REFERENCES folders(id),
    start_time TIMESTAMP DEFAULT now(),
    end_time TIMESTAMP,
    mode VARCHAR(20)
);

-- STUDY_SESSION_ITEMS
CREATE TABLE study_session_items (
    id BIGSERIAL PRIMARY KEY,
    session_run_id BIGINT REFERENCES study_session_runs(id),
    flashcard_id BIGINT REFERENCES flashcards(id),
    result BOOLEAN,
    score_delta INT,
    studied_at TIMESTAMP DEFAULT now()
);

-- AUDIT_LOGS
CREATE TABLE audit_logs (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT REFERENCES users(id),
    action VARCHAR(100) NOT NULL,
    metadata JSONB,
    created_at TIMESTAMP DEFAULT now()
);
