package inventory.model;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.util.Date;

@Setter
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Auth implements Serializable {
    Integer id;
    Menu menu;
    Role role;
    int permission;
    int activeFlag;
    Date createDate;
    Date updateDate;

    public Auth() {
    }

    public Auth(Menu menu, Role role, int permission, int activeFlag, Date createDate, Date updateDate) {
        this.menu = menu;
        this.role = role;
        this.permission = permission;
        this.activeFlag = activeFlag;
        this.createDate = createDate;
        this.updateDate = updateDate;
    }
}
