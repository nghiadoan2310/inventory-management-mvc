package inventory.service;

import inventory.dao.CategoryDAO;
import inventory.dao.ProductInfoDAO;
import inventory.model.Category;
import inventory.model.Paging;
import inventory.model.ProductInfo;
import inventory.util.ConfigLoader;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ProductService {
    static Logger log = LogManager.getLogger(ProductService.class);

    CategoryDAO<Category> categoryDAO;
    ProductInfoDAO<ProductInfo> productInfoDAO;

    public void saveCategory(Category category) throws Exception {
        log.info("Insert category: " + category.toString());
        category.setActiveFlag(1);
        category.setCreateDate(new Date());
        category.setUpdateDate(new Date());
        categoryDAO.save(category);
    }

    public void updateCategory(Category category) throws Exception {
        category.setUpdateDate(new Date());
        categoryDAO.update(category);
    }

    public void deleteCategory(Category category) throws Exception {
        category.setActiveFlag(0);
        category.setUpdateDate(new Date());
        categoryDAO.update(category);
    }

    public List<Category> findCategory(String name, Object value) {
        return categoryDAO.findByProperty(name, value);
    }

    public List<Category> getAllCategory(String keyword, Paging paging) {
        StringBuilder queryStr = new StringBuilder();
        Map<String, Object> mapParams = new HashMap<>();

        if (StringUtils.hasText(keyword)) {
            queryStr.append(" and model.code LIKE concat('%', :keyword, '%') or model.name LIKE concat('%', :keyword, '%')");
            mapParams.put("keyword", keyword);
        }

        return categoryDAO.findAll(queryStr.toString(), mapParams, paging);
    }

    public Category findByIdCategory(int id) {
        return categoryDAO.findById(Category.class, id);
    }

    //ProductInfo

    public void saveProductInfo(ProductInfo productInfo) throws Exception {
        log.info("Insert productInfo: " + productInfo.toString());
        productInfo.setActiveFlag(1);
        productInfo.setCreateDate(new Date());
        productInfo.setUpdateDate(new Date());

        String filename = processUploadFile(productInfo.getMultipartFile());
        if(filename != null) {
            productInfo.setImgUrl("/upload/" + filename);
        }

        productInfoDAO.save(productInfo);
    }

    public void updateProductInfo(ProductInfo productInfo) throws Exception {
        productInfo.setUpdateDate(new Date());

        String filename = processUploadFile(productInfo.getMultipartFile());
        if(filename != null) {
            productInfo.setImgUrl("/upload/" + filename);
        }

        productInfoDAO.update(productInfo);
    }

    public void deleteProductInfo(ProductInfo ProductInfo) throws Exception {
        ProductInfo.setActiveFlag(0);
        ProductInfo.setUpdateDate(new Date());
        productInfoDAO.update(ProductInfo);
    }

    public List<ProductInfo> findProductInfo(String name, Object value) {
        return productInfoDAO.findByProperty(name, value);
    }

    public List<ProductInfo> getAllProductInfo(String keyword, Paging paging) {
        StringBuilder queryStr = new StringBuilder();
        Map<String, Object> mapParams = new HashMap<>();

        if (StringUtils.hasText(keyword)) {
            queryStr.append(" and model.code LIKE concat('%', :keyword, '%') or model.name LIKE concat('%', :keyword, '%')");
            mapParams.put("keyword", keyword);
        }

        return productInfoDAO.findAll(queryStr.toString(), mapParams, paging);
    }

    public ProductInfo findByIdProductInfo(int id) {
        return productInfoDAO.findById(ProductInfo.class, id);
    }

    private String processUploadFile(MultipartFile multipartFile) throws IOException {
        if(multipartFile != null && !multipartFile.isEmpty()) {
            Path folder = Paths.get(ConfigLoader.getInstance().getValue("upload.location"));

            //Lấy phần mở rộng của file (png, pdf, ...)
            String fileExtension = StringUtils.getFilenameExtension(multipartFile.getOriginalFilename());

            //Tạo file name với UUID
            String fileName = ObjectUtils.isEmpty(fileExtension) ? UUID.randomUUID().toString() :
                    UUID.randomUUID() + "." + fileExtension;

            //Tạo đường dẫn của ảnh
            Path filePath = folder.resolve(fileName) //ghép đường dẫn folder với filename để được đường dẫn đến file
                    .normalize() //chuẩn hoá đường dẫn, loại bỏ các phần tử thừa như "." (thư mục hiện tại) và ".." (thư mục cha).
                    .toAbsolutePath(); //Chuyển đường dẫn tương đối thành đường dẫn tuyệt đối

            //Copy file vào folder với việc chèn file nếu file đã tồn tại
            Files.copy(multipartFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            return fileName;
        }

        return null;
    }
}
