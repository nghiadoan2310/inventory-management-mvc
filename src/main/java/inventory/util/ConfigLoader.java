package inventory.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

//Xây dụng loader để load các giá trị trong file properties
public class ConfigLoader {
    private Properties properties = null;
    private static ConfigLoader instance = null;
    String propertiesFilename = "config.properties";
    private ConfigLoader() {
        //Tạo luồng đọc dữ liệu từ properties file
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(propertiesFilename);

        if(inputStream != null) {
            properties = new Properties();
            try {
                properties.load(inputStream); //đọc dữ liệu từ luồng dữ liệu nếu tồn tại file properties
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    //Hàm khởi tạo configLoader
    public static ConfigLoader getInstance() {
        //Nếu hệ thống chưa khởi tạo
        if(instance == null) {
            //Xử lý đa luồng, đảm bảo chỉ có 1 inputStream khi 2 hay nhiều người dùng cùng khởi tạo cùng lúc
            synchronized (ConfigLoader.class) {
                instance = new ConfigLoader(); //khởi tạo configLoader để tạo luồng inputStream
            }
        }

        return instance;
    }

    //Lấy giá trị của key được đưa vào
    public String getValue(String key) {
        //Nếu có key trong file properties
        if(properties.containsKey(key) ) {
            return properties.getProperty(key); //Lấy giá trị
        }

        return null;
    }
}
