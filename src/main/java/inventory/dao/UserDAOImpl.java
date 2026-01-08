package inventory.dao;

import inventory.model.User;
import inventory.model.UserRole;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.math.BigDecimal;

@Repository
@Transactional(rollbackFor = Exception.class)
public class UserDAOImpl extends BaseDAOImpl<User> implements UserDAO<User>{
    @Autowired
    SessionFactory sessionFactory;

    @Override
    public Long getUserTotal() {
        String queryStr = "select count(u.ID) as TOTAL " +
                "from inventory_management.user as u " +
                "where u.ACTIVE_FLAG = 1;";

        Query<Long> query = sessionFactory.getCurrentSession().createNativeQuery(queryStr);

        return query.getSingleResult();
    }
}
