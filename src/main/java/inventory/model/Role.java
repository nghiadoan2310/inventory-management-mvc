package inventory.model;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Setter
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Role implements Serializable {
    Integer id;
    String roleName;
    String description;
    int activeFlag;
    Date createDate;
    Date updateDate;
    Set<Auth> auths = new HashSet<>(0);
    Set<UserRole> userRoles = new HashSet<>(0);

    public Role() {
    }

    public Role(String roleName, String description, int activeFlag, Date createDate, Date updateDate) {
        this.roleName = roleName;
        this.description = description;
        this.activeFlag = activeFlag;
        this.createDate = createDate;
        this.updateDate = updateDate;
    }

    public Role(String roleName, String description, int activeFlag, Date createDate, Date updateDate, Set<Auth> auths,
                Set<UserRole> userRoles) {
        this.roleName = roleName;
        this.description = description;
        this.activeFlag = activeFlag;
        this.createDate = createDate;
        this.updateDate = updateDate;
        this.auths = auths;
        this.userRoles = userRoles;
    }
}
