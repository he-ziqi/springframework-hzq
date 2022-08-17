package world.hzq.ioc.exception;
//bean不存在异常
public class NoSuchBeanException extends RuntimeException{
    public NoSuchBeanException(){}
    public NoSuchBeanException(String msg){
        super(msg);
    }
}
