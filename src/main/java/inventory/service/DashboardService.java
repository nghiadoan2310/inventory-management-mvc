package inventory.service;

import inventory.dao.CategoryDAO;
import inventory.dao.InvoiceDAO;
import inventory.dao.ProductInStockDAO;
import inventory.dao.UserDAO;
import inventory.model.Category;
import inventory.model.Invoice;
import inventory.model.ProductInStock;
import inventory.model.User;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.*;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class DashboardService {
    static Logger log = LogManager.getLogger(DashboardService.class);

    InvoiceDAO<Invoice> invoiceDAO;
    CategoryDAO<Category> categoryDAO;
    ProductInStockDAO<ProductInStock> productInStockDAO;
    UserDAO<User> userDAO;

    public Map<String, Object> getLast6MonthRevenue() {
        Map<String, Object> chartData = new HashMap<>();
        List<String> labels = new ArrayList<>(); //Danh sách 6 tháng gần nhất
        List<Double> data = new ArrayList<>(); //Doanh thu 6 tháng gần nhất

        List<Object[]> results = invoiceDAO.getRevenueLast6Month();
        results.forEach(row -> {
            int month = Integer.parseInt(row[0].toString().split("-")[1]);
            double total = ((Number) row[1]).doubleValue();

            labels.add(Month.of(month).getDisplayName(TextStyle.SHORT, Locale.ENGLISH)); //Lấy ra tên tháng VD: 11 -> Nov (November)
            data.add(total);
        });

        chartData.put("labels", labels);
        chartData.put("data", data);
        return chartData;
    }

    public Map<String, Object> getCategoryPercent() {
        Map<String, Object> chartData = new HashMap<>();
        List<String> labels = new ArrayList<>(); //Danh sách tên loại sản phẩm
        List<Double> data = new ArrayList<>(); //Phần trăm số sản phẩm các loại

        List<Object[]> results = categoryDAO.getCategoryPercent();
        results.forEach(row -> {
            double percent = ((Number) row[1]).doubleValue();

            labels.add(row[0].toString());
            data.add(percent);
        });


        chartData.put("labels", labels);
        chartData.put("data", data);
        return chartData;
    }

    public Map<String, Object> getProductReceiptCard() {
        Map<String, Object> cardData = new HashMap<>();

        BigInteger data = invoiceDAO.getProductTotalAMonth(1).toBigInteger();
        BigInteger productLastMonth = invoiceDAO.getProductTotalLastMonth(1).toBigInteger();

//        if (data == null) data = BigInteger.ZERO;
//        if (productLastMonth == null) productLastMonth = BigInteger.ZERO;

        int percent;

        if (productLastMonth.equals(BigInteger.ZERO)) {
            percent = data.equals(BigInteger.ZERO) ? 0 : 100;
        } else {
            BigInteger diff = data.subtract(productLastMonth); // data - last
            percent  = diff.multiply(BigInteger.valueOf(100)) // *100
                    .divide(productLastMonth).intValue(); // / last
        }

        cardData.put("data", data);
        cardData.put("percent", percent);

        return cardData;
    }

    public Map<String, Object> getProductIssueCard() {
        Map<String, Object> cardData = new HashMap<>();

        BigInteger data = invoiceDAO.getProductTotalAMonth(2).toBigInteger();
        BigInteger productLastMonth = invoiceDAO.getProductTotalLastMonth(2).toBigInteger();

        int percent;

        if (productLastMonth.equals(BigInteger.ZERO)) {
            percent = data.equals(BigInteger.ZERO) ? 0 : 100;
        } else {
            BigInteger diff = data.subtract(productLastMonth); // data - last
            percent  = diff.multiply(BigInteger.valueOf(100)) // *100
                    .divide(productLastMonth).intValue(); // / last
        }

        cardData.put("data", data);
        cardData.put("percent", percent);

        return cardData;
    }

    public BigDecimal getProductTotalInStock() {
        BigDecimal total = productInStockDAO.getProductTotal();

        if (total != null) {
            return total;
        } else {
            return BigDecimal.valueOf(0);
        }
    }

    public int getUserTotal() {
        return userDAO.getUserTotal().intValue();
    }
}
