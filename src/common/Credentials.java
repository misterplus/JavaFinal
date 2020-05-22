package common;

import java.io.Serializable;

/**
 * 凭据类
 * 用于存储用户名和密码 承担了验证登录等工作
 */
public class Credentials implements Serializable {
    private static final long serialVersionUID = 2L;
    private String username, password;

    public Credentials() {
        this("", "");
    }

    public Credentials(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean validate(String username, String password) {
        return this.username.equals(username) && this.password.equals(password);
    }

    public boolean isNew() {
        return this.username.isEmpty() || this.password.isEmpty();
    }

    @Override
    public String toString() {
        return "Credentials{" +
                "username='" + username + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
