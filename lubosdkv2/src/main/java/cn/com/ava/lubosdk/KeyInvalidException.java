package cn.com.ava.lubosdk;


public class KeyInvalidException extends IllegalAccessException {

    private static final String MESSAGE = "Login key invalid.";

    public KeyInvalidException() {
        super(MESSAGE);
    }

    @Override
    public boolean equals( Object obj) {
        if (obj instanceof KeyInvalidException && getMessage().equals(MESSAGE)) {
            return true;
        }
        return false;
    }
}
