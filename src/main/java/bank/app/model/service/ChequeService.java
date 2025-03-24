package bank.app.model.service;

import bank.app.model.entity.Cheque;
import bank.app.model.repository.ChequeRepository;

import java.util.List;

public class ChequeService {
    public void save(Cheque cheque) throws Exception {
        try (ChequeRepository repo = new ChequeRepository()) {
            if (cheque.getId() == 0) {
                repo.save(cheque);
            } else {
                repo.edit(cheque);
            }
        }
    }

    public void delete(Cheque cheque) throws Exception {
        try (ChequeRepository repo = new ChequeRepository()) {
            repo.remove(cheque.getId());
        }
    }

    public List<Cheque> findAll() throws Exception {
        try (ChequeRepository repo = new ChequeRepository()) {
            return repo.findAll();
        }
    }

    public List<Cheque> findByUserId(int userId) throws Exception {
        try (ChequeRepository repo = new ChequeRepository()) {
            return repo.findByUserId(userId);
        }
    }

    public Cheque findByNumber(String chequeNumber) throws Exception {
        try (ChequeRepository repo = new ChequeRepository()) {
            return repo.findByNumber(chequeNumber);
        }
    }
}