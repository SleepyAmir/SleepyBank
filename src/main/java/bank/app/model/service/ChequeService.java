package bank.app.model.service;

import bank.app.model.entity.Cheque;
import bank.app.model.repository.ChequeRepository;
import java.util.List;
import java.util.stream.Collectors;

public class ChequeService {
    private ChequeRepository chequeRepository = new ChequeRepository();

    public void save(Cheque cheque) throws Exception {
        try (ChequeRepository repo = new ChequeRepository()) {
            repo.save(cheque);
        }
    }

    public List<Cheque> findAll() throws Exception {
        try (ChequeRepository repo = new ChequeRepository()) {
            return repo.findAll();
        }
    }

    public List<Cheque> findByUserId(int userId) throws Exception {
        return findAll().stream()
                .filter(cheque -> cheque.getUser().getId() == userId)
                .collect(Collectors.toList());
    }
}