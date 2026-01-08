package inventory.service;

import inventory.dao.CategoryDAO;
import inventory.dao.ProductInStockDAO;
import inventory.dao.ProductInStockDAO;
import inventory.dao.ProductInfoDAO;
import inventory.model.*;
import inventory.model.ProductInStock;
import inventory.util.ConfigLoader;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ProductInStockService {
    static Logger log = LogManager.getLogger(ProductInStockService.class);

    ProductInStockDAO<ProductInStock> productInStockDAO;

    public void saveOrUpdate(Invoice invoice) throws Exception {
        log.info("Product In Stock" );
        if(invoice.getProductInfo() != null) {
            List<ProductInStock> productInStockList = productInStockDAO
                    .findByProperty("productInfo.code", invoice.getProductInfo().getCode());
            if(productInStockList != null && !productInStockList.isEmpty()) {
                ProductInStock productInStock = productInStockList.get(0);

                //Update
                productInStock.setUpdateDate(new Date());

                //Kiểm tra nếu type = 1 (hàng nhập)
                if (invoice.getType() == 1) {
                    productInStock.setPrice(invoice.getPrice()); //Set lại giá trong kho
                    productInStock.setQty(productInStock.getQty() + invoice.getQty());
                } else if(invoice.getType() == 2) {
                    productInStock.setQty(productInStock.getQty() - invoice.getQty());
                }

                productInStockDAO.update(productInStock);
            } else if (invoice.getType() == 1) {
                ProductInStock product = new ProductInStock();
                ProductInfo productInfo = new ProductInfo();

                productInfo.setId(invoice.getProductInfo().getId());
                product.setProductInfo(productInfo);
                product.setQty(invoice.getQty());
                product.setPrice(invoice.getPrice());
                product.setActiveFlag(1);
                product.setCreateDate(new Date());
                product.setUpdateDate(new Date());

                productInStockDAO.save(product);
            }
        }
    }

    public List<ProductInStock> getAll(String keyword, Paging paging) {
        StringBuilder queryStr = new StringBuilder();
        Map<String, Object> mapParams = new HashMap<>();

        if (StringUtils.hasText(keyword)) {
            queryStr.append(" and model.productInfo.category.name LIKE concat('%', :keyword, '%') " +
                    "or model.productInfo.code LIKE concat('%', :keyword, '%')" +
                    "or model.productInfo.name LIKE concat('%', :keyword, '%')");
            mapParams.put("keyword", keyword);
        }

        return productInStockDAO.findAll(queryStr.toString(), mapParams, paging);
    }

    public List<ProductInStock> findByProperty(String property, Object value) {
        return productInStockDAO.findByProperty(property, value);
    }

    public ProductInStock findById(int id) {
        return productInStockDAO.findById(ProductInStock.class, id);
    }
}
