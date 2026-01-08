package inventory.controller;

import inventory.model.Category;
import inventory.model.ProductInfo;
import inventory.model.Paging;
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
@RequestMapping("/product-info")
public class ProductInfoController {
    ProductService productService;

    ProductInfoValidator productInfoValidator;

    @InitBinder("modelForm")
    private void initBinder(WebDataBinder binder) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        sdf.setLenient(false);
        binder.registerCustomEditor(Date.class, new CustomDateEditor(sdf, true));
        binder.addValidators(productInfoValidator);
    }

    @GetMapping("/list")
    public String redirectShowProductInfoList() {
        return "redirect:/product-info/list/1";
    }

    @GetMapping("/list/{page}")
    public String showProductInfoList(Model model, @RequestParam(value = "keyword", required = false) String keyword,
                                   @PathVariable("page") int page,
                                   HttpSession session) {

        Paging paging = new Paging(1);
        paging.setIndexPage(page);

        List<ProductInfo> productsInfo = productService.getAllProductInfo(keyword, paging);
        if(session.getAttribute(Constant.MSG_SUCCESS) != null ) {
            model.addAttribute(Constant.MSG_SUCCESS, session.getAttribute(Constant.MSG_SUCCESS));
            session.removeAttribute(Constant.MSG_SUCCESS);
        }
        if(session.getAttribute(Constant.MSG_ERROR)!=null ) {
            model.addAttribute(Constant.MSG_ERROR, session.getAttribute(Constant.MSG_ERROR));
            session.removeAttribute(Constant.MSG_ERROR);
        }
        model.addAttribute("productsInfo", productsInfo);
        model.addAttribute("keyword", keyword);
        model.addAttribute("pageInfo", paging);
        model.addAttribute("view", "/productInfo/productInfo-list");
        return "index";
    }

    @GetMapping("/add")
    public String add(Model model) {
        List<Category> categoryList = productService.getAllCategory(null, null);
        Map<String, String> mapCategory = new HashMap<>();
        categoryList.forEach(category -> mapCategory.put(category.getId().toString(), category.getName()));

        model.addAttribute("titlePage", "Add ProductInfo");
        model.addAttribute("modelForm", new ProductInfo());
        model.addAttribute("mapCategory", mapCategory);
        model.addAttribute("viewOnly", false);
        model.addAttribute("view", "/productInfo/productInfo-action");
        return "index";
    }

    @GetMapping("/edit/{id}")
    public String edit(Model model, @PathVariable("id") int id) {
        ProductInfo productInfo = productService.findByIdProductInfo(id);
        if (productInfo != null) {
            List<Category> categoryList = productService.getAllCategory(null, null);
            Map<String, String> mapCategory = new HashMap<>();
            categoryList.forEach(category -> mapCategory.put(category.getId().toString(), category.getName()));
            productInfo.setCateId(productInfo.getCategory().getId());

            model.addAttribute("titlePage", "Edit ProductInfo");
            model.addAttribute("modelForm", productInfo);
            model.addAttribute("mapCategory", mapCategory);
            model.addAttribute("viewOnly", false);
            model.addAttribute("view", "/productInfo/productInfo-action");
            return "index";
        }
        model.addAttribute("view", "/productInfo/productInfo-list");
        return "index";
    }

    @GetMapping("/view/{id}")
    public String view(Model model , @PathVariable("id") int id) {
        ProductInfo productInfo = productService.findByIdProductInfo(id);
        if(productInfo!=null) {
            productInfo.setCateId(productInfo.getCategory().getId());
            List<Category> categoryList = productService.getAllCategory(null, null);
            Map<String, String> mapCategory = new HashMap<>();
            categoryList.forEach(category -> mapCategory.put(category.getId().toString(), category.getName()));

            model.addAttribute("titlePage", "View ProductInfo");
            model.addAttribute("modelForm", productInfo);
            model.addAttribute("mapCategory", mapCategory);
            model.addAttribute("viewOnly", true);
            model.addAttribute("view", "/productInfo/productInfo-action");
            return "index";
        }
        model.addAttribute("view", "/productInfo/productInfo-list");
        return "index";
    }

    @PostMapping("/save")
    public String save(Model model, @ModelAttribute("modelForm") @Validated ProductInfo productInfo,
                       BindingResult result, HttpSession session){
        if(result.hasErrors()) {
            if(productInfo.getId() != null) {
                model.addAttribute("titlePage", "Edit ProductInfo");
            } else {
                model.addAttribute("titlePage", "Add ProductInfo");
            }

            List<Category> categoryList = productService.getAllCategory(null, null);
            Map<String, String> mapCategory = new HashMap<>();
            categoryList.forEach(category -> mapCategory.put(category.getId().toString(), category.getName()));

            model.addAttribute("mapCategory", mapCategory);
            model.addAttribute("modelForm",productInfo);
            model.addAttribute("viewOnly", false);
            model.addAttribute("view", "/productInfo/productInfo-action");
            return "index";
        }

        Category category = productService.findByIdCategory(productInfo.getCateId());
        productInfo.setCategory(category);

        if (productInfo.getId() != null && productInfo.getId() != 0 && !ObjectUtils.isEmpty(productInfo.getId())) {
            try {
                productService.updateProductInfo(productInfo);
                session.setAttribute(Constant.MSG_SUCCESS, "Update success!");
            } catch (Exception e) {
                session.setAttribute(Constant.MSG_ERROR, "Update error");
                throw new RuntimeException(e);
            }

        } else {
            try {
                productService.saveProductInfo(productInfo);
                session.setAttribute(Constant.MSG_SUCCESS, "Create success!");
            } catch (Exception e) {
                session.setAttribute(Constant.MSG_ERROR, "Create error");
                throw new RuntimeException(e);
            }
        }
        
        return "redirect:/product-info/list";
    }

    @GetMapping("/delete/{id}")
    public String delete(Model model, @PathVariable("id") int id, HttpSession session) {
        ProductInfo productInfo = productService.findByIdProductInfo(id);
        if (productInfo != null) {
            try {
                productService.deleteProductInfo(productInfo);
                session.setAttribute(Constant.MSG_SUCCESS, "Delete success");
            } catch (Exception e) {
                session.setAttribute(Constant.MSG_ERROR, "Delete error");
                throw new RuntimeException(e);
            }
        }
        model.addAttribute("view", "/productInfo/productInfo-list");
        return "index";
    }
}
