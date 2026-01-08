package inventory.controller;

import inventory.model.History;
import inventory.model.Paging;
import inventory.model.ProductInStock;
import inventory.service.HistoryService;
import inventory.service.ProductInStockService;
import inventory.util.Constant;
import jakarta.servlet.http.HttpSession;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/history")
public class HistoryController {
    HistoryService historyService;

    @GetMapping
    public String redirect() {
        return "redirect:/history/1";
    }

    @RequestMapping("/{page}")
    public String show(Model model, @ModelAttribute("searchForm") History history,
                                   @PathVariable("page") int page) {

        Paging paging = new Paging(1);
        paging.setIndexPage(page);

        List<History> histories = historyService.getAll(history, paging);
        Map<String,String> mapType = new HashMap<>();
        Map<String, String> mapAction = new HashMap<>();

        mapType.put(String.valueOf(Constant.TYPE_ALL), "All");
        mapType.put(String.valueOf(Constant.TYPE_GOODS_RECEIPT), "Goods Receipt");
        mapType.put(String.valueOf(Constant.TYPE_GOODS_ISSUES), "Goods Issues");
        mapAction.put(Constant.ACTION_ADD, "Add");
        mapAction.put(Constant.ACTION_EDIT, "Edit");
        mapAction.put(Constant.ACTION_DELETE, "Delete");

        model.addAttribute("mapType", mapType);
        model.addAttribute("mapAction", mapAction);
        model.addAttribute("histories", histories);
        model.addAttribute("pageInfo", paging);
        model.addAttribute("view", "/history/history-list");
        return "index";
    }
}
