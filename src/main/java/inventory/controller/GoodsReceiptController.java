package inventory.controller;

import inventory.model.Invoice;
import inventory.model.Paging;
import inventory.model.ProductInfo;
import inventory.service.GoodsReceiptReport;
import inventory.service.InvoiceService;
import inventory.service.ProductService;
import inventory.util.Constant;
import inventory.validate.InvoiceReceiptValidator;
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
import org.springframework.web.servlet.ModelAndView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Controller
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/goods-receipt")
public class GoodsReceiptController {
    InvoiceService invoiceService;
    ProductService productService;

    InvoiceReceiptValidator invoiceReceiptValidator;

    @InitBinder({"modelForm", "searchForm"})
    private void initBinder (WebDataBinder binder) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        sdf.setLenient(false);
        binder.registerCustomEditor(Date.class, new CustomDateEditor(sdf, true));
        binder.addValidators(invoiceReceiptValidator);
    }

    @GetMapping("/list")
    public String redirect() {
        return "redirect:/goods-receipt/list/1";
    }

    @RequestMapping("/list/{page}")
    public String showInvoiceList(Model model, @ModelAttribute("searchForm") Invoice invoice,
                          @PathVariable("page") int page, HttpSession session) {

        Paging paging = new Paging(1);
        paging.setIndexPage(page);

        //Trạng thái khi không có thông tin tìm kiếm
//        if (invoice == null) {
//            invoice = new Invoice();
//        }

        invoice.setType(Constant.TYPE_GOODS_RECEIPT);
        List<Invoice> invoices = invoiceService.getList(invoice, paging);

        if(session.getAttribute(Constant.MSG_SUCCESS) != null ) {
            model.addAttribute(Constant.MSG_SUCCESS, session.getAttribute(Constant.MSG_SUCCESS));
            session.removeAttribute(Constant.MSG_SUCCESS);
        }
        if(session.getAttribute(Constant.MSG_ERROR)!=null ) {
            model.addAttribute(Constant.MSG_ERROR, session.getAttribute(Constant.MSG_ERROR));
            session.removeAttribute(Constant.MSG_ERROR);
        }

        model.addAttribute("invoices", invoices);
        model.addAttribute("searchForm", invoice);
        model.addAttribute("pageInfo", paging);
        model.addAttribute("view", "/invoice/goodsReceipt/goodsReceipt-list");

        return "index";
    }

    @GetMapping("/add")
    public String add(Model model) {
        model.addAttribute("titlePage", "Add Invoice");
        model.addAttribute("button", "Add");
        model.addAttribute("modelForm", new Invoice());
        model.addAttribute("viewOnly", false);
        model.addAttribute("view", "/invoice/goodsReceipt/goodsReceipt-action");

        return "index";
    }

    @PostMapping("/save")
    public String save(Model model, @ModelAttribute("modelForm") @Validated Invoice invoice,
                       BindingResult result, HttpSession session) {
        if (result.hasErrors()) {
            if (invoice.getId() != null) {
                model.addAttribute("titlePage", "Edit Invoice");
            } else {
                model.addAttribute("titlePage", "Add Invoice");
            }

            model.addAttribute("modelForm", invoice);
            model.addAttribute("viewOnly", false);
            model.addAttribute("view", "/invoice/goodsReceipt/goodsReceipt-action");
            return "index";
        }

        ProductInfo productInfo = productService.findByIdProductInfo(invoice.getProductId());
        invoice.setProductInfo(productInfo);
        invoice.setType(Constant.TYPE_GOODS_RECEIPT);

        if (invoice.getId() != null && invoice.getId() != 0 && !ObjectUtils.isEmpty(invoice.getId())) {
            try {
                invoiceService.update(invoice);
                session.setAttribute(Constant.MSG_SUCCESS, "Update success!");
            } catch (Exception e) {
                session.setAttribute(Constant.MSG_ERROR, "Update error");
                throw new RuntimeException(e);
            }

        } else {
            try {
                invoiceService.save(invoice);
                session.setAttribute(Constant.MSG_SUCCESS, "Create success!");
            } catch (Exception e) {
                session.setAttribute(Constant.MSG_ERROR, "Create error");
                throw new RuntimeException(e);
            }
        }

        return "redirect:/goods-receipt/list";
    }

    @GetMapping("/edit/{id}")
    public String edit(Model model , @PathVariable("id") int id) {
        Invoice invoice = invoiceService.find("id",id).get(0);
        if(invoice!=null) {
            invoice.setProductId(invoice.getProductInfo().getId());
            invoice.setProductName(invoice.getProductInfo().getName());

            model.addAttribute("titlePage", "Edit Invoice");
            model.addAttribute("modelForm", invoice);
            model.addAttribute("viewOnly", false);
            model.addAttribute("view", "/invoice/goodsReceipt/goodsReceipt-action");

            return "index";
        }
        return "redirect:/goods-receipt/list";
    }

    @GetMapping("/export")
    public ModelAndView exportReport() {
        ModelAndView modelAndView = new ModelAndView();
        Invoice invoice = new Invoice();
        invoice.setType(Constant.TYPE_GOODS_RECEIPT);
        List<Invoice> invoices = invoiceService.getList(invoice, null);
        modelAndView.addObject(Constant.KEY_GOODS_RECEIPT_REPORT, invoices);
        modelAndView.setView(new GoodsReceiptReport());
        return modelAndView;
    }
}
