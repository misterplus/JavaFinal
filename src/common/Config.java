package common;

import data.Serialization;

import java.io.Serializable;

public class Config implements Serializable {
    private static final long serialVersionUID = 3L;
    private long id = 1;

    public String getId() {
        return String.valueOf(id);
    }

    //每次更新编号后都进行保存
    public void increment() {
        id++;
        new Serialization<Config>().serialize(this, "config.txt");
    }

    @Override
    public String toString() {
        return "Config{" +
                "id=" + id +
                '}';
    }
}
