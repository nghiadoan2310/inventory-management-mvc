package inventory.controller;

import inventory.model.Category;
import inventory.model.Paging;
import inventory.service.ProductService;
import inventory.util.Constant;
import inventory.validate.CategoryValidator;
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
import java.util.List;

@Controller
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/category")
public class CategoryController {
    ProductService productService;

    CategoryValidator categoryValidator;

    @InitBinder("modelForm")
    private void initBinder(WebDataBinder binder) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        sdf.setLenient(false);
        binder.registerCustomEditor(Date.class, new CustomDateEditor(sdf, true));
        binder.addValidators(categoryValidator);
    }

    @GetMapping("/list")
    public String redirectShowCategoryList() {
        return "redirect:/category/list/1";
    }

    @GetMapping("/list/{page}")
    public String showCategoryList(Model model, @RequestParam(value = "keyword", required = false) String keyword,
                                   @PathVariable("page") int page,
                                   HttpSession session) {

        Paging paging = new Paging(1);
        paging.setIndexPage(page);

        List<Category> categories = productService.getAllCategory(keyword, paging);
        if(session.getAttribute(Constant.MSG_SUCCESS) != null ) {
            model.addAttribute(Constant.MSG_SUCCESS, session.getAttribute(Constant.MSG_SUCCESS));
            session.removeAttribute(Constant.MSG_SUCCESS);
        }
        if(session.getAttribute(Constant.MSG_ERROR)!=null ) {
            model.addAttribute(Constant.MSG_ERROR, session.getAttribute(Constant.MSG_ERROR));
            session.removeAttribute(Constant.MSG_ERROR);
        }
        model.addAttribute("categories", categories);
        model.addAttribute("keyword", keyword);
        model.addAttribute("pageInfo", paging);
        model.addAttribute("view", "/category/category-list");
        return "index";
    }

    @PostMapping("/search")
    public void searchCategory(Model model, @ModelAttribute("searchForm") String value) {
        System.out.println(value);
    }

    @GetMapping("/add")
    public String add(Model model) {
        model.addAttribute("titlePage", "Add Category");
        model.addAttribute("modelForm", new Category());
        model.addAttribute("viewOnly", false);
        model.addAttribute("view", "/category/category-action");
        return "index";
    }

    @GetMapping("/edit/{id}")
    public String edit(Model model, @PathVariable("id") int id) {
        Category category = productService.findByIdCategory(id);
        if (category != null) {
            model.addAttribute("titlePage", "Edit Category");
            model.addAttribute("modelForm",category);
            model.addAttribute("viewOnly", false);
            model.addAttribute("view", "/category/category-action");
            return "index";
        }
        model.addAttribute("view", "/category/category-list");
        return "index";
    }

    @GetMapping("/view/{id}")
    public String view(Model model , @PathVariable("id") int id) {
        Category category = productService.findByIdCategory(id);
        if(category!=null) {
            model.addAttribute("titlePage", "View Category");
            model.addAttribute("modelForm", category);
            model.addAttribute("viewOnly", true);
            model.addAttribute("view", "/category/category-action");
            return "index";
        }
        model.addAttribute("view", "/category/category-list");
        return "index";
    }

    @PostMapping("/save")
    public String save(Model model, @ModelAttribute("modelForm") @Validated Category category,
                       BindingResult result, HttpSession session){
        if(result.hasErrors()) {
            if(category.getId() != null) {
                model.addAttribute("titlePage", "Edit Category");
            } else {
                model.addAttribute("titlePage", "Add Category");
            }
            model.addAttribute("modelForm",category);
            model.addAttribute("viewOnly", false);
            model.addAttribute("view", "/category/category-action");
            return "index";
        }

        if (category.getId() != null && category.getId() != 0 && !ObjectUtils.isEmpty(category.getId())) {
            try {
                productService.updateCategory(category);
                session.setAttribute(Constant.MSG_SUCCESS, "Update success!");
            } catch (Exception e) {
                session.setAttribute(Constant.MSG_ERROR, "Update error");
                throw new RuntimeException(e);
            }

        } else {
            try {
                productService.saveCategory(category);
                session.setAttribute(Constant.MSG_SUCCESS, "Create success!");
            } catch (Exception e) {
                session.setAttribute(Constant.MSG_ERROR, "Create error");
                throw new RuntimeException(e);
            }
        }
        
        return "redirect:/category/list";
    }

    @GetMapping("/delete/{id}")
    public String delete(Model model, @PathVariable("id") int id, HttpSession session) {
        Category category = productService.findByIdCategory(id);
        if (category != null) {
            try {
                productService.deleteCategory(category);
                session.setAttribute(Constant.MSG_SUCCESS, "Delete success");
            } catch (Exception e) {
                session.setAttribute(Constant.MSG_ERROR, "Delete error");
                throw new RuntimeException(e);
            }
        }
        model.addAttribute("view", "/category/category-list");
        return "index";
    }
}
