package frame;

import common.Credentials;
import data.Serialization;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;

import static common.Utility.error;
import static common.Utility.info;

public class ModifyPwdFrame extends JFrame implements ActionListener {
    private JPasswordField t_oldPWD, t_newPWD, t_newPWDAgain;
    private JButton b_ok, b_cancel;
    private Credentials credentials;

    public ModifyPwdFrame(Credentials credentials) {
        super("�޸�����");
        this.credentials = credentials;
        JLabel l_oldPWD = new JLabel("        �����룺");
        JLabel l_newPWD = new JLabel("        �����룺");
        JLabel l_newPWDAgain = new JLabel("ȷ�������룺");
        t_oldPWD = new JPasswordField(15);
        t_newPWD = new JPasswordField(15);
        t_newPWDAgain = new JPasswordField(15);
        b_ok = new JButton("ȷ��");
        b_cancel = new JButton("ȡ��");
        Container c = this.getContentPane();
        c.setLayout(new FlowLayout());
        c.add(l_oldPWD);
        c.add(t_oldPWD);
        c.add(l_newPWD);
        c.add(t_newPWD);
        c.add(l_newPWDAgain);
        c.add(t_newPWDAgain);
        c.add(b_ok);
        c.add(b_cancel);
        b_ok.addActionListener(this);
        b_cancel.addActionListener(this);
        this.setResizable(false);
        this.setSize(280, 160);
        Dimension screen = this.getToolkit().getScreenSize();
        this.setLocation((screen.width - this.getSize().width) / 2, (screen.height - this.getSize().height) / 2);
        this.setVisible(true);
    }

    public void actionPerformed(ActionEvent e) {
        if (b_cancel == e.getSource()) {
            dispose();
        } else if (b_ok == e.getSource()) {
            if (Arrays.equals(t_newPWD.getPassword(), t_newPWDAgain.getPassword())) {
                if (t_newPWD.getPassword().length > 6) {
                    if (this.credentials.validate(this.credentials.getUsername(), String.valueOf(t_oldPWD.getPassword()))) {
                        this.credentials.setPassword(String.valueOf(t_newPWD.getPassword()));
                        new Serialization<Credentials>().serialize(this.credentials, "pwd.txt");
                        info("�޸ĳɹ���");
                        dispose();
                    } else {
                        error("���������");
                    }
                } else {
                    error("�����벻������6λ��");
                }
            } else {
                error("�����벻һ�£�");
            }
        }
    }
}
