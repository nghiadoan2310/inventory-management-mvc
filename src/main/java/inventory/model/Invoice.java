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
public class Invoice implements Serializable {
    Integer id;
    ProductInfo productInfo;
    String code;
    int type;
    int qty;
    BigDecimal price;
    int activeFlag;
    Date createDate;
    Date updateDate;
    Date fromDate;
    Date toDate;
    Integer productId;
    String productName;

    public Invoice() {
    }

    public Invoice(ProductInfo productInfo, String code, int type, int qty, BigDecimal price, int activeFlag,
                   Date createDate, Date updateDate) {
        this.productInfo = productInfo;
        this.code = code;
        this.type = type;
        this.qty = qty;
        this.price = price;
        this.activeFlag = activeFlag;
        this.createDate = createDate;
        this.updateDate = updateDate;
    }
}
