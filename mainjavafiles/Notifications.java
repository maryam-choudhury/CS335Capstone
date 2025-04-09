public class Notifications {
    private String message;

    public Notifications(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    // Problem: Just returning dates as notifications 
    // Solution: Override equals to prevent duplicate notifications being added
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Notifications)) return false;
        Notifications other = (Notifications) obj;
        return message.equals(other.message);
    }

    @Override
    public int hashCode() {
        return message.hashCode();
    }
    @Override
    public String toString() {
        return message;
    }
}
