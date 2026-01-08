package inventory.dao;

import inventory.model.Menu;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional(rollbackFor = Exception.class)
public class MenuDAOImpl extends BaseDAOImpl<Menu> implements MenuDAO<Menu>{

}
