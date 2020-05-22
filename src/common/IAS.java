package common;

import exception.StringTooLongException;

import java.io.Serializable;
import java.time.DateTimeException;
import java.time.LocalDate;

import static common.Utility.error;

//Income And Spending
public class IAS implements Serializable {
    private static final long serialVersionUID = 1L;
    private long id;
    private double amount;
    private LocalDate date;
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

    public static LocalDate parseDate(String date) {
        try {
            return $parseDate(date);
        } catch (StringTooLongException ex) {
            System.err.println("日期过长");
            error(ex.getMessage());
        } catch (DateTimeException | StringIndexOutOfBoundsException ex) {
            System.err.println("日期格式错误");
            error("日期格式错误！");
        }
        return null;
    }

    private static LocalDate $parseDate(String date) throws StringTooLongException {
        if (date.length() > 8)
            throw new StringTooLongException("日期格式错误！");
        return parseDate(date.substring(0, 4), date.substring(4, 6), date.substring(6, 8));
    }

    private static LocalDate parseDate(String substring, String substring1, String substring2) {
        return parseDate(Integer.parseInt(substring), Short.parseShort(substring1), Short.parseShort(substring2));
    }

    private static LocalDate parseDate(int year, short month, short day) {
        return LocalDate.of(year, month, day);
    }

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

    public String getDate() {
        return String.format("%d%02d%02d", date.getYear(), date.getMonthValue(), date.getDayOfMonth());
    }

    public LocalDate getActualDate() {
        return this.date;
    }
}
