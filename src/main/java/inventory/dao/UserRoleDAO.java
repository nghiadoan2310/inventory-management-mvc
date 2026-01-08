package inventory.dao;

import java.util.List;

public interface UserRoleDAO<E> extends BaseDAO<E>{
    List<E> findByUserId(int userId);
}
