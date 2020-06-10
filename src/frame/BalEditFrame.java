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
    private static final JComboBox<String> IN = new JComboBox<>(new String[]{"����", "����", "����", "����"}); //��������
    private static final JComboBox<String> OUT = new JComboBox<>(new String[]{"����", "����", "�Ӽ�", "��ͨ", "����", "����", "����"}); //֧������
    private JTextField t_id, t_date, t_bal;
    private JComboBox<String> c_type, c_item;
    private JButton b_update, b_delete, b_new, b_clear;
    private JTable table;

    private Config config;

    public BalEditFrame(JTable t, Config config) {
        super("��֧�༭");
        this.config = config;
        JLabel l_id = new JLabel("��ţ�");
        JLabel l_date = new JLabel("���ڣ�");
        JLabel l_bal = new JLabel("��");
        JLabel l_type = new JLabel("���ͣ�");
        JLabel l_item = new JLabel("���ݣ�");
        t_id = new JTextField(8);
        t_date = new JTextField(8);
        t_bal = new JTextField(8);
        t_id.setText(this.config.getId()); //��Ų����޸� ֱ�ӴӼ�¼��ȡ
        t_id.setEditable(false);

        c_type = new JComboBox<>(new String[]{"����", "֧��"});
        c_item = new JComboBox<>(new String[]{"����", "����", "����", "����"});

        b_update = new JButton("�޸�");
        b_delete = new JButton("ɾ��");
        b_new = new JButton("¼��");
        b_clear = new JButton("���");

        Container c = this.getContentPane();
        c.setLayout(new BorderLayout());

        JPanel p1 = new JPanel();
        p1.setLayout(new GridLayout(5, 2, 10, 10));
        p1.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("�༭��֧��Ϣ"),
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
                BorderFactory.createTitledBorder("��ʾ��֧��Ϣ"),
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
        table.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION); //ֻ��ѡ��һ�м�¼
        table.getSelectionModel().addListSelectionListener(e -> { //��������ѡ���¼� �Զ���д����ı���
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
                clearFields(); //ɾ����¼�����ñ�Ų�����ı��� ����Ҫ��
            } else if (b_new == e.getSource()) {
                model.add(getNewIAS());
                this.config.increment(); //������¼���ż�һ
                this.t_id.setText(this.config.getId());
                info("��ӳɹ���");
            } else if (b_clear == e.getSource()) {
                clearFields();
            } else if (c_type == e.getSource()) {
                switch (c_type.getSelectedIndex()) { //��������/֧���������ѡ�� ���Ŀ�ѡ���
                    case 0: {
                        c_item.setModel(IN.getModel());
                        break;
                    }
                    case 1: {
                        c_item.setModel(OUT.getModel());
                    }
                }
            }
            SwingUtilities.invokeLater(() -> table.updateUI()); //���±��
        } catch (NullPointerException ex) {
            System.err.println("�����ڸ�ʽ������null");
        } catch (NumberFormatException ex) {
            System.err.println("����ʽ����");
            error("����ʽ����");
        } catch (DuplicationException ex) {
            System.err.println("����ظ�");
            error(ex.getMessage());
            clearFields();
        } catch (IndexOutOfBoundsException ex) {
            System.err.println("������ӵ�иñ�ŵ���֧��¼");
            error("�����ڸ���֧��¼��");
        }
    }

    /**
     * ���ı����������������װһ������֧��¼����
     * @return ��װ��Ķ���
     * @throws NullPointerException ���ڲ�����ʱ���׳�NPE ���Լ���
     */
    private IAS getNewIAS() throws NullPointerException {
        return new IAS(Long.parseLong(t_id.getText()), Double.parseDouble(t_bal.getText()), IAS.parseDate(t_date.getText()), (String) c_type.getSelectedItem(), (String) c_item.getSelectedItem());
    }

    /**
     * ����ı������ñ��
     */
    private void clearFields() {
        this.t_bal.setText("");
        this.t_date.setText("");
        this.t_id.setText(this.config.getId());
    }

}