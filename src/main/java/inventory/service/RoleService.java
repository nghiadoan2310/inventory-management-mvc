package inventory.service;

import inventory.dao.RoleDAO;
import inventory.model.Paging;
import inventory.model.Role;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RoleService {
    static Logger log = LogManager.getLogger(RoleService.class);

    RoleDAO<Role> roleDAO;

    public void save(Role role) {
        role.setActiveFlag(1);
        role.setCreateDate(new Date());
        role.setUpdateDate(new Date());

        roleDAO.save(role);
    }

    public void update(Role role) {
        role.setUpdateDate(new Date());

        roleDAO.update(role);
    }

    public List<Role> getRoleList(String keyword, Paging paging) {
        StringBuilder queryStr = new StringBuilder();
        Map<String, Object> mapParams = new HashMap<>();

        if (StringUtils.hasText(keyword)) {
            queryStr.append(" and model.roleName LIKE concat('%', :keyword, '%')");
            mapParams.put("keyword", keyword);
        }

        return roleDAO.findAll(queryStr.toString(), mapParams, paging);
    }

    public List<Role> findByProperty(String property, Object value) {
        log.info("Find user by property ");
        return roleDAO.findByProperty(property, value);
    }

    public Role findById(int id) {
        return roleDAO.findById(Role.class, id);
    }

    public void delete(Role role) {
        role.setActiveFlag(0);
        role.setUpdateDate(new Date());
        roleDAO.update(role);
    }


}
