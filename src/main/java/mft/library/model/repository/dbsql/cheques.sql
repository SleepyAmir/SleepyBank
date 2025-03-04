CREATE TABLE Cheques (
                         chequeId INT IDENTITY(1,1) PRIMARY KEY,
                         issuerAccountId INT NOT NULL,
                         payeeAccountId INT NULL,
                         amount DECIMAL(12,2) NOT NULL CHECK (amount > 0),
                         chequeAddress VARCHAR(255) NOT NULL,
                         status VARCHAR(10) CHECK (status IN ('Pending', 'Cashed', 'Bounced')) DEFAULT 'Pending',
                         issuedAt DATETIME DEFAULT GETDATE(),
                         CONSTRAINT FK_Cheque_Issuer FOREIGN KEY (issuerAccountId) REFERENCES Accounts(accountId) ON DELETE CASCADE,
                         CONSTRAINT FK_Cheque_Payee FOREIGN KEY (payeeAccountId) REFERENCES Accounts(accountId) ON DELETE SET NULL
);
