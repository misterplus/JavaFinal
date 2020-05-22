package data;

import java.io.*;

import static common.Utility.error;
import static data.Reference.DATA_DIR;

public class Serialization<T> {

    public void serialize(T object, String filename) {
        try {
            this.$serialize(object, filename);
        } catch (IOException e) {
            System.err.println("�ļ���д����");
            error("�ļ���д���ܣ��뽫�����ڷ�ϵͳ�����г���");
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
            System.err.println("�������л�����");
            error("�������л�������ɾ��data�ļ����е�" + filename + "�ļ������������");
        } catch (IOException e) {
            System.err.println("�ļ���д����");
            error("�ļ���д���ܣ��뽫�����ڷ�ϵͳ�����г���");
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
