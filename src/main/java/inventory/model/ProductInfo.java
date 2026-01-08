package inventory.model;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Setter
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductInfo implements Serializable {
    Integer id;
    Category category;
    String code;
    String name;
    String description;
    String imgUrl;
    int activeFlag;
    Date createDate;
    Date updateDate;
    MultipartFile multipartFile;
    private Integer cateId;
    Set<History> histories = new HashSet<>(0);
    Set<ProductInStock> productInStocks = new HashSet<>(0);
    Set<Invoice> invoices = new HashSet<>(0);

    public ProductInfo() {
    }

    public ProductInfo(Category category, String code, String name, String imgUrl, int activeFlag, Date createDate,
                       Date updateDate) {
        this.category = category;
        this.code = code;
        this.name = name;
        this.imgUrl = imgUrl;
        this.activeFlag = activeFlag;
        this.createDate = createDate;
        this.updateDate = updateDate;
    }

    public ProductInfo(Category category, String code, String name, String description, String imgUrl, int activeFlag,
                       Date createDate, Date updateDate, Set<History> histories,
                       Set<ProductInStock> productInStocks, Set<Invoice> invoices) {
        this.category = category;
        this.code = code;
        this.name = name;
        this.description = description;
        this.imgUrl = imgUrl;
        this.activeFlag = activeFlag;
        this.createDate = createDate;
        this.updateDate = updateDate;
        this.histories = histories;
        this.productInStocks = productInStocks;
        this.invoices = invoices;
    }
}
