package inventory.controller;

import inventory.model.Category;
import inventory.model.Paging;
import inventory.model.ProductInStock;
import inventory.model.ProductInfo;
import inventory.service.ProductInStockService;
import inventory.service.ProductService;
import inventory.util.Constant;
import inventory.validate.ProductInfoValidator;
import jakarta.servlet.http.HttpSession;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/product-in-stock")
public class ProductInStockController {
    ProductInStockService productInStockService;

    @GetMapping("/list")
    public String redirect() {
        return "redirect:/product-in-stock/list/1";
    }

    @GetMapping("/list/{page}")
    public String show(Model model, @RequestParam(value = "keyword", required = false) String keyword,
                                   @PathVariable("page") int page,
                                   HttpSession session) {

        Paging paging = new Paging(1);
        paging.setIndexPage(page);

        List<ProductInStock> products = productInStockService.getAll(keyword, paging);
        model.addAttribute("products", products);
        model.addAttribute("keyword", keyword);
        model.addAttribute("pageInfo", paging);
        model.addAttribute("view", "/productInStock/productInStock-list");
        return "index";
    }
}
