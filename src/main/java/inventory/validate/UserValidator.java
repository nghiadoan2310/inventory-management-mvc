package inventory.validate;

import inventory.model.Category;
import inventory.model.User;
import inventory.service.UserService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import java.util.List;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserValidator implements Validator {
    UserService userService;

    @Override
    public boolean supports(Class<?> clazz) {
        return clazz == User.class;
    }

    @Override
    public void validate(Object target, Errors errors) {
        User user = (User) target;
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "userName", "msg.required");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "password", "msg.required");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "email", "msg.required");
        if (user.getUserName() != null && StringUtils.hasText(user.getUserName())) {
            List<User> results = userService.findByProperty("userName", user.getUserName());
            if(results != null && !ObjectUtils.isEmpty(results)) {
                if (user.getId() != null && user.getId() != 0 && !ObjectUtils.isEmpty(user.getId())) {
                    if(!(user.getId().equals(results.get(0).getId()))) {
                        errors.rejectValue("userName", "msg.username.exist");
                    }
                } else {
                    errors.rejectValue("userName", "msg.username.exist");
                }
            }
        }

        if (user.getEmail() != null && StringUtils.hasText(user.getEmail())) {
            List<User> results = userService.findByProperty("email", user.getEmail());
            if(results != null && !ObjectUtils.isEmpty(results)) {
                if (user.getId() != null && user.getId() != 0 && !ObjectUtils.isEmpty(user.getId())) {
                    if(!(user.getId().equals(results.get(0).getId()))) {
                        errors.rejectValue("email", "msg.email.exist");
                    }
                } else {
                    errors.rejectValue("email", "msg.email.exist");
                }
            }
        }
    }
}
