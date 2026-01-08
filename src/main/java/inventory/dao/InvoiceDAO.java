package inventory.dao;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

public interface InvoiceDAO<E> extends BaseDAO<E>{
    List<Object[]> getRevenueLast6Month();
    BigDecimal getProductTotalAMonth(int type);
    BigDecimal getProductTotalLastMonth(int type);
}
