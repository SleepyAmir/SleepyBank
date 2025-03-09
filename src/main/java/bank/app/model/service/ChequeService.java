package bank.app.model.service;

import bank.app.model.entity.Cheque;
import bank.app.model.repository.ChequeRepository;
import java.util.List;

public class ChequeService implements Service<Cheque, Integer> {

    @Override
    public void save(Cheque cheque) throws Exception {
        try (ChequeRepository repo = new ChequeRepository()) {
            repo.save(cheque);
        }
    }

    @Override
    public void edit(Cheque cheque) throws Exception {
        try (ChequeRepository repo = new ChequeRepository()) {
            repo.edit(cheque);
        }
    }

    @Override
    public void remove(Integer id) throws Exception {
        try (ChequeRepository repo = new ChequeRepository()) {
            repo.remove(id);
        }
    }

    @Override
    public List<Cheque> findAll() throws Exception {
        try (ChequeRepository repo = new ChequeRepository()) {
            return repo.findAll();
        }
    }

    @Override
    public Cheque findById(Integer id) throws Exception {
        try (ChequeRepository repo = new ChequeRepository()) {
            return repo.findById(id);
        }
    }
}