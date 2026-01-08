package inventory.model;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Setter
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Category implements Serializable {
    Integer id;
    String name;
    String code;
    String description;
    int activeFlag;
    Date createDate;
    Date updateDate;
    Set<ProductInfo> productInfos = new HashSet<>(0);

    public Category() {
    }

    public Category(String name, String code, int activeFlag, Date createDate, Date updateDate) {
        this.name = name;
        this.code = code;
        this.activeFlag = activeFlag;
        this.createDate = createDate;
        this.updateDate = updateDate;
    }

    public Category(String name, String code, String description, int activeFlag, Date createDate, Date updateDate,
                    Set<ProductInfo> productInfos) {
        this.name = name;
        this.code = code;
        this.description = description;
        this.activeFlag = activeFlag;
        this.createDate = createDate;
        this.updateDate = updateDate;
        this.productInfos = productInfos;
    }
}
