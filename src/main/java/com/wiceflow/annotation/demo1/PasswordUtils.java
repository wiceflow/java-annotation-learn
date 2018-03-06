package com.wiceflow.annotation.demo1;

/**
 * @author BF
 * @date 2018/3/5
 * 使用注解：
 */

public class PasswordUtils {
    @UseCase(id="47",description="Passwords must contain at least one numeric")
    public boolean validatePassword(String password) {
        return (password.matches("\\w*\\d\\w*"));
    }

    @UseCase(id ="48")
    public String encryptPassword(String password) {
        return new StringBuilder(password).reverse().toString();
    }
}