package inventory.validate;

import inventory.model.Role;
import inventory.service.RoleService;
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
public class RoleValidator implements Validator {
    RoleService roleService;

    @Override
    public boolean supports(Class<?> clazz) {
        return clazz == Role.class;
    }

    @Override
    public void validate(Object target, Errors errors) {
        Role role = (Role) target;
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "roleName", "msg.required");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "description", "msg.required");
        if (role.getRoleName() != null && StringUtils.hasText(role.getRoleName())) {
            List<Role> results = roleService.findByProperty("roleName", role.getRoleName());
            if(results != null && !ObjectUtils.isEmpty(results)) {
                if (role.getId() != null && role.getId() != 0 && !ObjectUtils.isEmpty(role.getId())) {
                    if(!(role.getId().equals(results.get(0).getId()))) {
                        errors.rejectValue("roleName", "msg.role.exist");
                    }
                } else {
                    errors.rejectValue("roleName", "msg.role.exist");
                }
            }
        }
    }
}
