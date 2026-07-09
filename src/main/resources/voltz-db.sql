-- --------------------------------------------------------------------------
-- Optional cleanup (CAUTION: Deletes EVERYTHING if already present!)
-- Uncomment the lines below ONLY if you want to recreate everything from scratch.
-- --------------------------------------------------------------------------
/*
DROP TABLE transactions CASCADE CONSTRAINTS;
DROP TABLE wallet_entries CASCADE CONSTRAINTS;
DROP TABLE wallets CASCADE CONSTRAINTS;
DROP TABLE users CASCADE CONSTRAINTS;
DROP SEQUENCE transactions_seq;
DROP SEQUENCE wallet_entries_seq;
DROP SEQUENCE wallets_seq;
DROP SEQUENCE users_seq;
*/
-- --------------------------------------------------------------------------
-- Create SEQUENCES for Auto-Increment IDs
-- (Run this block FIRST)
-- --------------------------------------------------------------------------
CREATE SEQUENCE users_seq START WITH 1 INCREMENT BY 1 NOCACHE NOCYCLE;
CREATE SEQUENCE wallets_seq START WITH 1 INCREMENT BY 1 NOCACHE NOCYCLE;
CREATE SEQUENCE wallet_entries_seq START WITH 1 INCREMENT BY 1 NOCACHE NOCYCLE;
CREATE SEQUENCE transactions_seq START WITH 1 INCREMENT BY 1 NOCACHE NOCYCLE;

-- --------------------------------------------------------------------------
-- Create TABLES with Inline Constraints
-- (Run in order: users -> wallets -> wallet_entries -> transactions)
-- --------------------------------------------------------------------------

-- Table: users
CREATE TABLE users
(
    id           NUMBER(19) DEFAULT users_seq.NEXTVAL NOT NULL,
    user_name    VARCHAR2(255) NOT NULL,
    cpf_cnpj     VARCHAR2(14) NOT NULL,                   -- Digits only
    email        VARCHAR2(255) NOT NULL,
    phone_number VARCHAR2(20),
    password     VARCHAR2(60) NOT NULL,                   -- BCrypt hash
    active       CHAR(1)   DEFAULT 'S'          NOT NULL, -- 'S' or 'N'
    date_created TIMESTAMP DEFAULT SYSTIMESTAMP NOT NULL,
    date_updated TIMESTAMP DEFAULT SYSTIMESTAMP NOT NULL,
    -- Constraints (Inline)
    CONSTRAINT pk_users PRIMARY KEY (id),
    CONSTRAINT uk_users_email UNIQUE (email),
    CONSTRAINT uk_users_cpf_cnpj UNIQUE (cpf_cnpj),
    CONSTRAINT ck_users_active CHECK (users.active IN ('S', 'N'))
);
COMMENT
ON COLUMN users.password IS 'Password hash generated with BCrypt';
COMMENT
ON COLUMN users.active IS 'S=Active, N=Inactive';

-- Table: wallets
CREATE TABLE wallets
(
    id         NUMBER(19) DEFAULT wallets_seq.NEXTVAL NOT NULL,
    user_id    NUMBER(19) NOT NULL,
    created_at TIMESTAMP DEFAULT SYSTIMESTAMP NOT NULL,
    -- Constraints (Inline)
    CONSTRAINT pk_wallets PRIMARY KEY (id),
    CONSTRAINT fk_wallets_user FOREIGN KEY (user_id) REFERENCES users (id) -- Default ON DELETE is RESTRICT/NO ACTION
    -- If you want deleting the user to delete the wallet: ON DELETE CASCADE
);
COMMENT
ON TABLE wallets IS 'Represents a user''s main wallet';

