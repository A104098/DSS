package data;
import java.sql.SQLException;
import java.util.List;

public interface IGenericDAO<T> {
    void insert(T entity) throws SQLException;
    void update(T entity) throws SQLException;
    void delete(String id) throws SQLException;
    T findById(String id) throws SQLException;
    List<T> findAll() throws SQLException;
}