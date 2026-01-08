package inventory.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Paging {
    long totalRows;
    int totalPages;
    int indexPage;
    int recordPerPage = 10;
    int offset;

    public Paging(int recordPerPage) {
        this.recordPerPage = recordPerPage;
    }

    public int getTotalPages() {
        if(indexPage > 0) {
            totalPages = (int) Math.ceil(totalRows/(double)recordPerPage);
        }
        return totalPages;
    }

    public int getOffset() {
        if(indexPage > 0) {
            offset = indexPage*recordPerPage - recordPerPage ;
        }
        return offset;
    }
}
