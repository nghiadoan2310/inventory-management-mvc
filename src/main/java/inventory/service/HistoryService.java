package inventory.service;

import inventory.dao.HistoryDAO;
import inventory.dao.ProductInStockDAO;
import inventory.model.*;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class HistoryService {
    static Logger log = LogManager.getLogger(HistoryService.class);

    HistoryDAO<History> historyDAO;

    public void save(Invoice invoice, String action) throws Exception {
        log.info("History" );
        if(invoice.getProductInfo() != null) {
            History history = new History();

            history.setProductInfo(invoice.getProductInfo());
            history.setActionName(action);
            history.setType(invoice.getType());
            history.setQty(invoice.getQty());
            history.setPrice(invoice.getPrice());
            history.setActiveFlag(1);
            history.setCreateDate(new Date());
            history.setUpdateDate(new Date());

            historyDAO.save(history);
        }
    }

    public List<History> getAll(History history, Paging paging) {
        StringBuilder queryStr = new StringBuilder();
        Map<String, Object> mapParams = new HashMap<>();

        if (history != null) {
            if (history.getProductInfo() != null) {
                if (StringUtils.hasText(history.getProductInfo().getCategory().getName())) {
                    queryStr.append(" and model.productInfo.category.name LIKE concat('%', :cateName, '%')");
                    mapParams.put("cateName", history.getProductInfo().getCategory().getName());
                }

                if (StringUtils.hasText(history.getProductInfo().getName())) {
                    queryStr.append(" and model.productInfo.name LIKE concat('%', :name, '%')");
                    mapParams.put("name", history.getProductInfo().getName());
                }
            }

            if (StringUtils.hasText(history.getActionName())) {
                queryStr.append(" and model.actionName =: actionName");
                mapParams.put("actionName", history.getActionName());
            }

            if(history.getType() != 0) {
                queryStr.append(" and model.type =: type");
                mapParams.put("type", history.getType());
            }
        }

        return historyDAO.findAll(queryStr.toString(), mapParams, paging);
    }
}
