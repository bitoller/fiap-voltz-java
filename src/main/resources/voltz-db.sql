-- ==========================================================================
-- Script SQL para Voltz Crypto Bank (Oracle SQL)
-- Baseado na estrutura definida: Users(CPF/CNPJ), Wallet, WalletEntry, Transaction
-- ==========================================================================

-- --------------------------------------------------------------------------
-- Limpeza Opcional (CUIDADO: Apaga TUDO se já existir!)
-- Descomente as linhas abaixo APENAS se quiser recriar tudo do zero.
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


-- --------------------------------------------------------------------------
-- Criação das SEQUENCES para IDs Auto-Increment
-- (Execute este bloco PRIMEIRO)
-- --------------------------------------------------------------------------
CREATE SEQUENCE users_seq START WITH 1 INCREMENT BY 1 NOCACHE NOCYCLE;
CREATE SEQUENCE wallets_seq START WITH 1 INCREMENT BY 1 NOCACHE NOCYCLE;
CREATE SEQUENCE wallet_entries_seq START WITH 1 INCREMENT BY 1 NOCACHE NOCYCLE;
CREATE SEQUENCE transactions_seq START WITH 1 INCREMENT BY 1 NOCACHE NOCYCLE;

-- --------------------------------------------------------------------------
-- Criação das TABELAS com Constraints Inline
-- (Execute na ordem: users -> wallets -> wallet_entries -> transactions)
-- --------------------------------------------------------------------------

-- Tabela: users
CREATE TABLE users
(
    id           NUMBER(19) DEFAULT users_seq.NEXTVAL NOT NULL,
    user_name    VARCHAR2(255) NOT NULL,
    cpf_cnpj     VARCHAR2(14) NOT NULL,                   -- Apenas dígitos
    email        VARCHAR2(255) NOT NULL,
    phone_number VARCHAR2(20),
    password     VARCHAR2(60) NOT NULL,                   -- Hash BCrypt
    active       CHAR(1)   DEFAULT 'S'          NOT NULL, -- 'S' ou 'N'
    date_created TIMESTAMP DEFAULT SYSTIMESTAMP NOT NULL,
    date_updated TIMESTAMP DEFAULT SYSTIMESTAMP NOT NULL,
    -- Constraints (Inline)
    CONSTRAINT pk_users PRIMARY KEY (id),
    CONSTRAINT uk_users_email UNIQUE (email),
    CONSTRAINT uk_users_cpf_cnpj UNIQUE (cpf_cnpj),
    CONSTRAINT ck_users_active CHECK (users.active IN ('S', 'N'))
);
COMMENT
ON COLUMN users.password IS 'Hash da senha gerado com BCrypt';
COMMENT
ON COLUMN users.active IS 'S=Ativo, N=Inativo';

-- Tabela: wallets
CREATE TABLE wallets
(
    id         NUMBER(19) DEFAULT wallets_seq.NEXTVAL NOT NULL,
    user_id    NUMBER(19) NOT NULL,
    created_at TIMESTAMP DEFAULT SYSTIMESTAMP NOT NULL,
    -- Constraints (Inline)
    CONSTRAINT pk_wallets PRIMARY KEY (id),
    CONSTRAINT fk_wallets_user FOREIGN KEY (user_id) REFERENCES users (id) -- Default ON DELETE é RESTRICT/NO ACTION
    -- Se quiser que deletar o usuário delete a carteira: ON DELETE CASCADE
);
COMMENT
ON TABLE wallets IS 'Representa a carteira principal de um usuário';

