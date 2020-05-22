package frame;

import common.Config;
import common.Credentials;
import common.IAS;
import data.IASTableModel;
import data.Serialization;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.EOFException;
import java.util.List;

public class MainFrame extends JFrame implements ActionListener {
    private JMenuItem[] mI = {new JMenuItem("��������"), new JMenuItem("�˳�ϵͳ")};
    private JMenuItem m_FMEdit = new JMenuItem("��֧�༭");
    private JTextField t_fromdate, t_todate;
    private JButton b_select1;
    private JLabel l_bal;
    private JComboBox<String> c_type;
    private JTable table;
    private Credentials credentials;

    public MainFrame(Credentials credentials, List<IAS> data) {
        super(credentials.getUsername() + ",��ӭʹ�ø�������˱�!");
        this.credentials = credentials;
        Container c = this.getContentPane();
        c.setLayout(new BorderLayout());
        JMenuBar mb = new JMenuBar();
        c.add(mb, "North");
        JMenu m_system = new JMenu("ϵͳ����");
        mb.add(m_system);
        JMenu m_fm = new JMenu("��֧����");
        mb.add(m_fm);
        m_system.add(mI[0]);
        m_system.add(mI[1]);
        m_fm.add(m_FMEdit);
        m_FMEdit.addActionListener(this);
        mI[0].addActionListener(this);
        mI[1].addActionListener(this);

        JLabel l_type = new JLabel("��֧���ͣ�");
        c_type = new JComboBox<>(new String[]{"����/֧��", "����", "֧��"});
        JLabel l_fromdate = new JLabel("��ʼʱ��");
        t_fromdate = new JTextField(8);
        JLabel l_todate = new JLabel("��ֹʱ��");
        t_todate = new JTextField(8);
        b_select1 = new JButton("��ѯ");
        JLabel l_ps = new JLabel("ע�⣺ʱ���ʽΪYYYYMMDD�����磺20150901");
        JPanel p_condition = new JPanel();
        p_condition.setLayout(new GridLayout(3, 1));
        p_condition.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("�����ѯ����"),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)));

        JPanel p1 = new JPanel();
        JPanel p2 = new JPanel();
        JPanel p3 = new JPanel();
        p1.add(l_type);
        p1.add(c_type);
        p2.add(l_fromdate);
        p2.add(t_fromdate);
        p2.add(l_todate);
        p2.add(t_todate);
        p2.add(b_select1);
        p3.add(l_ps);
        p_condition.add(p1);
        p_condition.add(p2);
        p_condition.add(p3);
        c.add(p_condition, "Center");

        b_select1.addActionListener(this);
        c_type.addActionListener(this);

        JPanel p_detail = new JPanel();
        p_detail.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("��֧��ϸ��Ϣ"),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        l_bal = new JLabel();
        table = new JTable(new IASTableModel(data));
        table.getTableHeader().setReorderingAllowed(false);
        JScrollPane scrollpane = new JScrollPane(table);
        scrollpane.setPreferredSize(new Dimension(580, 350));
        scrollpane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollpane.setViewportView(table);
        p_detail.add(l_bal);
        p_detail.add(scrollpane);
        c.add(p_detail, "South");
        updateBalance();
        this.setResizable(false);
        this.setSize(600, 580);
        Dimension screen = this.getToolkit().getScreenSize();
        this.setLocation((screen.width - this.getSize().width) / 2, (screen.height - this.getSize().height) / 2);
        this.setVisible(true);
    }

    public void actionPerformed(ActionEvent e) {
        Object temp = e.getSource();
        IASTableModel model = ((IASTableModel) table.getModel());
        if (temp == mI[0]) {
            new ModifyPwdFrame(this.credentials);
        } else if (temp == mI[1]) {
            dispose();
        } else if (temp == m_FMEdit) {
            BalEditFrame bef;
            try {
                bef = new BalEditFrame(table, new Serialization<Config>().deserialize("config.txt"));
            } catch (EOFException ex) {
                System.err.println("����Ϊ�ջ����󣬴���������ļ�");
                bef = new BalEditFrame(table, new Config());
            }
            bef.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    model.save();
                    updateBalance();
                    table.updateUI();
                }
            });
        } else if (temp == b_select1) {
            model.setInterval(t_fromdate.getText(), t_todate.getText());
            model.setMode(3 + c_type.getSelectedIndex());
            table.updateUI();
        } else if (temp == c_type) {
            model.setMode(c_type.getSelectedIndex());
            table.updateUI();
        }

    }

    private void updateBalance() {
        double balance = ((IASTableModel) this.table.getModel()).getBalance();
        if (balance < 0)
            l_bal.setText("��������֧���Ϊ" + balance + "Ԫ�����ѳ�֧�����ʶ����ѣ�");
        else
            l_bal.setText("��������֧���Ϊ" + balance + "Ԫ��");
    }
}
