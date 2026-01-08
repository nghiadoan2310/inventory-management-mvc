package inventory.service;

import inventory.model.Invoice;
import inventory.util.Constant;
import inventory.util.FormatDate;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.http.HttpHeaders;
import org.springframework.web.servlet.view.document.AbstractXlsxView;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class GoodsReceiptReport extends AbstractXlsxView {

    @Override
    protected void buildExcelDocument(Map<String, Object> model, Workbook workbook, HttpServletRequest request,
                                      HttpServletResponse response) throws Exception {
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=goods-receipt-export.xlsx");
        Sheet sheet = workbook.createSheet("data");
        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("#");
        headerRow.createCell(1).setCellValue("Code");
        headerRow.createCell(2).setCellValue("Product");
        headerRow.createCell(3).setCellValue("Qty");
        headerRow.createCell(4).setCellValue("Price");
        headerRow.createCell(5).setCellValue("Update date");

        @SuppressWarnings("unchecked")
        List<Invoice> invoices = (List<Invoice>) model.get(Constant.KEY_GOODS_RECEIPT_REPORT);

       if (invoices != null && !invoices.isEmpty()) {
           AtomicInteger rowNum= new AtomicInteger(1);
           invoices.forEach(invoice -> {
               Row row = sheet.createRow(rowNum.getAndIncrement());
               row.createCell(0).setCellValue(rowNum.intValue() -1);
               row.createCell(1).setCellValue(invoice.getCode());
               row.createCell(2).setCellValue(invoice.getQty());
               row.createCell(3).setCellValue(invoice.getPrice().toString());
               row.createCell(4).setCellValue(invoice.getProductInfo().getName());
               row.createCell(5).setCellValue(FormatDate.dateToString(invoice.getUpdateDate()));
           });
       }
    }
}
