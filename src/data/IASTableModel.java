package data;

import common.IAS;
import exception.DuplicationException;

import javax.swing.table.AbstractTableModel;
import java.util.LinkedList;
import java.util.List;

import static common.IAS.parseDate;
import static common.IAS.parseLocalDate;
import static common.Utility.confirm;
import static common.Utility.info;

/**
 * ���������б��ʹ�õ�����ģ��IASTableModel
 * �̳���AbstractTableModel ֻ��Ҫʵ����������
 */
public class IASTableModel extends AbstractTableModel {

    private static final String[] COLUMN_NAME = {"���", "����", "����", "����", "���"};
    private List<IAS> data, display_data; //Ϊ�˱��ⷴ������ͬһ��List ��������List һ�����ڴ洢���� һ��������ʾ
    private long from, to;

    public IASTableModel(List<IAS> data) {
        this.data = data;
        this.display_data = data;
    }

    /**
     * ����¼�¼
     * @param ias Ҫ��ӵ��¼�¼�Ķ���
     * @throws DuplicationException �¼�¼�ı�������м�¼�ظ�
     */
    public void add(IAS ias) throws DuplicationException {
        if (!isDuplicate(ias.getId())) {
            this.data.add(ias);
        } else
            throw new DuplicationException(String.format("�Ѵ��ڱ��Ϊ%d����֧��¼��", ias.getId()));
    }

    /**
     * �޸����м�¼
     * @param rowIndex Ҫ�޸ĵ����±ֻ꣬���û�ѡ�е��л��
     * @param ias �¼�¼
     */
    public void update(int rowIndex, IAS ias) {
        if (confirm("ȷ���޸ģ�")) {
            this.getValueAt(rowIndex).update(ias.getAmount(), ias.getActualDate(), ias.getType(), ias.getCategory());
            info("�޸ĳɹ���");
        }
    }

    /**
     * ɾ�����м�¼
     * @param rowIndex Ҫ�޸ĵ����±ֻ꣬���û�ѡ�е��л��
     */
    public void delete(int rowIndex) {
        if (confirm("ȷ��ɾ����")) {
            this.data.remove(this.getValueAt(rowIndex));
            info("ɾ���ɹ���");
        }
    }

    //�̳���AbstractTableModel����ʵ�ֵķ���
    @Override
    public int getRowCount() {
        return this.display_data.size() + 25;
    }

    @Override
    public int getColumnCount() {
        return 5;
    }

    @Override
    public String getColumnName(int column) {
        return COLUMN_NAME[column];
    }

    /**
     * ������������
     * @param from ��ʼ����
     * @param to ��������
     */
    public void setInterval(String from, String to) {
        try {
            this.from = parseLocalDate(parseDate(from));
            this.to = parseLocalDate(parseDate(to));
        } catch (NullPointerException e) {
            System.err.println("�����ڸ�ʽ������null");
        }
    }

    /**
     * ���ݲ���ģʽ�����㵱ǰ����
     * @return ��ǰ����
     */
    public double getBalance() {
        double d = 0;
        for (IAS ias : this.display_data) {
            d += ias.getAmount();
        }
        return d;
    }

    /**
     * AbstractTableModel����ʵ�ֵķ��� ������ʾ�������
     * @param rowIndex ���±�
     * @param columnIndex ���±�
     * @return Ҫ��ʾ��ֵ
     */
    @Override
    public String getValueAt(int rowIndex, int columnIndex) {
        if (isNull(rowIndex)) {
            return ""; //������в�������ʾ
        }
        IAS ias = getValueAt(rowIndex); //���ĳ�е���֧��¼
        switch (columnIndex) {
            case 0: {
                return String.valueOf(ias.getId());
            }
            case 1: {
                return ias.getDate();
            }
            case 2: {
                return ias.getType();
            }
            case 3: {
                return ias.getCategory();
            }
            case 4: {
                return String.valueOf(ias.getAmount());
            }
            default:
                return ""; //������Unreachable
        }
    }

    public IAS getValueAt(int rowIndex) {
        return this.display_data.get(rowIndex);
    }

    public boolean isNull(int rowIndex) {
        return rowIndex >= this.display_data.size();
    }

    /**
     * ˽�з��� ������Ƿ��ظ�
     * @param id Ҫ���ı��
     * @return �Ƿ��ظ�
     */
    private boolean isDuplicate(long id) {
        for (IAS ias : this.data) {
            if (ias.getId() == id) {
                return true;
            }
        }
        return false;
    }

    public void setMode(int mode) {
        this.display_data = getDisplayData(mode);
    }

    public void save() {
        new Serialization<List<IAS>>().serialize(this.data, "data.txt");
    }

    private List<IAS> filter(long from, long to) {
        return filter("", from, to);
    }

    private List<IAS> filter(String type) {
        return filter(type,  10000101, Long.MAX_VALUE);
    }

    /**
     *
     * @param type ���˵���֧��¼����
     * @param from ��ʼʱ��
     * @param to ��ֹʱ��
     * @return ������������֧��¼List
     */
    private List<IAS> filter(String type, long from, long to) {
        List<IAS> display_data = new LinkedList<>();
        for (IAS ias : this.data) {
            long l = Long.parseLong(ias.getDate());
            if (l >= from && l <= to && ias.getType().contains(type)) { //ʹ��contains����equals Ϊ������������
                display_data.add(ias);
            }
        }
        return display_data;
    }

    private List<IAS> getDisplayData(int mode) {
        switch (mode) {
            case 0: { //ȫ����¼ ����������
                return this.data;
            }
            case 1: { //�����¼ ����������
                return filter("����");
            }
            case 2: { //֧����¼ ����������
                return filter("֧��");
            }
            case 3: { //ȫ����¼ ����������
                return filter(this.from, this.to);
            }
            case 4: { //�����¼ ����������
                return filter("����", this.from, this.to);
            }
            case 5: { //֧����¼ ����������
                return filter("֧��", this.from, this.to);
            }
            default: {
                return null;
            }
        }
    }
}
