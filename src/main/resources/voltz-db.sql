-- Criação das tabelas conforme diagrama usando CREATE

CREATE TABLE Entity (
    id NUMBER,
    name VARCHAR2(255) NOT NULL
);

CREATE TABLE Company (
    id NUMBER,
    name VARCHAR2(255) NOT NULL,
    availableBalance NUMBER(15,2),
    bankAccount VARCHAR2(255)
);

CREATE TABLE Users (
    id NUMBER,
    name VARCHAR2(255) NOT NULL,
    email VARCHAR2(255) UNIQUE NOT NULL,
    password VARCHAR2(255) NOT NULL,
    authentication2FA NUMBER(1) CHECK (authentication2FA IN (0,1)),
    companyId NUMBER,
    FOREIGN KEY (companyId) REFERENCES Company(id)
);

CREATE TABLE Wallet (
    id NUMBER,
    userId NUMBER UNIQUE,
    totalBalance NUMBER(15,2),
    FOREIGN KEY (userId) REFERENCES Users(id)
);

CREATE TABLE CryptoAsset (
    id NUMBER,
    name VARCHAR2(255) NOT NULL,
    quantity NUMBER(15,6),
    currentValue NUMBER(15,2)
);

CREATE TABLE CryptoTransaction (
    id NUMBER,
    type VARCHAR2(50) NOT NULL,
    quantity NUMBER(15,6) NOT NULL,
    date DATE DEFAULT SYSDATE NOT NULL,
    value NUMBER(15,2) NOT NULL,
    cryptoAssetId NUMBER,
    FOREIGN KEY (cryptoAssetId) REFERENCES CryptoAsset(id)
);

CREATE TABLE Market (
    id NUMBER,
    exchangeRates NUMBER(10,6)
);

CREATE TABLE Dashboard (
    id NUMBER,
    walletId NUMBER,
    FOREIGN KEY (walletId) REFERENCES Wallet(id)
);

-- Adicionando chaves primárias usando ALTER

ALTER TABLE Entity ADD CONSTRAINT pk_entity PRIMARY KEY (id);
ALTER TABLE Company ADD CONSTRAINT pk_company PRIMARY KEY (id);
ALTER TABLE Users ADD CONSTRAINT pk_user PRIMARY KEY (id);
ALTER TABLE Wallet ADD CONSTRAINT pk_wallet PRIMARY KEY (id);
ALTER TABLE CryptoAsset ADD CONSTRAINT pk_cryptoasset PRIMARY KEY (id);
ALTER TABLE CryptoTransaction ADD CONSTRAINT pk_cryptotransaction PRIMARY KEY (id);
ALTER TABLE Market ADD CONSTRAINT pk_market PRIMARY KEY (id);
ALTER TABLE Dashboard ADD CONSTRAINT pk_dashboard PRIMARY KEY (id);

-- Iniciando transação para conseguir reverter em caso de erros

BEGIN;

-- Populando as tabelas com dados iniciais usando INSERT

INSERT INTO Company (id, name, availableBalance, bankAccount) VALUES (1, 'Crypto Corp', 100000.00, '123456789');
INSERT INTO Users (id, name, email, password, authentication2FA, companyId) VALUES (1, 'Bianca', 'bianca@mail.com', '1234', 1, 1);
INSERT INTO Wallet (id, userId, totalBalance) VALUES (1, 1, 5000.00);
INSERT INTO CryptoAsset (id, name, quantity, currentValue) VALUES (1, 'Bitcoin', 2, 50000.00);
INSERT INTO CryptoTransaction (id, type, quantity, date, value, cryptoAssetId) VALUES (1, 'Buy', 1, SYSDATE, 50000.00, 1);
INSERT INTO Market (id, exchangeRates) VALUES (1, 1.2);
INSERT INTO Dashboard (id, walletId) VALUES (1, 1);

-- Atualizações de exemplo usando UPDATE

UPDATE Wallet SET totalBalance = 6000.00 WHERE id = 1;
UPDATE CryptoAsset SET currentValue = 52000.00 WHERE id = 1;

-- Deletando um registro de exemplo usando DELETE

DELETE FROM Transaction WHERE id = 1;

-- Consultas para exibir os dados usando SELECT

SELECT * FROM Users;
SELECT * FROM Wallet;
SELECT * FROM CryptoAsset;
SELECT * FROM CryptoTransaction;
SELECT * FROM Market;
SELECT * FROM Dashboard;

-- Confirmação da transação caso tudo esteja correto

COMMIT;

-- Em caso de erro, reverter transação

ROLLBACK;

-- Dropando tabelas para testes usando DROP

DROP TABLE Dashboard CASCADE CONSTRAINTS;
DROP TABLE Market CASCADE CONSTRAINTS;
DROP TABLE CryptoTransaction CASCADE CONSTRAINTS;
DROP TABLE CryptoAsset CASCADE CONSTRAINTS;
DROP TABLE Wallet CASCADE CONSTRAINTS;
DROP TABLE Users CASCADE CONSTRAINTS;
DROP TABLE Company CASCADE CONSTRAINTS;
DROP TABLE Entity CASCADE CONSTRAINTS;



