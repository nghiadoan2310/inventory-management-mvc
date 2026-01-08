package inventory.controller;

import inventory.model.Paging;
import inventory.model.Role;
import inventory.model.User;
import inventory.service.RoleService;
import inventory.service.UserService;
import inventory.util.Constant;
import inventory.validate.RoleValidator;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Controller
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/role")
public class RoleController {
    RoleService roleService;

    RoleValidator roleValidator;

    @InitBinder("modelForm")
    private void initBinder(WebDataBinder binder) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        sdf.setLenient(false);
        binder.registerCustomEditor(Date.class, new CustomDateEditor(sdf, true));
        binder.addValidators(roleValidator);
    }

    @GetMapping("/list")
    public String redirectShowRoleList() {
        return "redirect:/role/list/1";
    }

    @GetMapping("/list/{page}")
    public String showRoleList(Model model, @RequestParam(value = "keyword", required = false) String keyword,
                                   @PathVariable("page") int page,
                                   HttpSession session) {

        Paging paging = new Paging(1);
        paging.setIndexPage(page);

        List<Role> roles = roleService.getRoleList(keyword, paging);
        if(session.getAttribute(Constant.MSG_SUCCESS) != null ) {
            model.addAttribute(Constant.MSG_SUCCESS, session.getAttribute(Constant.MSG_SUCCESS));
            session.removeAttribute(Constant.MSG_SUCCESS);
        }
        if(session.getAttribute(Constant.MSG_ERROR)!=null ) {
            model.addAttribute(Constant.MSG_ERROR, session.getAttribute(Constant.MSG_ERROR));
            session.removeAttribute(Constant.MSG_ERROR);
        }
        model.addAttribute("roles", roles);
        model.addAttribute("keyword", keyword);
        model.addAttribute("pageInfo", paging);
        model.addAttribute("view", "/role/role-list");
        return "index";
    }

    @GetMapping("/add")
    public String add(Model model) {
        model.addAttribute("titlePage", "Add Role");
        model.addAttribute("modelForm", new Role());
        model.addAttribute("viewOnly", false);
        model.addAttribute("view", "/role/role-action");

        return "index";
    }

    @GetMapping("/edit/{id}")
    public String edit(Model model, @PathVariable("id") int id) {
        Role role = roleService.findById(id);
        if (role != null) {
            model.addAttribute("titlePage", "Edit Role");
            model.addAttribute("modelForm", role);
            model.addAttribute("viewOnly", false);
            model.addAttribute("view", "/role/role-action");
            return "index";
        }
        model.addAttribute("view", "/user/user-list");
        return "index";
    }

    @PostMapping("/save")
    public String save(Model model, @ModelAttribute("modelForm") @Validated Role role,
                       BindingResult result, HttpSession session){
        if(result.hasErrors()) {
            if(role.getId() != null) {
                model.addAttribute("titlePage", "Edit Role");
            } else {
                model.addAttribute("titlePage", "Add Role");
            }

            model.addAttribute("modelForm",role);
            model.addAttribute("viewOnly", false);
            model.addAttribute("view", "/role/role-action");
            return "index";
        }

        if (role.getId() != null && role.getId() != 0 && !ObjectUtils.isEmpty(role.getId())) {
            try {
                roleService.update(role);
                session.setAttribute(Constant.MSG_SUCCESS, "Update success!");
            } catch (Exception e) {
                session.setAttribute(Constant.MSG_ERROR, "Update error");
                throw new RuntimeException(e);
            }

        } else {
            try {
                roleService.save(role);
                session.setAttribute(Constant.MSG_SUCCESS, "Create success!");
            } catch (Exception e) {
                session.setAttribute(Constant.MSG_ERROR, "Create error");
                throw new RuntimeException(e);
            }
        }
        
        return "redirect:/role/list";
    }

    @GetMapping("/delete/{id}")
    public String delete(Model model, @PathVariable("id") int id, HttpSession session) {
        Role role = roleService.findById(id);
        if (role != null) {
            try {
                roleService.delete(role);
                session.setAttribute(Constant.MSG_SUCCESS, "Delete success");
            } catch (Exception e) {
                session.setAttribute(Constant.MSG_ERROR, "Delete error");
                throw new RuntimeException(e);
            }
        }
        model.addAttribute("view", "/role/role-list");
        return "index";
    }
}
