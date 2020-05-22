package data;

import java.io.*;

import static common.Utility.error;
import static data.Reference.DATA_DIR;

public class Serialization<T> {

    /**
     * 将object对象序列化并写入filename文件中，处理了异常
     * @param object 要序列化的对象
     * @param filename 要写入的文件
     */
    public void serialize(T object, String filename) {
        try {
            this.$serialize(object, filename);
        } catch (IOException e) {
            System.err.println("文件读写被拒");
            error("文件读写被拒，请将尝试在非系统盘运行程序！");
        }
    }

    /**
     * 对象序列化的实际实现
     * @param object 要序列化的对象
     * @param filename 要写入的文件
     * @throws IOException 文件读写异常
     */
    private void $serialize(T object, String filename) throws IOException {
        ObjectOutputStream stream = new ObjectOutputStream(new FileOutputStream(new File(DATA_DIR, filename)));
        stream.writeObject(object);
        stream.close();
    }

    /**
     * 将filename中的字符反序列化并返回一个对象
     * @param filename 反序列化来源
     * @return 反序列化后得到的对象
     * @throws EOFException 文件为空 根据情况继续处理
     */
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

    /**
     * 对象反序列化的实际实现
     * @param filename 反序列化来源
     * @return 反序列化后得到的对象
     * @throws IOException 文件读写异常
     * @throws ClassNotFoundException 找不到类 正常情况下应该不会出现
     */
    private T $deserialize(String filename) throws IOException, ClassNotFoundException {
        ObjectInputStream stream = new ObjectInputStream(new FileInputStream(new File(DATA_DIR, filename)));
        T object = (T) stream.readObject();
        stream.close();
        return object;
    }
}
