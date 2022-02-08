package icu.xchat.server.exceptions;

/**
 * 登陆失败异常
 *
 * @author shouchen
 */
public class LoginException extends Exception {
    public LoginException(String message) {
        super(message);
    }
}
