package frame;

import common.Config;
import common.IAS;
import data.IASTableModel;
import exception.DuplicationException;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import static common.Utility.error;
import static common.Utility.info;

public class BalEditFrame extends JFrame implements ActionListener {
    private static final JComboBox<String> IN = new JComboBox<>(new String[]{"人情", "工资", "奖金", "其他"}); //收入种类
    private static final JComboBox<String> OUT = new JComboBox<>(new String[]{"购物", "餐饮", "居家", "交通", "娱乐", "人情", "其他"}); //支出种类
    private JTextField t_id, t_date, t_bal;
    private JComboBox<String> c_type, c_item;
    private JButton b_update, b_delete, b_new, b_clear;
    private JTable table;

    private Config config;

    public BalEditFrame(JTable t, Config config) {
        super("收支编辑");
        this.config = config;
        JLabel l_id = new JLabel("编号：");
        JLabel l_date = new JLabel("日期：");
        JLabel l_bal = new JLabel("金额：");
        JLabel l_type = new JLabel("类型：");
        JLabel l_item = new JLabel("内容：");
        t_id = new JTextField(8);
        t_date = new JTextField(8);
        t_bal = new JTextField(8);
        t_id.setText(this.config.getId()); //编号不可修改 直接从记录读取
        t_id.setEditable(false);

        c_type = new JComboBox<>(new String[]{"收入", "支出"});
        c_item = new JComboBox<>(new String[]{"人情", "工资", "奖金", "其他"});

        b_update = new JButton("修改");
        b_delete = new JButton("删除");
        b_new = new JButton("录入");
        b_clear = new JButton("清空");

        Container c = this.getContentPane();
        c.setLayout(new BorderLayout());

        JPanel p1 = new JPanel();
        p1.setLayout(new GridLayout(5, 2, 10, 10));
        p1.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("编辑收支信息"),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        p1.add(l_id);
        p1.add(t_id);
        p1.add(l_date);
        p1.add(t_date);
        p1.add(l_type);
        p1.add(c_type);
        p1.add(l_item);
        p1.add(c_item);
        p1.add(l_bal);
        p1.add(t_bal);
        c.add(p1, BorderLayout.WEST);

        JPanel p2 = new JPanel();
        p2.setLayout(new GridLayout(4, 1, 10, 10));
        p2.add(b_new);
        p2.add(b_update);
        p2.add(b_delete);
        p2.add(b_clear);

        c.add(p2, BorderLayout.CENTER);

        JPanel p3 = new JPanel();
        p3.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("显示收支信息"),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)));

        table = new JTable(t.getModel());
        table.getTableHeader().setReorderingAllowed(false);
        table.getTableHeader().setResizingAllowed(false);
        JScrollPane scrollpane = new JScrollPane(table);
        scrollpane.setViewportView(table);
        scrollpane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        p3.add(scrollpane);
        c.add(p3, BorderLayout.EAST);

        b_update.addActionListener(this);
        b_delete.addActionListener(this);
        b_new.addActionListener(this);
        b_clear.addActionListener(this);
        c_type.addActionListener(this);
        table.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION); //只能选中一行记录
        table.getSelectionModel().addListSelectionListener(e -> { //监听更改选中事件 自动填写左侧文本框
            IASTableModel model = (IASTableModel) table.getModel();
            if (model.isNull(table.getSelectedRow()))
                return;
            IAS ias = model.getValueAt(table.getSelectedRow());
            t_id.setText(String.valueOf(ias.getId()));
            t_date.setText(ias.getDate());
            t_bal.setText(String.valueOf(ias.getAmount()));
            c_type.setSelectedItem(ias.getType());
            c_item.setSelectedItem(ias.getCategory());
        });

        this.setResizable(false);
        this.setSize(800, 300);
        Dimension screen = this.getToolkit().getScreenSize();
        this.setLocation((screen.width - this.getSize().width) / 2, (screen.height - this.getSize().height) / 2);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.setVisible(true);
    }

    public void actionPerformed(ActionEvent e) {
        try {
            IASTableModel model = ((IASTableModel) this.table.getModel());
            if (b_update == e.getSource()) {
                model.update(table.getSelectedRow(), getNewIAS());
            } else if (b_delete == e.getSource()) {
                model.delete(table.getSelectedRow());
                clearFields(); //删除记录后重置编号并清空文本框 符合要求
            } else if (b_new == e.getSource()) {
                model.add(getNewIAS());
                this.config.increment(); //新增记录后编号加一
                this.t_id.setText(this.config.getId());
                info("添加成功！");
            } else if (b_clear == e.getSource()) {
                clearFields();
            } else if (c_type == e.getSource()) {
                switch (c_type.getSelectedIndex()) { //根据收入/支出下拉框的选择 更改可选类别
                    case 0: {
                        c_item.setModel(IN.getModel());
                        break;
                    }
                    case 1: {
                        c_item.setModel(OUT.getModel());
                    }
                }
            }
            SwingUtilities.invokeLater(() -> table.updateUI()); //更新表格
        } catch (NullPointerException ex) {
            System.err.println("因日期格式错误传入null");
        } catch (NumberFormatException ex) {
            System.err.println("金额格式错误");
            error("金额格式错误！");
        } catch (DuplicationException ex) {
            System.err.println("编号重复");
            error(ex.getMessage());
            clearFields();
        } catch (IndexOutOfBoundsException ex) {
            System.err.println("不存在拥有该编号的收支记录");
            error("不存在该收支记录！");
        }
    }

    /**
     * 从文本框和下拉框数据组装一个新收支记录对象
     * @return 组装后的对象
     * @throws NullPointerException 日期不存在时会抛出NPE 忽略即可
     */
    private IAS getNewIAS() throws NullPointerException {
        return new IAS(Long.parseLong(t_id.getText()), Double.parseDouble(t_bal.getText()), IAS.parseDate(t_date.getText()), (String) c_type.getSelectedItem(), (String) c_item.getSelectedItem());
    }

    /**
     * 清空文本框并重置编号
     */
    private void clearFields() {
        this.t_bal.setText("");
        this.t_date.setText("");
        this.t_id.setText(this.config.getId());
    }

}