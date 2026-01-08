package inventory.dao;

import java.util.List;

public interface CategoryDAO<E> extends BaseDAO<E>{
    List<Object[]> getCategoryPercent();
}
