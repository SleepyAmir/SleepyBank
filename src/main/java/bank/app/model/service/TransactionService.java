package bank.app.model.service;

import bank.app.model.entity.Transaction;
import bank.app.model.repository.TransactionRepository;

import java.util.List;
import java.util.stream.Collectors;

public class TransactionService {
    private TransactionRepository transactionRepository;

    {
        try {
            transactionRepository = new TransactionRepository();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void save(Transaction transaction) throws Exception {
        try (TransactionRepository repo = new TransactionRepository()) {
            repo.save(transaction);
        }
    }

    public void delete(Transaction transaction) throws Exception {
        try (TransactionRepository repo = new TransactionRepository()) {
            repo.remove(transaction.getId()); // Assuming TransactionRepository has a remove method
        }
    }

    public List<Transaction> findAll() throws Exception {
        try (TransactionRepository repo = new TransactionRepository()) {
            return repo.findAll();
        }
    }

    public List<Transaction> findByUserId(int userId) throws Exception {
        return findAll().stream()
                .filter(t -> t.getSourceAccount().getUser().getId() == userId ||
                        t.getDestinationAccount().getUser().getId() == userId)
                .collect(Collectors.toList());
    }
}