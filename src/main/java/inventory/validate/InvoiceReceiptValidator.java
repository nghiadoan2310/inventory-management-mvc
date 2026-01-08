package inventory.validate;

import inventory.model.Invoice;
import inventory.service.InvoiceService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import java.math.BigDecimal;
import java.util.List;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class InvoiceReceiptValidator implements Validator {
    InvoiceService invoiceService;

    @Override
    public boolean supports(Class<?> clazz) {
        return clazz == Invoice.class;
    }

    @Override
    public void validate(Object target, Errors errors) {
        Invoice invoice = (Invoice) target;
        ValidationUtils.rejectIfEmpty(errors, "code", "msg.required");
        ValidationUtils.rejectIfEmpty(errors, "price", "msg.required");
        ValidationUtils.rejectIfEmpty(errors, "qty", "msg.required");

        if (invoice.getCode() != null && !ObjectUtils.isEmpty(invoice.getCode())) {
            List<Invoice> results = invoiceService.find("code", invoice.getCode());
            if (results != null && !ObjectUtils.isEmpty(results)) {
                if (invoice.getId() != null && invoice.getId() != 0 && !ObjectUtils.isEmpty(invoice.getId())) {
                    if(!(invoice.getId().equals(results.get(0).getId()))) {
                        errors.rejectValue("code", "msg.code.exist");
                    }
                } else {
                    errors.rejectValue("code", "msg.code.exist");
                }
            }
        }

        if (invoice.getQty() <= 0) {
            errors.rejectValue("qty", "msg.wrong.qty");
        }

        if (invoice.getPrice() != null && invoice.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
            errors.rejectValue("price", "msg.wrong.format");
        }

        if (invoice.getFromDate() != null && invoice.getToDate() != null) {
            if (invoice.getFromDate().after(invoice.getToDate())) {
                errors.rejectValue("fromDate", "msg.wrong.date");
            }
        }
    }
}
