package inventory.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.util.*;

@Setter
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class User implements Serializable {
    Integer id;
    String userName;
    String password;
    String email;
    String name;
    int activeFlag;
    Date createDate;
    Date updateDate;
    Set<UserRole> userRoles = new HashSet<>(0);
    List<Integer> roleIds = new ArrayList<>();

    public User() {
    }

    public User(String userName, String password, String name, int activeFlag, Date createDate, Date updateDate) {
        this.userName = userName;
        this.password = password;
        this.name = name;
        this.activeFlag = activeFlag;
        this.createDate = createDate;
        this.updateDate = updateDate;
    }

    public User(String userName, String password, String email, String name, int activeFlag, Date createDate,
                 Date updateDate, Set<UserRole> userRoles) {
        this.userName = userName;
        this.password = password;
        this.email = email;
        this.name = name;
        this.activeFlag = activeFlag;
        this.createDate = createDate;
        this.updateDate = updateDate;
        this.userRoles = userRoles;
    }

}
