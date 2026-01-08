package inventory.model;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.util.*;

@Setter
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Menu implements Serializable {
    Integer id;
    int parentId;
    String url;
    String name;
    int orderIndex; //Thứ tự xuất hiện trên thanh sidebar (nếu > 0)
    int activeFlag;
    Date createDate;
    Date updateDate;
    Set<Auth> auths = new HashSet<>(0);
    List<Menu> child;
    List<Integer> roleIds;
    Map<Integer, Integer> mapAuths;
    String idMenu;

    public Menu() {
    }

    public Menu(int parentId, String url, String name, int orderIndex, int activeFlag, Date createDate,
                Date updateDate) {
        this.parentId = parentId;
        this.url = url;
        this.name = name;
        this.orderIndex = orderIndex;
        this.activeFlag = activeFlag;
        this.createDate = createDate;
        this.updateDate = updateDate;
    }

    public Menu(int parentId, String url, String name, int orderIndex, int activeFlag, Date createDate, Date updateDate,
                Set<Auth> auths) {
        this.parentId = parentId;
        this.url = url;
        this.name = name;
        this.orderIndex = orderIndex;
        this.activeFlag = activeFlag;
        this.createDate = createDate;
        this.updateDate = updateDate;
        this.auths = auths;
    }
}
