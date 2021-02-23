public class Node {

    private Status status;
    private Mono[] array;
    private Node parent;

    public Node(){
        // no parameter
        array = new Mono[BPlusTree.MAX_DEGREE];
        status = Status.LEAF;
        parent = null;
    }

    public Node(Mono[] array, Status status, Node parent){
        this.array = array;
        this.status = status;
        this.parent = parent;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Mono[] getArray() {
        return array;
    }

    public void setArray(Mono[] array) {
        this.array = array;
    }

    public Node getParent() {
        return parent;
    }

    public void setParent(Node parent) {
        this.parent = parent;
    }

    @Override
    public String toString() {
        String st = "";
        for (int i = 0; i < BPlusTree.MAX_DEGREE; i++) {
            if (array[i] != null)
                st += array[i].getValue()+", ";
        }
        return "{ " + st + " } ";
    }

    public String print(){
        String st = "";
        st += toString() + " => ";
        for (int i = 0; i < array.length; i++) {
            if (array[i] == null) break;
            if (array[i].getLeft() != null && i==0){
                st += array[i].getLeft().toString();
            }
            if (array[i].getRight() != null){
                st += array[i].getRight().toString();
            }
        }
        return st;
    }

}
