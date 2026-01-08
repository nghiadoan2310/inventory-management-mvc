package inventory.controller.api;

import inventory.dto.PageDTO;
import inventory.dto.ProductDTO;
import inventory.model.Paging;
import inventory.model.ProductInfo;
import inventory.service.ProductService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/product-info")
public class ProductInfoApiController {
    ProductService productService;

    @RequestMapping("/list/modal")
    public PageDTO<ProductDTO> getProductListForModal(@RequestParam(value = "keyword", required = false) String keyword,
                                                      @RequestParam(value = "page", required = false,
                                                              defaultValue = "1") Integer page) {

        Paging paging = new Paging(1);
        paging.setIndexPage(page);

        List<ProductDTO> productDTOList = productService.getAllProductInfo(keyword, paging)
                .stream().map(productInfo ->
                        new ProductDTO(productInfo.getId(), productInfo.getCode(), productInfo.getName()))
                .collect(Collectors.toList());

        return PageDTO.<ProductDTO>builder()
                .offset(paging.getOffset())
                .currentPage(page)
                .totalPages(paging.getTotalPages())
                .pageSize(paging.getRecordPerPage())
                .totalElements(paging.getTotalRows())
                .data(productDTOList)
                .build();
    }
}
