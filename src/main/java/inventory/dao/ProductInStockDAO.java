package inventory.dao;

import java.math.BigDecimal;

public interface ProductInStockDAO<E> extends BaseDAO<E>{
    BigDecimal getProductTotal();
}
