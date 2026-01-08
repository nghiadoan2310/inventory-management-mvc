package inventory.model;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.util.Date;

@Setter
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserRole implements Serializable {
    Integer id;
    Role role;
    User user;
    int activeFlag;
    Date createDate;
    Date updateDate;

    public UserRole() {
    }

    public UserRole(Role role, User user, int activeFlag, Date createDate, Date updateDate) {
        this.role = role;
        this.user = user;
        this.activeFlag = activeFlag;
        this.createDate = createDate;
        this.updateDate = updateDate;
    }

}
