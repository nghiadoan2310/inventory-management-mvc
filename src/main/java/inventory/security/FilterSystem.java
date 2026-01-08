package inventory.security;

import inventory.model.Auth;
import inventory.model.User;
import inventory.model.UserRole;
import inventory.util.Constant;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Set;

public class FilterSystem implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        User user = (User) request.getSession().getAttribute(Constant.USER_INFO);
        if(user == null) {
            response.sendRedirect(request.getContextPath() + "/login"); //context-path set trong web.xml
        } else {
            String url = request.getServletPath();
            if (!hasPermission(url, user)) {
                response.sendRedirect(request.getContextPath() + "/access-denied");
                return false;
            }
            return true;
        }

        return HandlerInterceptor.super.preHandle(request, response, handler);
    }

    private boolean hasPermission(String url, User user) {
        if (url.equals("/") || url.equals("/access-denied") || url.equals("/logout") ||
                url.equals("/product-info/list/modal") || url.equals("/dashboard")) {
            return true;
        }
        UserRole userRole = user.getUserRoles().iterator().next();
        Set<Auth> auths = userRole.getRole().getAuths();
        for (Auth auth : auths) {
            if (url.contains(auth.getMenu().getUrl())) {
                return auth.getPermission() == 1;
            }
        };
        return false;
    }
}
