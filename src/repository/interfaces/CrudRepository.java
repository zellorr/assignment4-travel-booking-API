package repository.interfaces;

import java.util.List;

public interface CrudRepository<T> {
    void create(T entity);
    List<T> getAll();
    T getById(int id);
    void update(int id, T entity);
    void delete(int id);
}