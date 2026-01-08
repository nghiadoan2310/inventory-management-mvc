package inventory.dao;

import inventory.model.Paging;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public interface BaseDAO<E> {
    List<E> findAll(String queryStr, Map<String, Object> mapParams, Paging paging);
    E findById(Class<E> e, Serializable id);
    List<E> findByProperty(String name, Object value);
    void save(E instance);
    void update(E instance);
}
