package data;

import common.IAS;
import exception.DuplicationException;

import javax.swing.table.AbstractTableModel;
import java.util.List;
import java.util.Objects;

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
    private List<IAS> data;
    private int mode; //�����ڸ��ֲ�ѯ״̬���л�
    private long from, to;

    public IASTableModel(List<IAS> data) {
        this.data = data;
        this.mode = 0;
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
            Objects.requireNonNull(getValueAt(rowIndex)).update(ias.getAmount(), ias.getActualDate(), ias.getType(), ias.getCategory());
            info("�޸ĳɹ���");
        }
    }

    /**
     * ɾ�����м�¼
     * @param rowIndex Ҫ�޸ĵ����±ֻ꣬���û�ѡ�е��л��
     */
    public void delete(int rowIndex) {
        if (confirm("ȷ��ɾ����")) {
            this.data.remove(Objects.requireNonNull(getValueAt(rowIndex)));
            info("ɾ���ɹ���");
        }
    }

    //�̳���AbstractTableModel����ʵ�ֵķ���
    @Override
    public int getRowCount() {
        return data.size() + 25;
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
        switch (this.mode) {
            case 0: {
                return this.getBalance("");
            }
            case 1: {
                return this.getBalance("����");
            }
            case 2: {
                return this.getBalance("֧��");
            }
            case 3: {
                return this.getBalance("", this.from, this.to);
            }
            case 4: {
                return this.getBalance("����", this.from, this.to);
            }
            case 5: {
                return this.getBalance("֧��", this.from, this.to);
            }
            default: {
                return 0;
            }
        }
    }

    public double getBalance(String type) {
        return getBalance(type, 0, Long.MAX_VALUE);
    }

    public double getBalance(String type, long from, long to) {
        double d = 0;
        for (IAS ias : this.data) {
            long l = Long.parseLong(ias.getDate());
            if (ias.getType().contains(type) && l >= from && l <= to) //ʹ��contains����equals Ϊ�������䲻������������
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
        if (ias == null)
            return "";
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

    public boolean isNull(int rowIndex) {
        return rowIndex >= this.data.size();
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
        this.mode = mode;
    }

    public void save() {
        new Serialization<List<IAS>>().serialize(this.data, "data.txt");
    }

    /**
     * ��ȡĳ�е���֧��¼����
     * �����˴����ı��� ��ʱ�临�ӶȻ�ȡ�ռ临�Ӷ� ��������ظ����ݵ�����
     * @param rowIndex ���±�
     * @return ��Ӧ����֧��¼
     */
    public IAS getValueAt(int rowIndex) {
        switch (this.mode) {
            case 0: { //ȫ����¼ ����������
                return this.data.get(rowIndex);
            }
            case 1: { //�����¼ ����������
                return getOnly(rowIndex, "����");
            }
            case 2: { //֧����¼ ����������
                return getOnly(rowIndex, "֧��");
            }
            case 3: { //ȫ����¼ ����������
                return getOnly(rowIndex, this.from, this.to);
            }
            case 4: { //�����¼ ����������
                return getOnly(rowIndex, "����", this.from, this.to);
            }
            case 5: { //֧����¼ ����������
                return getOnly(rowIndex, "֧��", this.from, this.to);
            }
            default: {
                return null;
            }
        }
    }

    //���������߸�����
    private IAS getOnly(int rowIndex, String type) {
        return getOnly(rowIndex, type, 0, Long.MAX_VALUE);
    }

    private IAS getOnly(int rowIndex, long from, long to) {
        return getOnly(rowIndex, "", from, to);
    }

    private IAS getOnly(int rowIndex, String type, long from, long to) {
        int i = -1;
        for (IAS ias : this.data) {
            long l = Long.parseLong(ias.getDate());
            if (l >= from && l <= to && ias.getType().contains(type)) { //ʹ��contains ����ͬ��
                i++;
                if (i == rowIndex) {
                    return ias;
                }
            }
        }
        return null;
    }
}
