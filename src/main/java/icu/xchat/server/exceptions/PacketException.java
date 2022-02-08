package icu.xchat.server.exceptions;

/**
 * 网络传输包异常
 *
 * @author shouchen
 */
public class PacketException extends Exception {
    public PacketException(String message) {
        super(message);
    }
}
