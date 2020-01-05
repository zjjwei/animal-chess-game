

public class Item {
    public ItemColor getColor() {
        return color;
    }

    public void setColor(ItemColor color) {
        this.color = color;
    }

    public ItemAnimal getAnimal() {
        return animal;
    }

    public void setAnimal(ItemAnimal animal) {
        this.animal = animal;
    }

    public boolean isFaceUp() {
        return faceUp;
    }

    public void setFaceUp(boolean faceUp) {
        this.faceUp = faceUp;
    }

    public void flipItem() {
        faceUp = true;
    }

    public String toString() {
        String face = faceUp ? "up" : "Down";
        int animalInt = animal.ordinal();
//        return "{" + color.toString() + " " + animal.toString() + " " + face + "}";
//        return "{" + color.toString() + " " + animalInt + " " + face + "}";
        return  "" + animalInt;
    }

    public String toStringVerbose() {
        String face = faceUp ? "up" : "Down";
        int animalInt = animal.ordinal();
        return "{" + color.toString() + " " + animal.toString() + " " + face + "}";
    }

    private ItemColor color;
    private ItemAnimal animal;
    private boolean faceUp = false;

    public Item(ItemColor color, ItemAnimal animal) {
        this.color = color;
        this.animal = animal;
    }


}
