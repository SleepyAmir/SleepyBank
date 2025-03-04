CREATE TABLE transactions (
                              transactionId INT IDENTITY(1,1) PRIMARY KEY,
                              senderAccountId INT NULL,
                              receiverAccountId INT NULL,
                              amount DECIMAL(12,2) NOT NULL CHECK (amount > 0),
                              transactionType VARCHAR(10) CHECK (transactionType IN ('Withdraw', 'Deposit', 'Transfer')) NOT NULL,
                              timestamp DATETIME DEFAULT GETDATE(),
                              CONSTRAINT FK_Transaction_Sender FOREIGN KEY (senderAccountId) REFERENCES Accounts(accountId) ON DELETE SET NULL,
                              CONSTRAINT FK_Transaction_Receiver FOREIGN KEY (receiverAccountId) REFERENCES Accounts(accountId) ON DELETE SET NULL
);
