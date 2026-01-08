package inventory.dao;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import inventory.model.Paging;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional(rollbackFor = Exception.class)
public class BaseDAOImpl<E> implements BaseDAO<E>{
    final static Logger log = LogManager.getLogger(BaseDAOImpl.class);

    @Autowired
    SessionFactory sessionFactory;

    @Override
    public List<E> findAll(String queryStr, Map<String, Object> mapParams, Paging paging) {
        log.info("find all record from db");
        StringBuilder queryString = new StringBuilder();
        queryString.append(" from ").append(getGenericName()).append(" as model where model.activeFlag=1");
        StringBuilder countQuery = new StringBuilder();
        countQuery.append(" select count(*) from ").append(getGenericName()).append(" as model where model.activeFlag=1");

        if(queryStr!= null && !queryStr.isEmpty()) {
            queryString.append(queryStr);
            countQuery.append(queryStr);
        }

        Query<E> query = sessionFactory.getCurrentSession().createQuery(queryString.toString());
        Query<E> countQ = sessionFactory.getCurrentSession().createQuery(countQuery.toString());

        if(mapParams!=null && !mapParams.isEmpty()) {
            for (String key : mapParams.keySet()) {
                query.setParameter(key, mapParams.get(key));
                countQ.setParameter(key, mapParams.get(key));
            }
        }

        if(paging != null) {
            if(paging.getIndexPage() <= 0) {
                paging.setIndexPage(1);
            }

            query.setFirstResult(paging.getOffset());
            query.setMaxResults(paging.getRecordPerPage());
            long totalRecords = (long)countQ.uniqueResult();
            paging.setTotalRows(totalRecords);

            if(paging.getIndexPage() > paging.getTotalPages()) {
                paging.setIndexPage(paging.getTotalPages());
                query.setFirstResult(paging.getOffset());
                query.setMaxResults(paging.getRecordPerPage());
            }
        }

        log.info("query find all ===>" + queryString.toString());
        return query.list();
    }

    @Override
    public E findById(Class<E> e, Serializable id) {
        log.info("find by id");
        return sessionFactory.getCurrentSession().get(e, id);
    }

    @Override
    public List<E> findByProperty(String property, Object value) {
        log.info("find by property");
        StringBuilder queryString = new StringBuilder();
        queryString.append(" from ").append(getGenericName())
                .append(" as model where model.activeFlag=1 and model.")
                .append(property).append("=?1");
        log.info("query find by property ===>" + queryString.toString());
        Query<E> query = sessionFactory.getCurrentSession().createQuery(queryString.toString());
        query.setParameter(1, value);
        return query.getResultList();
    }

    @Override
    public void save(E instance) {
        log.info("save instance");
        sessionFactory.getCurrentSession().persist(instance);
    }

    @Override
    public void update(E instance) {
        log.info("update instance");
        sessionFactory.getCurrentSession().merge(instance);
    }

    public String getGenericName() {
        //Lấy class cha - getClass: lấy class hiện tại (this), getGenericSuperclass: lấy ra class cha của class this
        String s = getClass().getGenericSuperclass().toString(); //BaseDAO<E> với E là 1 object (VD: Auth, User,...)
        Pattern pattern = Pattern.compile("\\<(.*?)\\>");
        Matcher matcher = pattern.matcher(s); //match với chuỗi regex
        String generic = "null";
        //Nếu match
        if(matcher.find()) {
            //Lấy ra nhóm con với chuỗi nằm trong () là 1 nhóm - với group(0) là lấy tất cả chuỗi match regex
            generic = matcher.group(1);
        }
        return generic;
    }
}
