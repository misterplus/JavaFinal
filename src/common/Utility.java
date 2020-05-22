package common;

import static javax.swing.JOptionPane.*;

//简化弹窗代码
public class Utility {

    public static void error(String message) {
        showMessageDialog(null, message, "错误", ERROR_MESSAGE);
    }

    public static void info(String message) {
        showMessageDialog(null, message, "提示", INFORMATION_MESSAGE);
    }

    public static boolean confirm(String message) {
        return showConfirmDialog(null, message, "确认", YES_NO_OPTION, QUESTION_MESSAGE) == YES_OPTION;
    }
}
