package inventory.validate;

import inventory.model.Invoice;
import inventory.model.ProductInStock;
import inventory.service.InvoiceService;
import inventory.service.ProductInStockService;
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
public class InvoiceIssueValidator implements Validator {
    InvoiceService invoiceService;
    ProductInStockService productInStockService;

    @Override
    public boolean supports(Class<?> clazz) {
        return clazz == Invoice.class;
    }

    @Override
    public void validate(Object target, Errors errors) {
        Invoice invoice = (Invoice) target;
        ValidationUtils.rejectIfEmpty(errors, "code", "msg.required");
        ValidationUtils.rejectIfEmpty(errors, "productName", "msg.required");
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

        if (invoice.getProductId() != null && !ObjectUtils.isEmpty(invoice.getProductId())) {
            List<ProductInStock> results = productInStockService.findByProperty("productInfo.id", invoice.getProductId());
            if (results != null) {
                ProductInStock productInStock = results.get(0);
                if (!productInStock.getProductInfo().getName().equals(invoice.getProductName())) {
                    errors.rejectValue("productInfo.name", "msg.wrong.productInfo");
                }

                if(invoice.getId() != null && !ObjectUtils.isEmpty(invoice.getId())) {
                    Invoice invoice_exist = invoiceService.findById(invoice.getId());
                    if (invoice_exist != null) {
                        if (invoice.getQty() > productInStock.getQty() + invoice_exist.getQty()) {
                            errors.rejectValue("qty", "msg.wrong.qty.goodsIssues");
                        }
                    } else {
                        errors.rejectValue("code", "msg.wrong.format");
                    }
                } else {
                    if (invoice.getQty() > productInStock.getQty()) {
                        errors.rejectValue("qty", "msg.wrong.qty.goodsIssues");
                    }
                }
            } else {
                errors.rejectValue("productInfo.name", "msg.wrong.productInfo");
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
