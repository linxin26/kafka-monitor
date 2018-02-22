package co.solinx.kafka.monitor.utils;

import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.file.*;

/**
 * Created by linx on 2016-03-08.
 * 加载 JSON 文件
 */
public class JsonLoader {

    static Logger logger = LoggerFactory.getLogger(JsonLoader.class);

    public static JSONObject loadJSONFile(InputStream inputStream) {

        JSONObject confJSONObj;
        try {
            byte[] buf = new byte[1024];
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            while (inputStream.available() > 0) {
                int readSize = inputStream.read(buf, 0, buf.length);
                if (readSize > 0) {
                    baos.write(buf, 0, readSize);
                }
            }
            if (baos.size() > 0) {
                confJSONObj = JSONObject.parseObject(baos.toString("UTF-8"));
            } else {
                confJSONObj = JSONObject.parseObject("{}");
            }
        } catch (Exception e) {
            logger.error(" load JSON file err {} ", e);
            confJSONObj = JSONObject.parseObject("{}");
        }
        return confJSONObj;
    }

    public static JSONObject loadJSONFile(String path) {
        logger.debug(" file path {} ", path);
        JSONObject confJSONObj = new JSONObject();
        try {
            if (!path.isEmpty()) {
                Path paths = Paths.get(path);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                if (Files.exists(paths, LinkOption.NOFOLLOW_LINKS)) {
                    InputStream inputStream = Files.newInputStream(paths, StandardOpenOption.READ);
                    byte[] buf = new byte[1024];
                    while (inputStream.available() > 0) {
                        int readSize = inputStream.read(buf, 0, buf.length);
                        if (readSize > 0) {
                            baos.write(buf, 0, readSize);
                        }
                    }
                    if (baos.size() > 0) {
                        confJSONObj = JSONObject.parseObject(baos.toString("UTF-8"));
                    } else {
                        confJSONObj = JSONObject.parseObject("{}");
                    }
                }
            }
        } catch (Exception e) {
            logger.error(" load JSON file err {} ", e);
            confJSONObj = JSONObject.parseObject("{}");
        }
        logger.debug("{}", confJSONObj);
        return confJSONObj;
    }
}
