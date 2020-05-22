package common;

import exception.StringTooLongException;

import java.io.Serializable;
import java.time.DateTimeException;
import java.time.LocalDate;

import static common.Utility.error;

//Income And Spending 收支
public class IAS implements Serializable {
    private static final long serialVersionUID = 1L;
    private long id;
    private double amount;
    private LocalDate date; //使用LocalDate类 不需要自己处理日期不存在及闰年等问题
    private String type, category;

    public IAS(long id, double amount, LocalDate date, String type, String category) throws NullPointerException {
        this.id = id;
        this.amount = amount;
        if (date == null)
            throw new NullPointerException();
        this.date = date;
        this.type = type;
        this.category = category;
    }

    /**
     * 解析日期
     * @param date 字符串形式的日期
     * @return date对应日期的LocalDate对象
     */
    public static LocalDate parseDate(String date) {
        try {
            return $parseDate(date);
        } catch (StringTooLongException ex) { //字符串长度大于8
            System.err.println("日期过长");
            error(ex.getMessage());
        } catch (DateTimeException | StringIndexOutOfBoundsException ex) { //日期不存在或字符串过短
            System.err.println("日期格式错误");
            error("日期格式错误！");
        }
        return null; //解析不了的情况下返回null 会在构造函数中抛出NPE
    }

    /**
     * 解析日期的具体实现
     * @param date 字符串形式的日期
     * @return date对应日期的LocalDate对象
     * @throws StringTooLongException 日期字符串长度大于8
     */
    private static LocalDate $parseDate(String date) throws StringTooLongException {
        if (date.length() > 8)
            throw new StringTooLongException("日期格式错误！");
        return parseDate(date.substring(0, 4), date.substring(4, 6), date.substring(6, 8));
    }

    //包装类进行类型转换
    private static LocalDate parseDate(String substring, String substring1, String substring2) {
        return parseDate(Integer.parseInt(substring), Short.parseShort(substring1), Short.parseShort(substring2));
    }

    //LocalDate.of自带日期合法性检查
    private static LocalDate parseDate(int year, short month, short day) {
        return LocalDate.of(year, month, day);
    }

    /**
     * 将一个LocalDate对象解析为对应的long
     * @param date 需要解析的LocalDate对象
     * @return date对应的long
     * @throws NullPointerException 日期格式错误获得null
     */
    public static long parseLocalDate(LocalDate date) throws NullPointerException {
        return Long.parseLong(new IAS(0, 0, date, "", "").getDate());
    }

    public double getAmount() {
        return amount;
    }

    public String getType() {
        return type;
    }

    public String getCategory() {
        return category;
    }

    public long getId() {
        return id;
    }

    //更新当前收支记录的内容 但不改变其对象的地址
    public void update(double amount, LocalDate date, String type, String category) {
        this.amount = amount;
        this.date = date;
        this.type = type;
        this.category = category;
    }

    @Override
    public String toString() {
        return "IAS{" +
                "id=" + id +
                ", date=" + date +
                ", type='" + type + '\'' +
                ", category='" + category + '\'' +
                '}';
    }

    //获取当前收支记录日期的字符串形式用于显示
    //不考虑对1000年以前的支持 真要支持未免太刁钻了
    public String getDate() {
        return String.format("%d%02d%02d", date.getYear(), date.getMonthValue(), date.getDayOfMonth());
    }

    //需要获取LocalDate对象本身的情况下使用
    public LocalDate getActualDate() {
        return this.date;
    }
}
