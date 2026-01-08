package inventory.dao;

import inventory.model.Category;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional(rollbackFor = Exception.class)
public class CategoryDAOImpl extends BaseDAOImpl<Category> implements CategoryDAO<Category>{

    @Autowired
    SessionFactory sessionFactory;

    @Override
    public List<Object[]> getCategoryPercent() {
        String queryStr = "with product as (\n" +
                "\tselect pi.CATE_ID as CATE_ID, SUM(s.QTY) as QTY_TOTAL\n" +
                "    from inventory_management.product_in_stock as s, inventory_management.product_info as pi\n" +
                "    where s.PRODUCT_ID = pi.ID\n" +
                "    group by pi.CATE_ID\n" +
                ")\n" +
                "\n" +
                "select c.NAME, p.QTY_TOTAL*100/(select sum(s.QTY) as TOTAL from inventory_management.product_in_stock as s) as PERCENT\n" +
                "from product as p, inventory_management.category as c\n" +
                "where p.CATE_ID = c.ID";

        Query<Object[]> query = sessionFactory.getCurrentSession().createNativeQuery(queryStr);

        return query.getResultList();
    }
}
