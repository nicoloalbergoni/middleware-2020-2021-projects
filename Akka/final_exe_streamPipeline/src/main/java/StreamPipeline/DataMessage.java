package StreamPipeline;

public class DataMessage {
    private int key;
    private int value;

    public int getKey() {
        return key;
    }

    public int getValue() {
        return value;
    }

    public DataMessage(int key, int value) {
        this.key = key;
        this.value = value;
    }
}
