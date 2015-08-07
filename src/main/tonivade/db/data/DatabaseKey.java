package tonivade.db.data;

import tonivade.db.redis.SafeString;

public class DatabaseKey {

    private SafeString value;

    public DatabaseKey(SafeString value) {
        super();
        this.value = value;
    }

    public SafeString getValue() {
        return value;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((value == null) ? 0 : value.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        DatabaseKey other = (DatabaseKey) obj;
        if (value == null) {
            if (other.value != null) {
                return false;
            }
        } else if (!value.equals(other.value)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return value.toString();
    }

    public static DatabaseKey safeKey(SafeString str) {
        return new DatabaseKey(str);
    }

    public static DatabaseKey safeKey(String str) {
        return new DatabaseKey(SafeString.safeString(str));
    }

}
