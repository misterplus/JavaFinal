import common.Credentials;
import data.Serialization;
import frame.LoginFrame;

import javax.swing.*;
import java.io.EOFException;
import java.io.IOException;

import static common.Utility.error;
import static common.Utility.info;
import static data.Reference.init;

public class MoneyManager {

    public static void main(String[] args) {
        LoginFrame lf;
        try {
            init();
            lf = new LoginFrame(new Serialization<Credentials>().deserialize("pwd.txt"));
        } catch (EOFException e) {
            System.err.println("ƾ������򲻴��ڣ�׼������ע��");
            info("δ�ҵ����ڵ�ƾ�ݣ����ε�¼���Զ�����ע�ᣡ");
            lf = new LoginFrame(new Credentials());
        } catch (IOException e) {
            System.err.println("�ļ���д����");
            error("�ļ���д���ܣ��뽫�����ڷ�ϵͳ�����г���");
            return;
        }
        lf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}