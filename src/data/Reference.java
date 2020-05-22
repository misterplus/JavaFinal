package data;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class Reference {
    public static final String DATA_DIR = "data";

    /***
     * 数据文件初始化
     * 如果某个文件不存在，则创建它
     * @throws IOException 文件读写异常
     */
    public static void init() throws IOException {
        List<File> files = new LinkedList<File>() {{
            add(new File(DATA_DIR, "pwd.txt"));
            add(new File(DATA_DIR, "data.txt"));
            add(new File(DATA_DIR, "config.txt"));
        }};
        for (File file : files) {
            if (!file.exists())
                file.createNewFile();
        }
    }
}
