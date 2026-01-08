package inventory.service;

import inventory.dao.InvoiceDAO;
import inventory.model.Invoice;
import inventory.model.Paging;
import inventory.model.ProductInfo;
import inventory.util.Constant;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class InvoiceService {
    static Logger log = LogManager.getLogger(InvoiceService.class);

    ProductInStockService productInStockService;
    HistoryService historyService;

    InvoiceDAO<Invoice> invoiceDAO;

    public void save(Invoice invoice) throws Exception {
        invoice.setActiveFlag(1);
        invoice.setCreateDate(new Date());
        invoice.setUpdateDate(new Date());
        invoiceDAO.save(invoice);
        productInStockService.saveOrUpdate(invoice);
        historyService.save(invoice, Constant.ACTION_ADD);
    }

    public void update(Invoice invoice) throws Exception {
        int originQty = invoiceDAO.findById(Invoice.class, invoice.getId()).getQty();
        invoice.setUpdateDate(new Date());
        Invoice invoiceInStock = new Invoice();
        invoiceInStock.setProductInfo(invoice.getProductInfo());
        invoiceInStock.setType(invoice.getType());
        invoiceInStock.setPrice(invoice.getPrice());
        //Nếu update giảm số lượng thì invoiceInStock.setQty sẽ âm và ngược lại
        //Kết hợp với ProductInStockService thì qty trong stock sẽ thay đổi
        invoiceInStock.setQty(invoice.getQty() - originQty);

        invoiceDAO.update(invoice);
        historyService.save(invoice, Constant.ACTION_EDIT);
        productInStockService.saveOrUpdate(invoiceInStock);
    }

    public List<Invoice> getList(Invoice invoice, Paging paging) {
        StringBuilder queryStr = new StringBuilder();
        Map<String, Object> mapParams = new HashMap<>();

        if (invoice != null) {
            if (invoice.getType() != 0) {
                queryStr.append(" and model.type =: type");
                mapParams.put("type", invoice.getType());
            }

            if (invoice.getCode() != null) {
                queryStr.append(" and model.code LIKE concat('%', :code, '%')");
                mapParams.put("code", invoice.getCode());
            }

            if (invoice.getFromDate() != null) {
                queryStr.append(" and model.updateDate >= :fromDate");
                mapParams.put("fromDate", invoice.getFromDate());
            }

            if (invoice.getToDate() != null) {
                queryStr.append(" and model.updateDate <= :toDate");
                mapParams.put("toDate", invoice.getToDate());
            }
        }

        return invoiceDAO.findAll(queryStr.toString(), mapParams, paging);
    }

    public List<Invoice> find(String property, Object value) {
        return invoiceDAO.findByProperty(property, value);
    }
    public Invoice findById (int id) {
        return invoiceDAO.findById(Invoice.class, id);
    }
}
