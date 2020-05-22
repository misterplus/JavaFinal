package common;

import static javax.swing.JOptionPane.*;

//�򻯵�������
public class Utility {

    public static void error(String message) {
        showMessageDialog(null, message, "����", ERROR_MESSAGE);
    }

    public static void info(String message) {
        showMessageDialog(null, message, "��ʾ", INFORMATION_MESSAGE);
    }

    public static boolean confirm(String message) {
        return showConfirmDialog(null, message, "ȷ��", YES_NO_OPTION, QUESTION_MESSAGE) == YES_OPTION;
    }
}
