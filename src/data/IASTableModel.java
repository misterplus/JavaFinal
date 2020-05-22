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
 * 程序中所有表格使用的数据模型IASTableModel
 * 继承于AbstractTableModel 只需要实现少量方法
 */
public class IASTableModel extends AbstractTableModel {

    private static final String[] COLUMN_NAME = {"编号", "日期", "类型", "内容", "金额"};
    private List<IAS> data;
    private int mode; //用于在各种查询状态下切换
    private long from, to;

    public IASTableModel(List<IAS> data) {
        this.data = data;
        this.mode = 0;
    }

    /**
     * 添加新记录
     * @param ias 要添加的新纪录的对象
     * @throws DuplicationException 新记录的编号与已有记录重复
     */
    public void add(IAS ias) throws DuplicationException {
        if (!isDuplicate(ias.getId())) {
            this.data.add(ias);
        } else
            throw new DuplicationException(String.format("已存在编号为%d的收支记录！", ias.getId()));
    }

    /**
     * 修改已有记录
     * @param rowIndex 要修改的行下标，只从用户选中的行获得
     * @param ias 新记录
     */
    public void update(int rowIndex, IAS ias) {
        if (confirm("确认修改？")) {
            Objects.requireNonNull(getValueAt(rowIndex)).update(ias.getAmount(), ias.getActualDate(), ias.getType(), ias.getCategory());
            info("修改成功！");
        }
    }

    /**
     * 删除已有记录
     * @param rowIndex 要修改的行下标，只从用户选中的行获得
     */
    public void delete(int rowIndex) {
        if (confirm("确认删除？")) {
            this.data.remove(Objects.requireNonNull(getValueAt(rowIndex)));
            info("删除成功！");
        }
    }

    //继承于AbstractTableModel必须实现的方法
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
     * 设置日期区间
     * @param from 开始日期
     * @param to 结束日期
     */
    public void setInterval(String from, String to) {
        try {
            this.from = parseLocalDate(parseDate(from));
            this.to = parseLocalDate(parseDate(to));
        } catch (NullPointerException e) {
            System.err.println("因日期格式错误传入null");
        }
    }

    /**
     * 根据查找模式来计算当前净额
     * @return 当前净额
     */
    public double getBalance() {
        switch (this.mode) {
            case 0: {
                return this.getBalance("");
            }
            case 1: {
                return this.getBalance("收入");
            }
            case 2: {
                return this.getBalance("支出");
            }
            case 3: {
                return this.getBalance("", this.from, this.to);
            }
            case 4: {
                return this.getBalance("收入", this.from, this.to);
            }
            case 5: {
                return this.getBalance("支出", this.from, this.to);
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
            if (ias.getType().contains(type) && l >= from && l <= to) //使用contains而非equals 为的是适配不限制种类的情况
                d += ias.getAmount();
        }
        return d;
    }

    /**
     * AbstractTableModel必须实现的方法 用于显示表格内容
     * @param rowIndex 行下标
     * @param columnIndex 列下标
     * @return 要显示的值
     */
    @Override
    public String getValueAt(int rowIndex, int columnIndex) {
        if (isNull(rowIndex)) {
            return ""; //多余空行不进行显示
        }
        IAS ias = getValueAt(rowIndex); //获得某行的收支记录
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
                return ""; //理论上Unreachable
        }
    }

    public boolean isNull(int rowIndex) {
        return rowIndex >= this.data.size();
    }

    /**
     * 私有方法 检查编号是否重复
     * @param id 要检查的编号
     * @return 是否重复
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
     * 获取某行的收支记录对象
     * 进行了大量的遍历 以时间复杂度换取空间复杂度 避免大量重复数据的冗余
     * @param rowIndex 行下标
     * @return 对应的收支记录
     */
    public IAS getValueAt(int rowIndex) {
        switch (this.mode) {
            case 0: { //全部记录 无日期限制
                return this.data.get(rowIndex);
            }
            case 1: { //收入记录 无日期限制
                return getOnly(rowIndex, "收入");
            }
            case 2: { //支出记录 无日期限制
                return getOnly(rowIndex, "支出");
            }
            case 3: { //全部记录 有日期限制
                return getOnly(rowIndex, this.from, this.to);
            }
            case 4: { //收入记录 有日期限制
                return getOnly(rowIndex, "收入", this.from, this.to);
            }
            case 5: { //支出记录 有日期限制
                return getOnly(rowIndex, "支出", this.from, this.to);
            }
            default: {
                return null;
            }
        }
    }

    //多次重载提高复用性
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
            if (l >= from && l <= to && ias.getType().contains(type)) { //使用contains 理由同上
                i++;
                if (i == rowIndex) {
                    return ias;
                }
            }
        }
        return null;
    }
}
