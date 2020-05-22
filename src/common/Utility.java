package common;

import javax.swing.*;

public class Utility {
    public static void error(String message) {
        JOptionPane.showMessageDialog(null, message, "����", JOptionPane.ERROR_MESSAGE);
    }

    public static void info(String message) {
        JOptionPane.showMessageDialog(null, message, "��ʾ", JOptionPane.INFORMATION_MESSAGE);
    }

    public static boolean confirm(String message) {
        return JOptionPane.showConfirmDialog(null, message, "ȷ��", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION;
    }
}
