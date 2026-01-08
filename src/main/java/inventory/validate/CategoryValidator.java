package inventory.validate;

import inventory.model.Category;
import inventory.service.ProductService;
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
public class CategoryValidator implements Validator {
    ProductService productService;

    @Override
    public boolean supports(Class<?> clazz) {
        return clazz == Category.class;
    }

    @Override
    public void validate(Object target, Errors errors) {
        Category category = (Category) target;
        ValidationUtils.rejectIfEmpty(errors, "code", "msg.required");
        ValidationUtils.rejectIfEmpty(errors, "name", "msg.required");
        ValidationUtils.rejectIfEmpty(errors, "description", "msg.required");
        if (category.getCode() != null && StringUtils.hasText(category.getCode())) {
            List<Category> results = productService.findCategory("code", category.getCode());
            if(results != null && !ObjectUtils.isEmpty(results)) {
                if (category.getId() != null && category.getId() != 0 && !ObjectUtils.isEmpty(category.getId())) {
                    if(!(category.getId().equals(results.get(0).getId()))) {
                        errors.rejectValue("code", "msg.code.exist");
                    }
                } else {
                    errors.rejectValue("code", "msg.code.exist");
                }
            }
        }
    }
}
