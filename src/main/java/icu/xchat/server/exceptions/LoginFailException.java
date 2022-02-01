package icu.xchat.server.exceptions;

/**
 * 登陆失败异常
 *
 * @author shouchen
 */
public class LoginFailException extends Exception {
    public LoginFailException(String message) {
        super(message);
    }
}
