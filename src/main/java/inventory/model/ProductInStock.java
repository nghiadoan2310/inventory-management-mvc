package inventory.model;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Setter
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductInStock implements Serializable {
    Integer id;
    ProductInfo productInfo;
    int qty;
    BigDecimal price;
    int activeFlag;
    Date createDate;
    Date updateDate;

    public ProductInStock() {
    }

    public ProductInStock(ProductInfo productInfo, int qty, int activeFlag, Date createDate, Date updateDate) {
        this.productInfo = productInfo;
        this.qty = qty;
        this.activeFlag = activeFlag;
        this.createDate = createDate;
        this.updateDate = updateDate;
    }
}
