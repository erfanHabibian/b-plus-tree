public class Mono {

    private String value;
    private Node left;
    private Node right;


    public Mono(String value, Node left, Node right){
        this.value = value;
        this.right = right;
        this.left = left;
    }


    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Node getLeft() {
        return left;
    }

    public void setLeft(Node left) {
        this.left = left;
    }

    public Node getRight() {
        return right;
    }

    public void setRight(Node right) {
        this.right = right;
    }

}
