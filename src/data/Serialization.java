package data;

import java.io.*;

import static common.Utility.error;
import static data.Reference.DATA_DIR;

public class Serialization<T> {

    /**
     * ��object�������л���д��filename�ļ��У��������쳣
     * @param object Ҫ���л��Ķ���
     * @param filename Ҫд����ļ�
     */
    public void serialize(T object, String filename) {
        try {
            this.$serialize(object, filename);
        } catch (IOException e) {
            System.err.println("�ļ���д����");
            error("�ļ���д���ܣ��뽫�����ڷ�ϵͳ�����г���");
        }
    }

    /**
     * �������л���ʵ��ʵ��
     * @param object Ҫ���л��Ķ���
     * @param filename Ҫд����ļ�
     * @throws IOException �ļ���д�쳣
     */
    private void $serialize(T object, String filename) throws IOException {
        ObjectOutputStream stream = new ObjectOutputStream(new FileOutputStream(new File(DATA_DIR, filename)));
        stream.writeObject(object);
        stream.close();
    }

    /**
     * ��filename�е��ַ������л�������һ������
     * @param filename �����л���Դ
     * @return �����л���õ��Ķ���
     * @throws EOFException �ļ�Ϊ�� ���������������
     */
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

    /**
     * �������л���ʵ��ʵ��
     * @param filename �����л���Դ
     * @return �����л���õ��Ķ���
     * @throws IOException �ļ���д�쳣
     * @throws ClassNotFoundException �Ҳ����� ���������Ӧ�ò������
     */
    private T $deserialize(String filename) throws IOException, ClassNotFoundException {
        ObjectInputStream stream = new ObjectInputStream(new FileInputStream(new File(DATA_DIR, filename)));
        T object = (T) stream.readObject();
        stream.close();
        return object;
    }
}
