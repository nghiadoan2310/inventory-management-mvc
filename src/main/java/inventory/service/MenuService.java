package inventory.service;

import inventory.dao.*;
import inventory.model.*;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class MenuService {
    static Logger log = LogManager.getLogger(MenuService.class);

    MenuDAO<Menu> menuDAO;
    RoleDAO<Role> roleDAO;
    AuthDAO<Auth> authDAO;

//    public void save(Menu menu) {
//        menu.setActiveFlag(1);
//        menu.setCreateDate(new Date());
//        menu.setUpdateDate(new Date());
//
//        menuDAO.save(menu);
//
//        List<Role> adminRole = roleDAO.findByProperty("roleName", "admin");
//        if (adminRole != null) {
//            if(menu.getRoleIds() != null) {
//                menu.getRoleIds().forEach(roleId -> {
//                    Auth auth = new Auth();
//                    auth.setMenu(menu);
//                    auth.setRole(roleDAO.findById(Role.class, roleId));
//                    auth.setActiveFlag(1);
//                    auth.setCreateDate(new Date());
//                    auth.setUpdateDate(new Date());
//
//                    authDAO.save(auth);
//                });
//            } else {
//                Auth auth = new Auth();
//                auth.setMenu(menu);
//                auth.setRole(adminRole.get(0));
//                auth.setActiveFlag(1);
//                auth.setCreateDate(new Date());
//                auth.setUpdateDate(new Date());
//
//                authDAO.save(auth);
//            }
//        }
//    }

    public void update(Menu menu) {
        Menu menu_origin = menuDAO.findById(Menu.class, menu.getId());
        menu_origin.setUpdateDate(new Date());

        menuDAO.update(menu_origin);

        List<Auth> authList = authDAO.findByMenuId(menu.getId());
        List<Integer> roleIdsInAuth = new ArrayList<>();

        //Cập nhật lại permission trong bảng auth
        authList.forEach(auth -> {
            if (!menu.getRoleIds().contains(auth.getRole().getId())) {
                auth.setPermission(0);
                authDAO.update(auth);
            } else if(auth.getPermission() == 0){
                auth.setPermission(1);
                authDAO.update(auth);
            }
            roleIdsInAuth.add(auth.getRole().getId());
        });

        menu.getRoleIds().forEach(roleId -> {
            if (!roleIdsInAuth.contains(roleId)) {
                Auth auth = new Auth();
                auth.setMenu(menu);
                auth.setRole(roleDAO.findById(Role.class, roleId));
                auth.setPermission(1);
                auth.setActiveFlag(1);
                auth.setCreateDate(new Date());
                auth.setUpdateDate(new Date());

                authDAO.save(auth);
            }
        });

    }

    public List<Menu> getMenuList(String keyword, Paging paging) {
        StringBuilder queryStr = new StringBuilder();
        Map<String, Object> mapParams = new HashMap<>();

        queryStr.append(" or model.activeFlag=0");
        if (StringUtils.hasText(keyword)) {
            queryStr.append(" and model.url LIKE concat('%', :keyword, '%') or model.name LIKE concat('%', :keyword, '%')");
            mapParams.put("keyword", keyword);
        }

        return menuDAO.findAll(queryStr.toString(), mapParams, paging);
    }

    public List<Menu> findByProperty(String property, Object value) {
        log.info("Find user by property ");
        return menuDAO.findByProperty(property, value);
    }

    public Menu findById(int id) {
        return menuDAO.findById(Menu.class, id);
    }

    public void changeStatus(int id) {
        Menu menu = menuDAO.findById(Menu.class, id);
        if (menu != null) {
            menu.setActiveFlag(menu.getActiveFlag() == 1 ? 0 : 1);
            menu.setUpdateDate(new Date());
            menuDAO.update(menu);
        }
    }

//    public void updatePermission(int menuId, int roleId, int permission) {
//        Auth auth = authDAO.find(menuId, roleId);
//        if (auth != null) {
//            auth.setPermission(permission);
//            auth.setUpdateDate(new Date());
//
//            authDAO.update(auth);
//        } else {
//            if (permission == 1) {
//                Menu menu = new Menu();
//                menu.setId(menuId);
//                Role role = new Role();
//                role.setId(roleId);
//                Auth auth_new = new Auth();
//                auth_new.setMenu(menu);
//                auth_new.setRole(role);
//                auth_new.setPermission(1);
//                auth_new.setActiveFlag(1);
//                auth_new.setCreateDate(new Date());
//                auth_new.setUpdateDate(new Date());
//
//                authDAO.save(auth_new);
//            }
//        }
//    }
}
