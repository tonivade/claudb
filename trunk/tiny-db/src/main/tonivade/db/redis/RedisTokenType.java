package tonivade.db.redis;

public enum RedisTokenType {

    STATUS(false),
    INTEGER(false),
    STRING(false),
    ARRAY(false),
    ERROR(true),
    UNKNOWN(true);

    private boolean error;

    private RedisTokenType(boolean error) {
        this.error = error;
    }

    public boolean isError() {
        return error;
    }
}