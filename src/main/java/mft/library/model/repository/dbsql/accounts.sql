CREATE TABLE Accounts (
                          accountId INT IDENTITY(1,1) PRIMARY KEY,
                          userId INT NOT NULL,
                          accountType VARCHAR(10) CHECK (accountType IN ('Checking', 'Savings')) NOT NULL,
                          balance DECIMAL(12,2) DEFAULT 0.00,
                          cardNumber VARCHAR(16) UNIQUE NOT NULL,
                          cvv2 CHAR(3) NOT NULL,
                          expiryDate DATE NOT NULL
);
