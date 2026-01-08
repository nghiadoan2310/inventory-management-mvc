package inventory.dao;

import inventory.model.Auth;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional(rollbackFor = Exception.class)
public class AuthDAOImpl extends BaseDAOImpl<Auth> implements AuthDAO<Auth>{
    @Autowired
    SessionFactory sessionFactory;

    //Lấy ra userRole List theo userId(bao gồm cả active = 0)
    @Override
    public List<Auth> findByMenuId(int menuId) {
        String queryString = " from " + getGenericName() +
                " as model where model.menu.id" +
                "=?1";
        Query<Auth> query = sessionFactory.getCurrentSession().createQuery(queryString);
        query.setParameter(1, menuId);

        return query.getResultList();
    }

    @Override
    public Auth find(int menuId, int roleId) {
        String queryString = " from " + getGenericName() +
                " as model where model.menu.id" +
                "=?1" + "and model.role.id" + "=?2";
        Query<Auth> query = sessionFactory.getCurrentSession().createQuery(queryString);
        query.setParameter(1, menuId);
        query.setParameter(2, roleId);

        return query.uniqueResult();
    }
}
