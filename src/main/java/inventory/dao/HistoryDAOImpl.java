package inventory.dao;

import inventory.model.History;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional(rollbackFor = Exception.class)
public class HistoryDAOImpl extends BaseDAOImpl<History> implements HistoryDAO<History>{

}