-- Tabela: wallet_entries
CREATE TABLE wallet_entries
(
    id            NUMBER(19) DEFAULT wallet_entries_seq.NEXTVAL NOT NULL,
    wallet_id     NUMBER(19) NOT NULL,
    crypto_symbol VARCHAR2(10) NOT NULL,                                                                   -- Ex: BTC, ETH
    amount        NUMBER(38, 18) DEFAULT 0 NOT NULL,                                                       -- Saldo da cripto específica
    last_updated  TIMESTAMP DEFAULT SYSTIMESTAMP NOT NULL,
    -- Constraints (Inline)
    CONSTRAINT pk_wallet_entries PRIMARY KEY (id),
    CONSTRAINT fk_wallet_entries_wallet FOREIGN KEY (wallet_id) REFERENCES wallets (id) ON DELETE CASCADE, -- Deletar carteira remove saldos
    CONSTRAINT uk_wallet_entries_symbol UNIQUE (wallet_id, crypto_symbol),                                 -- Garante um saldo por cripto/carteira
    CONSTRAINT ck_wallet_entries_amount CHECK (amount >= 0)                                                -- Saldo não negativo
);
COMMENT
ON TABLE wallet_entries IS 'Armazena o saldo de cada criptomoeda por carteira';
COMMENT
ON COLUMN wallet_entries.amount IS 'Quantidade da criptomoeda especificada em crypto_symbol';

-- Tabela: transactions
CREATE TABLE transactions
(
    id                    NUMBER(19) DEFAULT transactions_seq.NEXTVAL NOT NULL,
    type                  VARCHAR2(20) NOT NULL,                                                                           -- DEPOSIT, WITHDRAWAL, TRANSFER
    source_wallet_id      NUMBER(19),                                                                                      -- NULL para DEPOSIT
    destination_wallet_id NUMBER(19),                                                                                      -- NULL para WITHDRAWAL
    crypto_symbol         VARCHAR2(10) NOT NULL,
    amount                NUMBER(38, 18) NOT NULL,
    transaction_date      TIMESTAMP DEFAULT SYSTIMESTAMP NOT NULL,
    status                VARCHAR2(20) NOT NULL,                                                                           -- PENDING, COMPLETED, FAILED
    -- Constraints (Inline)
    CONSTRAINT pk_transactions PRIMARY KEY (id),
    CONSTRAINT fk_transactions_source_wallet FOREIGN KEY (source_wallet_id) REFERENCES wallets (id) ON DELETE SET NULL,    -- Mantém Tx se wallet for deletada
    CONSTRAINT fk_transactions_dest_wallet FOREIGN KEY (destination_wallet_id) REFERENCES wallets (id) ON DELETE SET NULL, -- Mantém Tx se wallet for deletada
    CONSTRAINT ck_transactions_type CHECK (type IN ('DEPOSIT', 'WITHDRAWAL', 'TRANSFER')),
    CONSTRAINT ck_transactions_status CHECK (status IN ('PENDING', 'COMPLETED', 'FAILED')),
    CONSTRAINT ck_transactions_amount CHECK (amount > 0)                                                                   -- Transação com valor positivo
);
COMMENT
ON TABLE transactions IS 'Histórico de movimentações de criptomoedas';
COMMENT
ON COLUMN transactions.type IS 'Tipo da transação: DEPOSIT, WITHDRAWAL, TRANSFER';
COMMENT
ON COLUMN transactions.status IS 'Status da transação: PENDING, COMPLETED, FAILED';

-- --------------------------------------------------------------------------
-- Populando tabelas com dados iniciais (Exemplos)
-- (Execute DEPOIS de criar as tabelas)
-- --------------------------------------------------------------------------

-- Criando Usuário 1 (Pessoa Física)
-- OBS: A senha '1234' será hasheada pelo DAO ao usar usersDao.save()
-- Para inserir direto no SQL, você precisaria gerar o hash BCrypt antes.
-- Vamos inserir sem a senha por enquanto, ou com um hash placeholder se a coluna permitir NULL (a nossa não permite).
-- Para popular via SQL, você precisaria gerar o hash externamente ou relaxar a constraint NOT NULL temporariamente.
-- Assumindo que o DAO fará o insert inicial:
-- INSERT INTO users (id, nome, cpf_cnpj, email, telefone, senha, ativo) VALUES (users_seq.NEXTVAL, 'Bianca PF', '11122233344', 'bianca.pf@mail.com', '1199998888', '$2a$10$placeholderhashparainsertdireto', 'S');
-- Vamos pular o insert direto de users por causa do hash. Assume-se que o app fará isso.

