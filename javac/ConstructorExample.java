class Flower {

    String color;
    double height;

    public Flower(String color, double height) {
        this.color = color;
        this.height = height;
    }
}

public class ConstructorExample {

    public static void main(String[] args) {

        Flower f = new Flower("Red", 1.2);

        System.out.println(f.color);
        System.out.println(f.height);
    }
}