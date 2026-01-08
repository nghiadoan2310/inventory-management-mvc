package inventory.controller;

import inventory.service.DashboardService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@Controller
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/dashboard")
public class DashboardController {

    DashboardService dashboardService;

    @GetMapping
    public String viewDashboard(Model model) {
        Object revenueLabels = dashboardService.getLast6MonthRevenue().get("labels");
        Object revenueData = dashboardService.getLast6MonthRevenue().get("data");

        //Dữ liệu biểu đồ (doanh thu 6 tháng gần nhất)
        model.addAttribute("revenueLabels", revenueLabels);
        model.addAttribute("revenueData", revenueData);

        Object categoryLabels = dashboardService.getCategoryPercent().get("labels");
        Object categoryData = dashboardService.getCategoryPercent().get("data");

        //Dữ liệu biểu đồ tròn (tỉ lệ sản phẩm theo loại)
        model.addAttribute("categoryLabels", categoryLabels);
        model.addAttribute("categoryData", categoryData);

        //Bảng hiện số lượng sản phẩm nhập trong tháng
        Object receiptInMonth = dashboardService.getProductReceiptCard().get("data");
        Object percentReceiptInMonth = dashboardService.getProductReceiptCard().get("percent");
        model.addAttribute("receiptInMonth", receiptInMonth);
        model.addAttribute("percentReceiptInMonth", percentReceiptInMonth);

        //Bảng hiện số lượng sản phẩm xuất trong tháng
        Object issueInMonth = dashboardService.getProductReceiptCard().get("data");
        Object percentIssueInMonth = dashboardService.getProductReceiptCard().get("percent");
        model.addAttribute("issueInMonth", issueInMonth);
        model.addAttribute("percentIssueInMonth", percentIssueInMonth);

        //Tổng số user
        model.addAttribute("userTotal", dashboardService.getUserTotal());

        //Tổng số lượng sản phẩm đang có trong kho
        model.addAttribute("productInStock", dashboardService.getProductTotalInStock());

        model.addAttribute("view", "/dashboard/dashboard");
        return "index";
    }
}
