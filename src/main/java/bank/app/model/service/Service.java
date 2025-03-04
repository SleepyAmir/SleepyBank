package bank.app.model.service;

import java.util.List;

public interface Service<T, I>  {
    void save(T t) throws Exception;
    void edit(T t) throws Exception;
    void remove(I id) throws Exception;
    List<T> findAll() throws Exception;
    T findById(I id) throws Exception;
}
