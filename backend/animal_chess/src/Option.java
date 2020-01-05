import javax.crypto.spec.OAEPParameterSpec;
import java.util.Arrays;

enum MoveType {
    ATTACK, FLIP, FLEE, DIE
}


public class Option implements Comparable<Option> {
    public MoveType moveType;
    public int[] src;
    public int[] dst;

    public Option(MoveType moveType, int[] src, int[] dst) {
        this.moveType = moveType;
        this.src = src;
        this.dst = dst;
    }

    @Override
    public String toString() {
        return "{" +
                "moveType=" + moveType +
                ", src=" + Arrays.toString(src) +
                ", dst=" + Arrays.toString(dst) +
                '}';
    }

    @Override
    public int compareTo(Option option) {
        return moveType.ordinal() - option.moveType.ordinal();
    }
}
