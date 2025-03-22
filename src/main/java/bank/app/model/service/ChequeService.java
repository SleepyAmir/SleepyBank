package bank.app.model.service;

import bank.app.model.entity.Cheque;
import bank.app.model.repository.ChequeRepository;

import java.util.List;
import java.util.stream.Collectors;

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
            return repo.findAll().stream()
                    .filter(cheque -> cheque.getUser().getId() == userId)
                    .collect(Collectors.toList());
        }
    }

    public Cheque findByChequeNumber(String chequeNumber) throws Exception {
        try (ChequeRepository repo = new ChequeRepository()) {
            return repo.findAll().stream()
                    .filter(cheque -> cheque.getNumber().equals(chequeNumber))
                    .findFirst()
                    .orElse(null);
        }
    }
    public Cheque saveOrUpdate(Cheque cheque) throws Exception {
        try (ChequeRepository repo = new ChequeRepository()) {
            if (cheque.getId() == 0) {
                repo.save(cheque);
            }
            return repo.findById(cheque.getId());
        }
    }
}