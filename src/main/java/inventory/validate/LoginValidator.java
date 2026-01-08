package inventory.validate;

import inventory.model.User;
import inventory.service.UserService;
import inventory.util.HashingPassword;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import java.util.List;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class LoginValidator implements Validator {
    UserService userService;

    @Override
    public boolean supports(Class<?> clazz) {
        return clazz == User.class;
    }

    @Override
    public void validate(Object target, Errors errors) {
        User user = (User) target;
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "userName", "msg.required");
        ValidationUtils.rejectIfEmpty(errors, "password", "msg.required");
        if(StringUtils.hasText(user.getUserName()) && StringUtils.hasText(user.getPassword())) {
            List<User> users = userService.findByProperty("userName", user.getUserName());
            if(users != null && !users.isEmpty()) {
                if(!user.getUserName().equals("admin") && !users.get(0).getPassword().equals(HashingPassword.encrypt(user.getPassword()))) {
                    errors.rejectValue("password", "msg.wrong.password");
                }
            } else {
                errors.rejectValue("userName", "msg.wrong.username");
            }
        }
    }
}
