package inventory.dao;

import java.io.Serializable;
import java.math.BigDecimal;

public interface UserDAO<E> extends BaseDAO<E>{
    Long getUserTotal();
}
