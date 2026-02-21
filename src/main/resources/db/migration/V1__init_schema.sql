-- 1. Create Tables
CREATE TABLE IF NOT EXISTS users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    external_id VARCHAR(100) NOT NULL UNIQUE,
    name VARCHAR(100) NOT NULL,
    system_account BOOLEAN NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS assets (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    code VARCHAR(50) NOT NULL UNIQUE,
    name VARCHAR(100) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS wallets (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    asset_id BIGINT NOT NULL,
    balance DECIMAL(19,4) NOT NULL DEFAULT 0,
    version BIGINT DEFAULT 0,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_wallet_user FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT fk_wallet_asset FOREIGN KEY (asset_id) REFERENCES assets(id),
    CONSTRAINT uk_user_asset UNIQUE (user_id, asset_id)
);

CREATE TABLE IF NOT EXISTS transactions (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    reference_id VARCHAR(100) NOT NULL UNIQUE,
    type VARCHAR(20) NOT NULL,
    status VARCHAR(20) NOT NULL,
    amount DECIMAL(19,4) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS ledger_entries (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    wallet_id BIGINT NOT NULL,
    transaction_id BIGINT NOT NULL,
    type VARCHAR(20) NOT NULL, -- DEBIT/CREDIT
    amount DECIMAL(19,4) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_ledger_wallet FOREIGN KEY (wallet_id) REFERENCES wallets(id),
    CONSTRAINT fk_ledger_txn FOREIGN KEY (transaction_id) REFERENCES transactions(id)
);

-- 2. Seed Data
INSERT IGNORE INTO assets (id, code, name) VALUES (1, 'GOLD', 'Gold Coins'), (2, 'POINT', 'Reward Points');

INSERT IGNORE INTO users (id, external_id, name, system_account) VALUES
(1, 'SYSTEM_TREASURY', 'Treasury Account', true),
(2, 'USER_1', 'Alice', false),
(3, 'USER_2', 'Bob', false);

INSERT IGNORE INTO wallets (user_id, asset_id, balance, version) VALUES
(1, 1, 1000000, 0),
(2, 1, 100, 0),
(3, 1, 50, 0);