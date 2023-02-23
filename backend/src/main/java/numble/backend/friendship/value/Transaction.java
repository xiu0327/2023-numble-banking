package numble.backend.friendship.value;

public class Transaction {
    private int value;

    public Transaction() {
        this.value = 0;
    }

    public void increase(){
        value++;
    }

    public void decrease(){
        value--;
    }
}
