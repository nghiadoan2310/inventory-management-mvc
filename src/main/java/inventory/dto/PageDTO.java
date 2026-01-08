package inventory.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Collections;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PageDTO<T> {
    int offset;
    int currentPage;
    int totalPages;
    int pageSize;
    long totalElements;

    List<T> data = Collections.emptyList();
}
