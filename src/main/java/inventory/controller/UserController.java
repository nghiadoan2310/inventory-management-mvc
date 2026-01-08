package inventory.controller;

import inventory.model.*;
import inventory.service.RoleService;
import inventory.service.UserService;
import inventory.util.Constant;
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
@RequestMapping("/user")
public class UserController {
    UserService userService;
    RoleService roleService;

    UserValidator userValidator;

    @InitBinder("modelForm")
    private void initBinder(WebDataBinder binder) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        sdf.setLenient(false);
        binder.registerCustomEditor(Date.class, new CustomDateEditor(sdf, true));
        binder.addValidators(userValidator);
    }

    @GetMapping("/list")
    public String redirectShowUserList() {
        return "redirect:/user/list/1";
    }

    @GetMapping("/list/{page}")
    public String showUserList(Model model, @RequestParam(value = "keyword", required = false) String keyword,
                                   @PathVariable("page") int page,
                                   HttpSession session) {

        Paging paging = new Paging(1);
        paging.setIndexPage(page);

        List<User> users = userService.getUserList(keyword, paging);
        if(session.getAttribute(Constant.MSG_SUCCESS) != null ) {
            model.addAttribute(Constant.MSG_SUCCESS, session.getAttribute(Constant.MSG_SUCCESS));
            session.removeAttribute(Constant.MSG_SUCCESS);
        }
        if(session.getAttribute(Constant.MSG_ERROR)!=null ) {
            model.addAttribute(Constant.MSG_ERROR, session.getAttribute(Constant.MSG_ERROR));
            session.removeAttribute(Constant.MSG_ERROR);
        }
        model.addAttribute("users", users);
        model.addAttribute("keyword", keyword);
        model.addAttribute("pageInfo", paging);
        model.addAttribute("view", "/user/user-list");
        return "index";
    }

    @GetMapping("/add")
    public String add(Model model) {
        List<Role> roles = roleService.getRoleList(null, null);
        List<Role> roleList = roleService.findByProperty("roleName", "staff");
        User user = new User();
        if (roleList != null) {
            List<Integer> roleIds = new ArrayList<>();
            roleIds.add(roleList.get(0).getId());
            user.setRoleIds(roleIds);
        }

        model.addAttribute("titlePage", "Add User");
        model.addAttribute("modelForm", user);
        model.addAttribute("roles", roles);
        model.addAttribute("viewOnly", false);
        model.addAttribute("view", "/user/user-action");
        return "index";
    }

    @GetMapping("/edit/{id}")
    public String edit(Model model, @PathVariable("id") int id) {
        User user = userService.findById(id);
        if (user != null) {
            List<Integer> roleIds = new ArrayList<>();
            user.getUserRoles().forEach(userRole -> {
                if (userRole.getActiveFlag() == 1) {
                    roleIds.add(userRole.getRole().getId());
                }
            });
            user.setRoleIds(roleIds);
            List<Role> roles = roleService.getRoleList(null, null);

            model.addAttribute("titlePage", "Edit User");
            model.addAttribute("modelForm", user);
            model.addAttribute("roles", roles);
            model.addAttribute("viewOnly", false);
            model.addAttribute("view", "/user/user-action");
            return "index";
        }
        model.addAttribute("view", "/user/user-list");
        return "index";
    }

    @PostMapping("/save")
    public String save(Model model, @ModelAttribute("modelForm") @Validated User user,
                       BindingResult result, HttpSession session){
        if(result.hasErrors()) {
            if(user.getId() != null) {
                model.addAttribute("titlePage", "Edit User");
            } else {
                model.addAttribute("titlePage", "Add User");
            }

            List<Role> roles = roleService.getRoleList(null, null);

            model.addAttribute("roles", roles);
            model.addAttribute("modelForm",user);
            model.addAttribute("viewOnly", false);
            model.addAttribute("view", "/user/user-action");
            return "index";
        }

        String name = Objects.requireNonNull(StringUtils.split(user.getEmail(), "@"))[0];
        user.setName(name);

        List<Role> roleList = roleService.findByProperty("roleName", "staff");

        //Khi role list có role staff và role rỗng
        if (roleList != null && (user.getRoleIds() == null || ObjectUtils.isEmpty(user.getRoleIds()))) {
            List<Integer> roleIds = new ArrayList<>();
            roleIds.add(roleList.get(0).getId());
            user.setRoleIds(roleIds);
        }

        if (user.getId() != null && user.getId() != 0 && !ObjectUtils.isEmpty(user.getId())) {
            try {
                userService.update(user);
                session.setAttribute(Constant.MSG_SUCCESS, "Update success!");
            } catch (Exception e) {
                session.setAttribute(Constant.MSG_ERROR, "Update error");
                throw new RuntimeException(e);
            }

        } else {
            try {
                userService.save(user);
                session.setAttribute(Constant.MSG_SUCCESS, "Create success!");
            } catch (Exception e) {
                session.setAttribute(Constant.MSG_ERROR, "Create error");
                throw new RuntimeException(e);
            }
        }
        
        return "redirect:/user/list";
    }

    @GetMapping("/delete/{id}")
    public String delete(Model model, @PathVariable("id") int id, HttpSession session) {
        User user = userService.findById(id);
        if (user != null) {
            try {
                userService.delete(user);
                session.setAttribute(Constant.MSG_SUCCESS, "Delete success");
            } catch (Exception e) {
                session.setAttribute(Constant.MSG_ERROR, "Delete error");
                throw new RuntimeException(e);
            }
        }
        model.addAttribute("view", "/user/user-list");
        return "index";
    }
}
