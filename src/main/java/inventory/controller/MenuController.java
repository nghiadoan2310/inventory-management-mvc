package inventory.controller;

import inventory.model.*;
import inventory.service.MenuService;
import inventory.service.RoleService;
import inventory.service.UserService;
import inventory.util.Constant;
import inventory.validate.MenuValidator;
import inventory.validate.UserValidator;
import jakarta.servlet.http.HttpSession;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.*;

@Controller
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/menu")
public class MenuController {
    MenuService menuService;
    RoleService roleService;

    MenuValidator menuValidator;

    @InitBinder("modelForm")
    private void initBinder(WebDataBinder binder) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        sdf.setLenient(false);
        binder.registerCustomEditor(Date.class, new CustomDateEditor(sdf, true));
        binder.addValidators(menuValidator);
    }

    @GetMapping("/list")
    public String redirectShowUserList() {
        return "redirect:/menu/list/1";
    }

    @GetMapping("/list/{page}")
    public String showUserList(Model model, @RequestParam(value = "keyword", required = false) String keyword,
                                   @PathVariable("page") int page,
                                   HttpSession session) {

        Paging paging = new Paging(6);
        paging.setIndexPage(page);

        List<Menu> menus = menuService.getMenuList(keyword, paging);
        List<Role> roles = roleService.getRoleList(null, null);
        roles.sort((Comparator.comparingInt(Role::getId))); //Sắp xếp roleId tăng dần

        menus.forEach(menu -> {
            Map<Integer, Integer> mapAuths = new TreeMap<>();

            roles.forEach(role -> {
                mapAuths.put(role.getId(), 0);
            });

            menu.getAuths().forEach(auth -> {
                mapAuths.put(auth.getRole().getId(), auth.getPermission());
            });

            menu.setMapAuths(mapAuths);
        });

        if(session.getAttribute(Constant.MSG_SUCCESS) != null ) {
            model.addAttribute(Constant.MSG_SUCCESS, session.getAttribute(Constant.MSG_SUCCESS));
            session.removeAttribute(Constant.MSG_SUCCESS);
        }
        if(session.getAttribute(Constant.MSG_ERROR)!=null ) {
            model.addAttribute(Constant.MSG_ERROR, session.getAttribute(Constant.MSG_ERROR));
            session.removeAttribute(Constant.MSG_ERROR);
        }


        model.addAttribute("menus", menus);
        model.addAttribute("roles", roles);
        model.addAttribute("keyword", keyword);
        model.addAttribute("pageInfo", paging);
        model.addAttribute("view", "/menu/menu-list");
        return "index";
    }

    @GetMapping("/edit/{id}")
    public String edit(Model model, @PathVariable("id") int id) {
        Menu menu = menuService.findById(id);
        if (menu != null) {
            List<Integer> roleIds = new ArrayList<>();
            menu.getAuths().forEach(auth -> {
                if (auth.getActiveFlag() == 1 && auth.getPermission() == 1) {
                    roleIds.add(auth.getRole().getId());
                }
            });
            menu.setRoleIds(roleIds);
            List<Role> roles = roleService.getRoleList(null, null);

            model.addAttribute("titlePage", "Edit Menu");
            model.addAttribute("modelForm", menu);
            model.addAttribute("roles", roles);
            model.addAttribute("viewOnly", false);
            model.addAttribute("view", "/menu/menu-action");
            return "index";
        }
        model.addAttribute("view", "/menu/menu-list");
        return "index";
    }

    @PostMapping("/save")
    public String save(Model model, @ModelAttribute("modelForm") @Validated Menu menu,
                       BindingResult result, HttpSession session){
        if(result.hasErrors()) {
            List<Role> roles = roleService.getRoleList(null, null);

            model.addAttribute("titlePage", "Edit Menu");
            model.addAttribute("roles", roles);
            model.addAttribute("modelForm", menu);
            model.addAttribute("viewOnly", false);
            model.addAttribute("view", "/menu/menu-action");

            return "index";
        }

        List<Role> roleList = roleService.findByProperty("roleName", "admin");

        //Khi role list có role admin và role rỗng
        if (roleList != null && (menu.getRoleIds() == null || ObjectUtils.isEmpty(menu.getRoleIds()))) {
            List<Integer> roleIds = new ArrayList<>();
            roleIds.add(roleList.get(0).getId());
            menu.setRoleIds(roleIds);
        }

        if (menu.getId() != null && menu.getId() != 0 && !ObjectUtils.isEmpty(menu.getId())) {
            try {
                menuService.update(menu);
                session.setAttribute(Constant.MSG_SUCCESS, "Update success!");
            } catch (Exception e) {
                session.setAttribute(Constant.MSG_ERROR, "Update error");
                throw new RuntimeException(e);
            }
        }

        return "redirect:/menu/list";
    }

    @GetMapping("/change-status/{id}")
    public void changeStatus(Model model, @PathVariable("id") int id, HttpSession session) {
        try {
            menuService.changeStatus(id);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//    @GetMapping("/permission")
//    public String permission(Model model) {
//        List<Role> roles = roleService.getRoleList(null, null);
//        Map<Integer, Object> mapRoles = new HashMap<>();
//        roles.forEach(role -> {
//            mapRoles.put(role.getId(), role.getRoleName());
//        });
//
//        model.addAttribute("titlePage", "Permission");
//        model.addAttribute("modelForm", new AuthForm());
//        model.addAttribute("mapRoles", mapRoles);
//        model.addAttribute("view", "/menu/permission");
//
//        return "index";
//    }
//
//    @PostMapping("/update-permission")
//    public String updatePermission(Model model, BindingResult result ,@ModelAttribute("modelForm") AuthForm authForm, HttpSession session) {
//        if(result.hasErrors()) {
//            List<Role> roles = roleService.getRoleList(null, null);
//
//            model.addAttribute("titlePage", "Permission");
//            model.addAttribute("roles", roles);
//            model.addAttribute("modelForm", authForm);
//            model.addAttribute("viewOnly", false);
//            model.addAttribute("view", "/menu/permission");
//
//            return "index";
//        }
//
//        try {
//            menuService.updatePermission(authForm.getRoleId(), authForm.getMenuId(), authForm.getPermission());
//            session.setAttribute(Constant.MSG_SUCCESS, "Update success!!!");
//        } catch (Exception e) {
//            session.setAttribute(Constant.MSG_ERROR, "Update error");
//            throw new RuntimeException(e);
//        }
//
//        return "redirect:/menu/list";
//    }
}