-- Table: wallet_entries
CREATE TABLE wallet_entries
(
    id            NUMBER(19) DEFAULT wallet_entries_seq.NEXTVAL NOT NULL,
    wallet_id     NUMBER(19) NOT NULL,
    crypto_symbol VARCHAR2(10) NOT NULL,                                                                   -- Ex: BTC, ETH
    amount        NUMBER(38, 18) DEFAULT 0 NOT NULL,                                                       -- Balance for the specific crypto
    last_updated  TIMESTAMP DEFAULT SYSTIMESTAMP NOT NULL,
    -- Constraints (Inline)
    CONSTRAINT pk_wallet_entries PRIMARY KEY (id),
    CONSTRAINT fk_wallet_entries_wallet FOREIGN KEY (wallet_id) REFERENCES wallets (id) ON DELETE CASCADE, -- Deleting wallet removes balances
    CONSTRAINT uk_wallet_entries_symbol UNIQUE (wallet_id, crypto_symbol),                                 -- Ensures one balance per crypto/wallet
    CONSTRAINT ck_wallet_entries_amount CHECK (amount >= 0)                                                -- Non-negative balance
);
COMMENT
ON TABLE wallet_entries IS 'Stores the balance of each cryptocurrency per wallet';
COMMENT
ON COLUMN wallet_entries.amount IS 'Amount of cryptocurrency specified in crypto_symbol';

-- Table: transactions
CREATE TABLE transactions
(
    id                    NUMBER(19) DEFAULT transactions_seq.NEXTVAL NOT NULL,
    type                  VARCHAR2(20) NOT NULL,                                                                           -- DEPOSIT, WITHDRAWAL, TRANSFER
    source_wallet_id      NUMBER(19),                                                                                      -- NULL for DEPOSIT
    destination_wallet_id NUMBER(19),                                                                                      -- NULL for WITHDRAWAL
    crypto_symbol         VARCHAR2(10) NOT NULL,
    amount                NUMBER(38, 18) NOT NULL,
    transaction_date      TIMESTAMP DEFAULT SYSTIMESTAMP NOT NULL,
    status                VARCHAR2(20) NOT NULL,                                                                           -- PENDING, COMPLETED, FAILED
    -- Constraints (Inline)
    CONSTRAINT pk_transactions PRIMARY KEY (id),
    CONSTRAINT fk_transactions_source_wallet FOREIGN KEY (source_wallet_id) REFERENCES wallets (id) ON DELETE SET NULL,    -- Keep transaction if wallet is deleted
    CONSTRAINT fk_transactions_dest_wallet FOREIGN KEY (destination_wallet_id) REFERENCES wallets (id) ON DELETE SET NULL, -- Keep transaction if wallet is deleted
    CONSTRAINT ck_transactions_type CHECK (type IN ('DEPOSIT', 'WITHDRAWAL', 'TRANSFER')),
    CONSTRAINT ck_transactions_status CHECK (status IN ('PENDING', 'COMPLETED', 'FAILED')),
    CONSTRAINT ck_transactions_amount CHECK (amount > 0)                                                                   -- Transaction amount must be positive
);
COMMENT
ON TABLE transactions IS 'Cryptocurrency movement history';
COMMENT
ON COLUMN transactions.type IS 'Transaction type: DEPOSIT, WITHDRAWAL, TRANSFER';
COMMENT
ON COLUMN transactions.status IS 'Transaction status: PENDING, COMPLETED, FAILED';

-- --------------------------------------------------------------------------
-- Populate tables with initial data (Examples)
-- (Run AFTER creating the tables)
-- --------------------------------------------------------------------------

-- Creating User 1 (Individual)
-- NOTE: The password '1234' will be hashed by the DAO when using usersDao.save()
-- To insert directly in SQL, you would need to generate the BCrypt hash first.
-- We will insert without the password for now, or with a placeholder hash if the column allowed NULL (ours does not).
-- To populate via SQL, you would need to generate the hash externally or temporarily relax the NOT NULL constraint.
-- Assuming the DAO will perform the initial insert:
-- INSERT INTO users (id, user_name, cpf_cnpj, email, phone_number, password, active) VALUES (users_seq.NEXTVAL, 'Bianca PF', '11122233344', 'bianca.pf@mail.com', '1199998888', '$2a$10$placeholderhashparainsertdireto', 'S');
-- We will skip the direct users insert because of the hash. It is assumed the app will do it.

