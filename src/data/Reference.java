package data;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class Reference {
    public static final String DATA_DIR = "data";
    public static final List<File> FILES = new LinkedList<File>() {{
        add(new File(DATA_DIR, "pwd.txt"));
        add(new File(DATA_DIR, "data.txt"));
        add(new File(DATA_DIR, "config.txt"));
    }};

    public static void init() throws IOException {
        for (File file : FILES) {
            if (!file.exists())
                file.createNewFile();
        }
    }
}
