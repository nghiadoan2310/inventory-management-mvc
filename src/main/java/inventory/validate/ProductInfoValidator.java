package inventory.validate;

import inventory.model.Category;
import inventory.model.ProductInfo;
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
public class ProductInfoValidator implements Validator {
    ProductService productService;

    @Override
    public boolean supports(Class<?> clazz) {
        return clazz == ProductInfo.class;
    }

    @Override
    public void validate(Object target, Errors errors) {
        ProductInfo productInfo = (ProductInfo) target;
        ValidationUtils.rejectIfEmpty(errors, "code", "msg.required");
        ValidationUtils.rejectIfEmpty(errors, "name", "msg.required");
        ValidationUtils.rejectIfEmpty(errors, "description", "msg.required");
        ValidationUtils.rejectIfEmpty(errors, "multipartFile", "msg.required");
        if (productInfo.getCode() != null && StringUtils.hasText(productInfo.getCode())) {
            List<ProductInfo> results = productService.findProductInfo("code", productInfo.getCode());
            if(results != null && !ObjectUtils.isEmpty(results)) {
                if (productInfo.getId() != null && productInfo.getId() != 0 && !ObjectUtils.isEmpty(productInfo.getId())) {
                    if(!(productInfo.getId().equals(results.get(0).getId()))) {
                        errors.rejectValue("code", "msg.code.exist");
                    }
                } else {
                    errors.rejectValue("code", "msg.code.exist");
                }
            }
        }

        if(productInfo.getMultipartFile() != null) {
            String extension = StringUtils.getFilenameExtension(productInfo.getMultipartFile().getOriginalFilename());
            if(!("png").equals(extension) && !("jpg").equals(extension)) {
                errors.rejectValue("multipartFile", "msg.file.extension.error");
            }
        }
    }
}
