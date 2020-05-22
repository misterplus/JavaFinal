package common;

import exception.StringTooLongException;

import java.io.Serializable;
import java.time.DateTimeException;
import java.time.LocalDate;

import static common.Utility.error;

//Income And Spending ��֧
public class IAS implements Serializable {
    private static final long serialVersionUID = 1L;
    private long id;
    private double amount;
    private LocalDate date; //ʹ��LocalDate�� ����Ҫ�Լ��������ڲ����ڼ����������
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
     * ��������
     * @param date �ַ�����ʽ������
     * @return date��Ӧ���ڵ�LocalDate����
     */
    public static LocalDate parseDate(String date) {
        try {
            return $parseDate(date);
        } catch (StringTooLongException ex) { //�ַ������ȴ���8
            System.err.println("���ڹ���");
            error(ex.getMessage());
        } catch (DateTimeException | StringIndexOutOfBoundsException ex) { //���ڲ����ڻ��ַ�������
            System.err.println("���ڸ�ʽ����");
            error("���ڸ�ʽ����");
        }
        return null; //�������˵�����·���null ���ڹ��캯�����׳�NPE
    }

    /**
     * �������ڵľ���ʵ��
     * @param date �ַ�����ʽ������
     * @return date��Ӧ���ڵ�LocalDate����
     * @throws StringTooLongException �����ַ������ȴ���8
     */
    private static LocalDate $parseDate(String date) throws StringTooLongException {
        if (date.length() > 8)
            throw new StringTooLongException("���ڸ�ʽ����");
        return parseDate(date.substring(0, 4), date.substring(4, 6), date.substring(6, 8));
    }

    //��װ���������ת��
    private static LocalDate parseDate(String substring, String substring1, String substring2) {
        return parseDate(Integer.parseInt(substring), Short.parseShort(substring1), Short.parseShort(substring2));
    }

    //LocalDate.of�Դ����ںϷ��Լ��
    private static LocalDate parseDate(int year, short month, short day) {
        return LocalDate.of(year, month, day);
    }

    /**
     * ��һ��LocalDate�������Ϊ��Ӧ��long
     * @param date ��Ҫ������LocalDate����
     * @return date��Ӧ��long
     * @throws NullPointerException ���ڸ�ʽ������null
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

    //���µ�ǰ��֧��¼������ �����ı������ĵ�ַ
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

    //��ȡ��ǰ��֧��¼���ڵ��ַ�����ʽ������ʾ
    //�����Ƕ�1000����ǰ��֧�� ��Ҫ֧��δ��̫������
    public String getDate() {
        return String.format("%d%02d%02d", date.getYear(), date.getMonthValue(), date.getDayOfMonth());
    }

    //��Ҫ��ȡLocalDate������������ʹ��
    public LocalDate getActualDate() {
        return this.date;
    }
}
