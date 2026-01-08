package inventory.dao;

import java.util.List;

public interface AuthDAO<E> extends BaseDAO<E>{
    List<E> findByMenuId(int userId);
    E find(int menuId, int roleId);
}
