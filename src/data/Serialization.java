package data;

import java.io.*;

import static common.Utility.error;
import static data.Reference.DATA_DIR;

public class Serialization<T> {

    public void serialize(T object, String filename) {
        try {
            this.$serialize(object, filename);
        } catch (IOException e) {
            System.err.println("文件读写被拒");
            error("文件读写被拒，请将尝试在非系统盘运行程序！");
        }
    }

    private void $serialize(T object, String filename) throws IOException {
        ObjectOutputStream stream = new ObjectOutputStream(new FileOutputStream(new File(DATA_DIR, filename)));
        stream.writeObject(object);
        stream.close();
    }

    public T deserialize(String filename) throws EOFException {
        try {
            return this.$deserialize(filename);
        } catch (EOFException e) {
            throw e;
        } catch (ClassNotFoundException | StreamCorruptedException e) {
            System.err.println("对象序列化出错");
            error("对象序列化出错，请删除data文件夹中的" + filename + "文件后重启软件！");
        } catch (IOException e) {
            System.err.println("文件读写被拒");
            error("文件读写被拒，请将尝试在非系统盘运行程序！");
        }
        return null;
    }

    private T $deserialize(String filename) throws IOException, ClassNotFoundException {
        ObjectInputStream stream = new ObjectInputStream(new FileInputStream(new File(DATA_DIR, filename)));
        T object = (T) stream.readObject();
        stream.close();
        return object;
    }
}
