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
    private JMenuItem[] mI = {new JMenuItem("密码重置"), new JMenuItem("退出系统")};
    private JMenuItem m_FMEdit = new JMenuItem("收支编辑");
    private JTextField t_fromdate, t_todate;
    private JButton b_select1;
    private JLabel l_bal;
    private JComboBox<String> c_type;
    private JTable table;
    private Credentials credentials;

    public MainFrame(Credentials credentials, List<IAS> data) {
        super(credentials.getUsername() + ",欢迎使用个人理财账本!");
        this.credentials = credentials;
        Container c = this.getContentPane();
        c.setLayout(new BorderLayout());
        JMenuBar mb = new JMenuBar();
        c.add(mb, "North");
        JMenu m_system = new JMenu("系统管理");
        mb.add(m_system);
        JMenu m_fm = new JMenu("收支管理");
        mb.add(m_fm);
        m_system.add(mI[0]);
        m_system.add(mI[1]);
        m_fm.add(m_FMEdit);
        m_FMEdit.addActionListener(this);
        mI[0].addActionListener(this);
        mI[1].addActionListener(this);

        JLabel l_type = new JLabel("收支类型：");
        c_type = new JComboBox<>(new String[]{"收入/支出", "收入", "支出"});
        JLabel l_fromdate = new JLabel("起始时间");
        t_fromdate = new JTextField(8);
        JLabel l_todate = new JLabel("终止时间");
        t_todate = new JTextField(8);
        b_select1 = new JButton("查询");
        JLabel l_ps = new JLabel("注意：时间格式为YYYYMMDD，例如：20150901");
        JPanel p_condition = new JPanel();
        p_condition.setLayout(new GridLayout(3, 1));
        p_condition.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("输入查询条件"),
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
                BorderFactory.createTitledBorder("收支明细信息"),
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
                System.err.println("数据为空或有误，传入空配置文件");
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
            l_bal.setText("个人总收支余额为" + balance + "元。您已超支，请适度消费！");
        else
            l_bal.setText("个人总收支余额为" + balance + "元。");
    }
}
