package inventory.validate;

import inventory.model.Menu;
import inventory.service.MenuService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class MenuValidator implements Validator {
    MenuService menuService;

    @Override
    public boolean supports(Class<?> clazz) {
        return clazz == Menu.class;
    }

    @Override
    public void validate(Object target, Errors errors) {
        Menu menu = (Menu) target;
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "roleIds", "msg.required");
    }
}
