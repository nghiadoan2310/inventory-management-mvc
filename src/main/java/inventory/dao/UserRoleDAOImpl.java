package inventory.dao;

import inventory.model.UserRole;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional(rollbackFor = Exception.class)
public class UserRoleDAOImpl extends BaseDAOImpl<UserRole> implements UserRoleDAO<UserRole>{

    @Autowired
    SessionFactory sessionFactory;

    //Lấy ra userRole List theo userId(bao gồm cả active = 0)
    @Override
    public List<UserRole> findByUserId(int userId) {
        String queryString = " from " + getGenericName() +
                " as model where model.user.id" +
                "=?1";
        Query<UserRole> query = sessionFactory.getCurrentSession().createQuery(queryString);
        query.setParameter(1, userId);

        return query.getResultList();
    }
}
