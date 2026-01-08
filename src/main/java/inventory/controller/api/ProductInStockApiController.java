package inventory.controller.api;

import inventory.dto.PageDTO;
import inventory.dto.ProductDTO;
import inventory.dto.ProductInStockDTO;
import inventory.model.Paging;
import inventory.service.ProductInStockService;
import inventory.service.ProductService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/product-in-stock")
public class ProductInStockApiController {
    ProductInStockService productInStockService;

    @RequestMapping("/list/modal")
    public PageDTO<ProductInStockDTO> getProductListForModal(@RequestParam(value = "keyword", required = false) String keyword,
                                                             @RequestParam(value = "page", required = false,
                                                              defaultValue = "1") Integer page) {

        Paging paging = new Paging(1);
        paging.setIndexPage(page);

        List<ProductInStockDTO> productDTOList = productInStockService.getAll(keyword, paging)
                .stream().map(productInStock ->
                        new ProductInStockDTO(productInStock.getProductInfo().getId(), productInStock.getProductInfo().getCode(),
                                productInStock.getProductInfo().getName(), productInStock.getQty()))
                .collect(Collectors.toList());

        return PageDTO.<ProductInStockDTO>builder()
                .offset(paging.getOffset())
                .currentPage(page)
                .totalPages(paging.getTotalPages())
                .pageSize(paging.getRecordPerPage())
                .totalElements(paging.getTotalRows())
                .data(productDTOList)
                .build();
    }
}
