package world.hzq.aop.exeception;

public class NoSuchPackageException extends RuntimeException{
    public NoSuchPackageException() {
    }

    public NoSuchPackageException(String message) {
        super(message);
    }
}
