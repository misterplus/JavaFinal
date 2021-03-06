import common.Credentials;
import data.Serialization;
import frame.LoginFrame;

import java.io.EOFException;
import java.io.IOException;

import static common.Utility.error;
import static common.Utility.info;
import static data.Reference.init;

public class MoneyManager {
    public static void main(String[] args) {
        try {
            init();
            new LoginFrame(new Serialization<Credentials>().deserialize("pwd.txt"));
        } catch (EOFException e) { //凭据文件为空 传入空凭据
            System.err.println("凭据有误或不存在，准备进行注册");
            info("未找到存在的凭据，本次登录将自动进行注册！");
            new LoginFrame(new Credentials());
        } catch (IOException e) {
            System.err.println("文件读写被拒");
            error("文件读写被拒，请将尝试在非系统盘运行程序！");
        }
    }
}