package inventory.dao;

import inventory.model.History;
import inventory.model.Invoice;
import inventory.model.UserRole;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

@Repository
@Transactional(rollbackFor = Exception.class)
public class InvoiceDAOImpl extends BaseDAOImpl<Invoice> implements InvoiceDAO<Invoice>{
    @Autowired
    SessionFactory sessionFactory;

    @Override
    public List<Object[]> getRevenueLast6Month() {
        String queryString =
                "WITH months AS (" +
                        "  SELECT DATE_FORMAT(DATE_SUB(CURDATE(), INTERVAL 5 MONTH), '%Y-%m') AS month " +
                        "  UNION ALL SELECT DATE_FORMAT(DATE_SUB(CURDATE(), INTERVAL 4 MONTH), '%Y-%m') " +
                        "  UNION ALL SELECT DATE_FORMAT(DATE_SUB(CURDATE(), INTERVAL 3 MONTH), '%Y-%m') " +
                        "  UNION ALL SELECT DATE_FORMAT(DATE_SUB(CURDATE(), INTERVAL 2 MONTH), '%Y-%m') " +
                        "  UNION ALL SELECT DATE_FORMAT(DATE_SUB(CURDATE(), INTERVAL 1 MONTH), '%Y-%m') " +
                        "  UNION ALL SELECT DATE_FORMAT(CURDATE(), '%Y-%m')" +
                        ") " + "SELECT m.month, ifnull(SUM(model.PRICE), 0) as total " +
                " FROM months m " + "LEFT JOIN inventory_management.invoice" +
                " as model " + "ON DATE_FORMAT(model.CREATE_DATE, '%Y-%m') = m.month and model.TYPE = 2 " +
                "GROUP BY m.month " + "ORDER BY m.month;";

        Query<Object[]> query = sessionFactory.getCurrentSession().createNativeQuery(queryString);

        return query.getResultList();
    }

    @Override
    public BigDecimal getProductTotalAMonth(int type) {
        String queryStr = "select sum(i.QTY) as QTY " +
                "from inventory_management.invoice as i " +
                "where i.TYPE " + "=?1" + " and date(CREATE_DATE) between date_format(CURDATE(), '%Y-%m-01') and curdate()";

        Query<BigDecimal> query = sessionFactory.getCurrentSession().createNativeQuery(queryStr);
        query.setParameter(1, type);

        return query.getSingleResult();
    }

    @Override
    public BigDecimal getProductTotalLastMonth(int type) {
        String queryStr = "select sum(i.QTY) as QTY " +
                "from inventory_management.invoice as i " +
                "where i.TYPE " + "=?1" + " and date(CREATE_DATE) " +
                "between date_format(date_sub(CURDATE(), INTERVAL 1 MONTH), '%Y-%m-01') " +
                "and date_sub(CURDATE(), INTERVAL 1 MONTH)";

        Query<BigDecimal> query = sessionFactory.getCurrentSession().createNativeQuery(queryStr);
        query.setParameter(1, type);

        return query.getSingleResult();
    }
}
