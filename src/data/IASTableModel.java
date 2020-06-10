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
 * 程序中所有表格使用的数据模型IASTableModel
 * 继承于AbstractTableModel 只需要实现少量方法
 */
public class IASTableModel extends AbstractTableModel {

    private static final String[] COLUMN_NAME = {"编号", "日期", "类型", "内容", "金额"};
    private List<IAS> data, display_data; //为了避免反复遍历同一个List 建立两个List 一个用于存储数据 一个用于显示
    private long from, to;

    public IASTableModel(List<IAS> data) {
        this.data = data;
        this.display_data = data;
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
            this.getValueAt(rowIndex).update(ias.getAmount(), ias.getActualDate(), ias.getType(), ias.getCategory());
            info("修改成功！");
        }
    }

    /**
     * 删除已有记录
     * @param rowIndex 要修改的行下标，只从用户选中的行获得
     */
    public void delete(int rowIndex) {
        if (confirm("确认删除？")) {
            this.data.remove(this.getValueAt(rowIndex));
            info("删除成功！");
        }
    }

    //继承于AbstractTableModel必须实现的方法
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
        double d = 0;
        for (IAS ias : this.display_data) {
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

    public IAS getValueAt(int rowIndex) {
        return this.display_data.get(rowIndex);
    }

    public boolean isNull(int rowIndex) {
        return rowIndex >= this.display_data.size();
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
     * @param type 过滤的收支记录种类
     * @param from 起始时间
     * @param to 终止时间
     * @return 满足条件的收支记录List
     */
    private List<IAS> filter(String type, long from, long to) {
        List<IAS> display_data = new LinkedList<>();
        for (IAS ias : this.data) {
            long l = Long.parseLong(ias.getDate());
            if (l >= from && l <= to && ias.getType().contains(type)) { //使用contains而非equals 为的是适配重载
                display_data.add(ias);
            }
        }
        return display_data;
    }

    private List<IAS> getDisplayData(int mode) {
        switch (mode) {
            case 0: { //全部记录 无日期限制
                return this.data;
            }
            case 1: { //收入记录 无日期限制
                return filter("收入");
            }
            case 2: { //支出记录 无日期限制
                return filter("支出");
            }
            case 3: { //全部记录 有日期限制
                return filter(this.from, this.to);
            }
            case 4: { //收入记录 有日期限制
                return filter("收入", this.from, this.to);
            }
            case 5: { //支出记录 有日期限制
                return filter("支出", this.from, this.to);
            }
            default: {
                return null;
            }
        }
    }
}
