package inventory.model;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Setter
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class History implements Serializable {
    Integer id;
    ProductInfo productInfo;
    String actionName;
    int type;
    int qty;
    BigDecimal price;
    int activeFlag;
    Date createDate;
    Date updateDate;

    public History() {
    }

    public History(ProductInfo productInfo, String actionName, int type, int qty, BigDecimal price, int activeFlag,
                   Date createDate, Date updateDate) {
        this.productInfo = productInfo;
        this.actionName = actionName;
        this.type = type;
        this.qty = qty;
        this.price = price;
        this.activeFlag = activeFlag;
        this.createDate = createDate;
        this.updateDate = updateDate;
    }
}
