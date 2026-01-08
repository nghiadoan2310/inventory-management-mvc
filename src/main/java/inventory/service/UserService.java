package inventory.service;

import inventory.dao.RoleDAO;
import inventory.dao.UserDAO;
import inventory.dao.UserRoleDAO;
import inventory.model.Paging;
import inventory.model.Role;
import inventory.model.User;
import inventory.model.UserRole;
import inventory.util.HashingPassword;
import jakarta.persistence.criteria.CriteriaBuilder;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.util.*;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserService {
    static Logger log = LogManager.getLogger(UserService.class);

    UserDAO<User> userDAO;
    RoleDAO<Role> roleDAO;
    UserRoleDAO<UserRole> userRoleDAO;

    public void save(User user) {
        user.setPassword(HashingPassword.encrypt(user.getPassword()));
        user.setActiveFlag(1);
        user.setCreateDate(new Date());
        user.setUpdateDate(new Date());

        userDAO.save(user);

        List<Role> role = roleDAO.findByProperty("roleName", "staff");
        if (role != null) {
            saveUserRole(user.getRoleIds(), user, role.get(0));
        }
    }

    public void update(User user) {
        user.setPassword(HashingPassword.encrypt(user.getPassword()));
        user.setUpdateDate(new Date());

        userDAO.update(user);

        List<UserRole> userRoleList = userRoleDAO.findByUserId(user.getId());
        List<Integer> roleIdsInUserRole = new ArrayList<>();

        //Cập nhật lại activeFlag trong bảng userRole
        userRoleList.forEach(userRole -> {
            if (!user.getRoleIds().contains(userRole.getRole().getId())) {
                userRole.setActiveFlag(0);
                userRoleDAO.update(userRole);
            } else if (userRole.getActiveFlag() == 0) {
                userRole.setActiveFlag(1);
                userRoleDAO.update(userRole);
            }
            roleIdsInUserRole.add(userRole.getRole().getId());
        });

        user.getRoleIds().forEach(roleId -> {
            if (!roleIdsInUserRole.contains(roleId)) {
                UserRole userRole = new UserRole();
                userRole.setUser(user);
                userRole.setRole(roleDAO.findById(Role.class, roleId));
                userRole.setActiveFlag(1);
                userRole.setCreateDate(new Date());
                userRole.setUpdateDate(new Date());

                userRoleDAO.save(userRole);
            }
        });

    }

    public List<User> getUserList(String keyword, Paging paging) {
        StringBuilder queryStr = new StringBuilder();
        Map<String, Object> mapParams = new HashMap<>();

        if (StringUtils.hasText(keyword)) {
            queryStr.append(" and model.userName LIKE concat('%', :keyword, '%')");
            mapParams.put("keyword", keyword);
        }

        return userDAO.findAll(queryStr.toString(), mapParams, paging);
    }

    private void saveUserRole(List<Integer> roleIds, User user, Role roleDefault) {
        if(roleIds != null) {
            roleIds.forEach(roleId -> {
                UserRole userRole = new UserRole();
                userRole.setUser(user);
                userRole.setRole(roleDAO.findById(Role.class, roleId));
                userRole.setActiveFlag(1);
                userRole.setCreateDate(new Date());
                userRole.setUpdateDate(new Date());

                userRoleDAO.save(userRole);
            });
        } else {
            UserRole userRole = new UserRole();
            userRole.setUser(user);
            userRole.setRole(roleDefault);
            userRole.setActiveFlag(1);
            userRole.setCreateDate(new Date());
            userRole.setUpdateDate(new Date());

            userRoleDAO.save(userRole);
        }
    }

    public List<User> findByProperty(String property, Object value) {
        log.info("Find user by property ");
        return userDAO.findByProperty(property, value);
    }

    public User findById(int id) {
        return userDAO.findById(User.class, id);
    }

    public void delete(User user) {
        user.setActiveFlag(0);
        user.setUpdateDate(new Date());
        userDAO.update(user);
    }


}
