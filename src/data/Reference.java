package data;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class Reference {
    public static final String DATA_DIR = "data";

    /***
     * �����ļ���ʼ��
     * ���ĳ���ļ������ڣ��򴴽���
     * @throws IOException �ļ���д�쳣
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
