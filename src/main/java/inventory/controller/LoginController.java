package inventory.controller;

import inventory.model.*;
import inventory.service.UserService;
import inventory.util.Constant;
import inventory.validate.LoginValidator;
import jakarta.servlet.http.HttpSession;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Controller
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class LoginController {
    UserService userService;

    LoginValidator loginValidator;

    @InitBinder("loginForm")
    private void initBinder(WebDataBinder binder) {
        binder.addValidators(loginValidator); //add validate form login
    }

    @GetMapping("/login")
    public String login(Model model) {
        //Khai báo kiểu dữ lệu loginForm, thymeleaf sẽ hiểu loginFrom bind với User và hiểu được
        // các trường như userName, password sử dụng trong file html (views)
        model.addAttribute("loginForm", new User());
        return "auth/login";
    }

    @PostMapping("/processLogin")
    public String processLogin(Model model, @ModelAttribute("loginForm") @Validated User users, BindingResult result, HttpSession session) {

        if(result.hasErrors()) {
            return "auth/login";
        }

        User user = userService.findByProperty("userName", users.getUserName()).get(0);
        UserRole userRole = user.getUserRoles().iterator().next();
        List<Menu> menuList = new ArrayList<>();
        Role role = userRole.getRole();
        List<Menu> menuChildList = new ArrayList<>();
        for (Auth auth : role.getAuths()) {
            Menu menu = auth.getMenu();
            if (menu.getParentId() == 0 && menu.getOrderIndex() != -1 && menu.getActiveFlag() == 1
                    && auth.getPermission() == 1 && auth.getActiveFlag() == 1) {
                menu.setIdMenu(menu.getUrl().replace("/", "") + "Id");
                menuList.add(menu);
            } else if (menu.getParentId() != 0 && menu.getOrderIndex() != -1 && menu.getActiveFlag() == 1
                    && auth.getPermission() == 1 && auth.getActiveFlag() == 1) {
                menu.setIdMenu(menu.getUrl().replace("/", "") + "Id");
                menuChildList.add(menu);
            }
        }

        for (Menu menu : menuList) {
            List<Menu> childList = new ArrayList<>();
            for (Menu childMenu : menuChildList) {
                if(childMenu.getParentId() == menu.getId()) {
                    childList.add(childMenu);
                }
            }
            menu.setChild(childList);
        }
        sortMenu(menuList);
        for (Menu menu : menuList) {
            sortMenu(menu.getChild());
        }

        session.setAttribute(Constant.MENU_SESSION, menuList);
        session.setAttribute(Constant.USER_INFO, user);
        return "redirect:/";
    }

    @GetMapping("/access-denied")
    public String accessDenied() {
        return "error-page/page-403";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.removeAttribute(Constant.MENU_SESSION);
        session.removeAttribute(Constant.USER_INFO);
        return "redirect:/login";
    }

    public void sortMenu(List<Menu> menuList) {
        menuList.sort(new Comparator<Menu>() {
            @Override
            public int compare(Menu o1, Menu o2) {
                return o1.getOrderIndex() - o2.getOrderIndex();
            }
        });
    }

}
