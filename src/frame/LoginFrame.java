package frame;

import common.Credentials;
import common.IAS;
import data.Serialization;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.EOFException;
import java.util.LinkedList;
import java.util.List;

import static common.Utility.error;

public class LoginFrame extends JFrame implements ActionListener {
    private JTextField t_user;
    private JPasswordField t_pwd;
    private JButton b_ok, b_cancel;
    private Credentials credentials;

    public LoginFrame(Credentials credentials) {
        super("��ӭʹ�ø�������˱�!");
        this.credentials = credentials;
        JLabel l_user = new JLabel("�û�����", JLabel.RIGHT);
        JLabel l_pwd = new JLabel("    ���룺", JLabel.RIGHT);
        t_user = new JTextField(31);
        t_pwd = new JPasswordField(31);
        b_ok = new JButton("��¼");
        b_cancel = new JButton("�˳�");

        Container c = this.getContentPane();
        c.setLayout(new FlowLayout());
        c.add(l_user);
        c.add(t_user);
        c.add(l_pwd);
        c.add(t_pwd);
        c.add(b_ok);
        c.add(b_cancel);

        b_ok.addActionListener(this);
        b_cancel.addActionListener(this);

        this.setResizable(false);
        this.setSize(455, 150);

        Dimension screen = this.getToolkit().getScreenSize();
        this.setLocation((screen.width - this.getSize().width) / 2, (screen.height - this.getSize().height) / 2);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);
    }

    public void actionPerformed(ActionEvent e) {
        if (b_cancel == e.getSource()) {
            dispose();
            System.exit(0);
        } else if (b_ok == e.getSource()) {
            if (credentials.isNew()) { //ƾ��Ϊ�� ��������û����������¼Ϊ��ʼƾ��
                if (t_user.getText().isEmpty()) { //�û�����Ϊ��
                    error("�û�������Ϊ�գ�");
                } else if (t_pwd.getPassword().length < 6) { //��������6λ
                    error("���벻������6λ��");
                } else {
                    credentials = new Credentials(t_user.getText(), String.valueOf(t_pwd.getPassword()));
                    new Serialization<Credentials>().serialize(credentials, "pwd.txt"); //�����ʼƾ��
                    login();
                }
            } else {
                if (credentials.validate(t_user.getText(), String.valueOf(t_pwd.getPassword()))) { //��֤ƾ��
                    login();
                } else {
                    error("�û����������");
                }
            }
        }
    }

    /**
     * ��¼��ʵ��
     * �������ļ������л�Ϊ�������MainFrame���췽��
     * ͬʱ����ƾ�ݣ����޸�����ʱ��֤�Լ���ʾ���ڱ���
     */
    private void login() {
        try {
            new MainFrame(this.credentials, new Serialization<List<IAS>>().deserialize("data.txt"));
        } catch (EOFException ex) {
            System.err.println("����Ϊ�ջ����󣬴�������ݱ�");
            new MainFrame(this.credentials, new LinkedList<>());
        }
    }
}