-- Assuming the user with ID 1 was created by the app:
-- Creating the wallet for user 1
INSERT INTO wallets (user_id)
VALUES (1);
-- Assuming Bianca''s user ID is 1

-- Adding balances (Wallet Entries) for Wallet 1 (Assuming the generated wallet ID is 1)
INSERT INTO wallet_entries (wallet_id, crypto_symbol, amount)
VALUES (1, 'BTC', 0.5);
INSERT INTO wallet_entries (wallet_id, crypto_symbol, amount)
VALUES (1, 'ETH', 2.12345);

-- Creating a simulated deposit transaction for Wallet 1
INSERT INTO transactions (type, destination_wallet_id, crypto_symbol, amount, status)
VALUES ('DEPOSIT', 1, 'BTC', 0.5, 'COMPLETED');

-- Creating User 2 (Corporate)
-- (Again, ideally created via App/DAO to hash the password)
-- INSERT INTO users (id, user_name, cpf_cnpj, email, phone_number, password, active) VALUES (users_seq.NEXTVAL, 'Voltz Inc', '11222333000144', 'contact@voltz.com', '1133334444', '$2a$10$placeholderhashparainsertdireto', 'S');

-- Assuming the user with ID 2 was created:
-- Creating the wallet for user 2
INSERT INTO wallets (user_id)
VALUES (2);
-- Assuming this is the wallet ID for the corporate user

-- Adding balance for Wallet 2 (Assuming wallet ID 2)
INSERT INTO wallet_entries (wallet_id, crypto_symbol, amount)
VALUES (2, 'USDC', 10000.00);
-- Stablecoin example

-- --------------------------------------------------------------------------
-- Sample Queries (SELECT)
-- --------------------------------------------------------------------------

-- View all users
SELECT id, user_name, email, cpf_cnpj, active
FROM users;

-- View the wallet of a specific user (e.g. user_id = 1)
SELECT id, user_id, created_at
FROM wallets
WHERE user_id = 1;

-- View balances for a specific wallet (e.g. wallet_id = 1)
SELECT crypto_symbol, amount, last_updated
FROM wallet_entries
WHERE wallet_id = 1;

-- View transactions for a specific wallet (e.g. wallet_id = 1)
SELECT *
FROM transactions
WHERE source_wallet_id = 1
   OR destination_wallet_id = 1
ORDER BY transaction_date DESC;

-- --------------------------------------------------------------------------
-- Sample Updates (UPDATE)
-- --------------------------------------------------------------------------

-- Update the phone number of the user with id 1 (DAO would do this and update date_updated)
UPDATE users
SET phone_number = '11987654321',
    date_updated = SYSTIMESTAMP
WHERE id = 1;

-- Yesulate adding balance via UPDATE (DAO would do this and update last_updated)
UPDATE wallet_entries
SET amount       = amount + 1.5,
    last_updated = SYSTIMESTAMP
WHERE wallet_id = 1
  AND crypto_symbol = 'ETH';

-- --------------------------------------------------------------------------
-- DELETE example - Use CAUTION!
-- --------------------------------------------------------------------------

-- Delete a specific transaction (e.g. id = 1)
-- DELETE FROM transactions WHERE id = 1;

-- Delete a specific balance (e.g. ETH from wallet 1)
-- DELETE FROM wallet_entries WHERE wallet_id = 1 AND crypto_symbol = 'ETH';

-- --------------------------------------------------------------------------
-- Manual Transaction Control
-- Execute após um bloco de operações bem-sucedidas
-- --------------------------------------------------------------------------

COMMIT;

-- --------------------------------------------------------------------------
-- Manual Reversal
-- Execute if an error occurs during a block of manual operations
-- --------------------------------------------------------------------------

-- ROLLBACK;

-- ==========================================================================
-- End of Script
-- ==========================================================================