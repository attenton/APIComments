package utils;

import com.alibaba.fastjson.JSON;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collection;

public class JSONWriter {
    public static void writeModelListToJson(String filename, Collection<?> entityModelList) {
        try {
            if (!(entityModelList.size() > 0)) {
                return;
            }
            System.out.println(filename);

            Files.write(Paths.get(filename), JSON.toJSONString(entityModelList).getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
