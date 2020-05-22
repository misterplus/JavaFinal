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

public class IASTableModel extends AbstractTableModel {

    private static final String[] COLUMN_NAME = {"���", "����", "����", "����", "���"};
    private List<IAS> data;
    private int mode;
    private long from, to;

    public IASTableModel(List<IAS> data) {
        this.data = data;
        this.mode = 0;
    }

    public void add(IAS ias) throws DuplicationException {
        if (!isDuplicate(ias.getId())) {
            this.data.add(ias);
        } else
            throw new DuplicationException(String.format("�Ѵ��ڱ��Ϊ%d����֧��¼��", ias.getId()));
    }

    public void update(int rowIndex, IAS ias) {
        if (confirm("ȷ���޸ģ�")) {
            Objects.requireNonNull(getValueAt(rowIndex)).update(ias.getAmount(), ias.getActualDate(), ias.getType(), ias.getCategory());
            info("�޸ĳɹ���");
        }
    }

    public void delete(int rowIndex) {
        if (confirm("ȷ��ɾ����")) {
            this.data.remove(Objects.requireNonNull(getValueAt(rowIndex)));
            info("ɾ���ɹ���");
        }
    }

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

    public void setInterval(String from, String to) {
        try {
            this.from = parseLocalDate(parseDate(from));
            this.to = parseLocalDate(parseDate(to));
        } catch (NullPointerException e) {
            System.err.println("�����ڸ�ʽ������null");
        }
    }

    public double getBalance() {
        double d = 0;
        for (IAS ias : this.data) {
            d += ias.getAmount();
        }
        return d;
    }

    @Override
    public String getValueAt(int rowIndex, int columnIndex) {
        if (rowIndex >= this.data.size()) {
            return "";
        }
        IAS ias = getValueAt(rowIndex);
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
                return "";
        }
    }

    public boolean isNull(int rowIndex) {
        return rowIndex >= this.data.size();
    }

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

    public IAS getValueAt(int rowIndex) {
        switch (this.mode) {
            case 0: {
                return this.data.get(rowIndex);
            }
            case 1: {
                return getOnly(rowIndex, "����");
            }
            case 2: {
                return getOnly(rowIndex, "֧��");
            }
            case 3: {
                return getOnly(rowIndex, this.from, this.to);
            }
            case 4: {
                return getOnly(rowIndex, "����", this.from, this.to);
            }
            case 5: {
                return getOnly(rowIndex, "֧��", this.from, this.to);
            }
            default: {
                return null;
            }
        }
    }

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
            if (l >= from && l <= to && ias.getType().contains(type)) {
                i++;
                if (i == rowIndex) {
                    return ias;
                }
            }
        }
        return null;
    }
}
