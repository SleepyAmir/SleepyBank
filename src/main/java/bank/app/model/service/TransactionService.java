package bank.app.model.service;

import bank.app.model.entity.Transaction;
import bank.app.model.repository.TransactionRepository;
import java.util.List;

public class TransactionService implements Service<Transaction, Integer> {

    @Override
    public void save(Transaction transaction) throws Exception {
        try (TransactionRepository repo = new TransactionRepository()) {
            repo.save(transaction);
        }
    }

    @Override
    public void edit(Transaction transaction) throws Exception {
        try (TransactionRepository repo = new TransactionRepository()) {
            repo.edit(transaction);
        }
    }

    @Override
    public void remove(Integer id) throws Exception {
        try (TransactionRepository repo = new TransactionRepository()) {
            repo.remove(id);
        }
    }

    @Override
    public List<Transaction> findAll() throws Exception {
        try (TransactionRepository repo = new TransactionRepository()) {
            return repo.findAll();
        }
    }

    @Override
    public Transaction findById(Integer id) throws Exception {
        try (TransactionRepository repo = new TransactionRepository()) {
            return repo.findById(id);
        }
    }
}