-- Supondo que o usuário com ID 1 foi criado pelo app:
-- Criando a Wallet para o usuário 1
INSERT INTO wallets (user_id)
VALUES (1);
-- Supondo que o ID do usuário Bianca seja 1

-- Adicionando saldos (Wallet Entries) para a Wallet 1 (Supondo que o ID da wallet gerado seja 1)
INSERT INTO wallet_entries (wallet_id, crypto_symbol, amount)
VALUES (1, 'BTC', 0.5);
INSERT INTO wallet_entries (wallet_id, crypto_symbol, amount)
VALUES (1, 'ETH', 2.12345);

-- Criando uma transação de Depósito simulada para a Wallet 1
INSERT INTO transactions (type, destination_wallet_id, crypto_symbol, amount, status)
VALUES ('DEPOSIT', 1, 'BTC', 0.5, 'COMPLETED');

-- Criando Usuário 2 (Pessoa Jurídica)
-- (Novamente, idealmente criado via App/DAO para hashear senha)
-- INSERT INTO users (id, nome, cpf_cnpj, email, telefone, senha, ativo) VALUES (users_seq.NEXTVAL, 'Empresa Voltz', '11222333000144', 'contato@voltz.com', '1133334444', '$2a$10$placeholderhashparainsertdireto', 'S');

-- Supondo que o usuário com ID 2 foi criado:
-- Criando a Wallet para o usuário 2
INSERT INTO wallets (user_id)
VALUES (2);
-- Supondo ID 2 para Empresa Voltz

-- Adicionando saldo para Wallet 2 (Supondo ID 2 para a wallet)
INSERT INTO wallet_entries (wallet_id, crypto_symbol, amount)
VALUES (2, 'USDC', 10000.00);
-- Exemplo com Stablecoin

-- --------------------------------------------------------------------------
-- Exemplos de Consultas (SELECT)
-- --------------------------------------------------------------------------

-- Ver todos os usuários
SELECT id, user_name, email, cpf_cnpj, active
FROM users;

-- Ver carteira de um usuário específico (ex: user_id = 1)
SELECT id, user_id, created_at
FROM wallets
WHERE user_id = 1;

-- Ver saldos de uma carteira específica (ex: wallet_id = 1)
SELECT crypto_symbol, amount, last_updated
FROM wallet_entries
WHERE wallet_id = 1;

-- Ver transações de uma carteira específica (ex: wallet_id = 1)
SELECT *
FROM transactions
WHERE source_wallet_id = 1
   OR destination_wallet_id = 1
ORDER BY transaction_date DESC;

-- --------------------------------------------------------------------------
-- Exemplos de Atualização (UPDATE)
-- --------------------------------------------------------------------------

-- Atualizar telefone do usuário com id 1 (DAO faria isso e atualizaria data_atualizacao)
UPDATE users
SET phone_number = '11987654321',
    date_updated = SYSTIMESTAMP
WHERE id = 1;

-- Simular adição de saldo via UPDATE (DAO faria isso e atualizaria last_updated)
UPDATE wallet_entries
SET amount       = amount + 1.5,
    last_updated = SYSTIMESTAMP
WHERE wallet_id = 1
  AND crypto_symbol = 'ETH';

-- --------------------------------------------------------------------------
-- Exemplo de Exclusão (DELETE) - Use com CUIDADO!
-- --------------------------------------------------------------------------

-- Deletar uma transação específica (ex: id = 1)
-- DELETE FROM transactions WHERE id = 1;

-- Deletar um saldo específico (ex: ETH da wallet 1)
-- DELETE FROM wallet_entries WHERE wallet_id = 1 AND crypto_symbol = 'ETH';

-- --------------------------------------------------------------------------
-- Controle de Transação (Manual)
-- Execute após um bloco de operações bem-sucedidas
-- --------------------------------------------------------------------------

COMMIT;

-- --------------------------------------------------------------------------
-- Reversão (Manual)
-- Execute se ocorrer um erro durante um bloco de operações manuais
-- --------------------------------------------------------------------------

-- ROLLBACK;

-- ==========================================================================
-- Fim do Script
-- ==========================================================================