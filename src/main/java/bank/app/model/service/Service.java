package bank.app.model.service;

import java.util.List;

public interface Service<T, I> {
    void save(T entity) throws Exception; // Corrected to use generic type T
    void edit(T entity) throws Exception;
    void remove(I id) throws Exception;
    List<T> findAll() throws Exception;
    T findById(I id) throws Exception;
}