package inventory.dao;

import inventory.model.ProductInStock;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Repository
@Transactional(rollbackFor = Exception.class)
public class ProductInStockDAOImpl extends BaseDAOImpl<ProductInStock> implements ProductInStockDAO<ProductInStock>{

    @Autowired
    SessionFactory sessionFactory;

    @Override
    public BigDecimal getProductTotal() {
        String queryStr = "select sum(pit.QTY) as TOTAL " +
                "from inventory_management.product_in_stock as pit " +
                "where pit.ACTIVE_FLAG = 1;";

        Query<BigDecimal> query =  sessionFactory.getCurrentSession().createNativeQuery(queryStr);

        return query.getSingleResult();
    }
}
