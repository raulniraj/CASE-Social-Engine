-- CASE Social Engine Database Schema

-- 1. Users Table
CREATE TABLE app_users (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    role VARCHAR(50) DEFAULT 'USER',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 2. Posts Table (The Core)
CREATE TABLE posts (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    user_id BIGINT NOT NULL,
    content TEXT NOT NULL,
    platform VARCHAR(50) NOT NULL, -- 'TWITTER', 'LINKEDIN'
    scheduled_time TIMESTAMP NOT NULL,
    status VARCHAR(50) DEFAULT 'PENDING', -- 'PENDING', 'PUBLISHED', 'BLOCKED'
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_user FOREIGN KEY (user_id) REFERENCES app_users(id)
);

-- 3. Rules Table (The "Smart" Logic)
CREATE TABLE post_rules (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    post_id BIGINT NOT NULL,
    rule_type VARCHAR(50) NOT NULL, -- 'WEATHER', 'SENTIMENT'
    condition_value VARCHAR(255) NOT NULL, -- e.g., 'RAIN', 'NEGATIVE'
    action VARCHAR(50) NOT NULL, -- 'BLOCK', 'SWAP'
    CONSTRAINT fk_post_rule FOREIGN KEY (post_id) REFERENCES posts(id) ON DELETE CASCADE
);

-- 4. Logs Table (Audit Trail)
CREATE TABLE post_logs (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    post_id BIGINT NOT NULL,
    execution_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    status VARCHAR(50) NOT NULL,
    message TEXT,
    CONSTRAINT fk_post_log FOREIGN KEY (post_id) REFERENCES posts(id)
);