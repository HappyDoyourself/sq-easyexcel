
package exception;

/**
 * Service层公用的Exception.
 * 
 * 继承自RuntimeException, 从由Spring管理事务的函数中抛出时会触发事务回滚.
 *
 * @author zjw
 * @date 2020.03.23
 */
public class ServiceException extends RuntimeException {

    /**
     * 异常码
     */
    private int code;

    /**
     * 是否为error级别异常，默认为是<br>
     *     error级别异常，打印异常信息到error级别日志中;非error级别异常，打印异常信息到info级别日志中。
     */
    private boolean errorLevel = true;

    public ServiceException(int code, String message) {
        this(code,message,true);
    }

    public ServiceException(int code, String message, boolean errorLevel) {
        super(message);
        this.code = code;
        this.errorLevel = errorLevel;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public boolean isErrorLevel() {
        return errorLevel;
    }

    public void setErrorLevel(boolean errorLevel) {
        this.errorLevel = errorLevel;
    }

    @Override
    public String toString() {
        return "ServiceException[code=" + code + ",message=" + getMessage() + ",errorLevel=" + errorLevel + "]";
    }
}
