
import java.util.ArrayList;


public class BPlusTree {


    public static int MAX_DEGREE = 3;
    public static int threshold = MAX_DEGREE /2;
    public Node root;
    public ArrayList<String> values = new ArrayList<>();

    public BPlusTree(int MAX_DEGREE){
        this.MAX_DEGREE = MAX_DEGREE;
        this.threshold = MAX_DEGREE /2;
        root = new Node();
    }


    public void add(Node node, String value){

        //go till reaching leaf
        values.add(value);

        Node current = node;
        int k = 0;

        while (current.getStatus() != Status.LEAF){
            Mono[] array = current.getArray();
            if (array[k] == null){
                current = array[k-1].getRight();
                k=0;
                continue;

            } else if (compare(array[k].getValue(), value) == 0 || (array[k].getValue().equals(value)) ){
                // value is lower than array element
                current = array[k].getLeft();
                k=0;
                continue;
            }
            k++;
        }

        // the appropriate leaf is found
        Mono mono = new Mono(value, null, null);

        insert(current, mono);

    }

    private Node findBestNode(String value) {
        Node current = root;
        int z = 0;
        while (current.getStatus() != Status.LEAF){
            Mono[] array = current.getArray();
            if (array[z] == null){
                current = array[z-1].getRight();
                z=0;
                continue;

            } else if (compare(array[z].getValue(), value) == 0 ){
                // value is lower than array element
                current = array[z].getLeft();
                z=0;
                continue;
            }else if ((array[z].getValue().equals(value))){
                current = array[z].getRight();
                z=0;
                continue;
            }
            z++;
        }
        return current;
    }


    private void breakNode(Node node){

        //leaf
        Mono[] newArray = new Mono[MAX_DEGREE];
        int j = 0;
        for (int i = 0; i < MAX_DEGREE; i++) {
            if (node.getArray()[i] == null) break;
            if (i>= threshold){
                //add to new node
                newArray[j] = node.getArray()[i];
                j++;
                node.getArray()[i] = null;
            }
        }
        Node newNode = new Node(newArray, Status.LEAF, node.getParent());

        if (node.getParent() == null){
            //root is leaf
            Mono[] array = new Mono[MAX_DEGREE];
            array[0] = new Mono(newArray[0].getValue(), node, newNode);
            Node newRoot = new Node(array, Status.MIDDLE, null);
            root = newRoot;
            node.setParent(root);
            newNode.setParent(root);
        }else {
            //leaf has parent
            Mono mono = new Mono(newArray[0].getValue(), node, newNode);
            insert(node.getParent(), mono);
        }


    }

    private void middleBreak(Node node){

        Mono[] newArray = new Mono[MAX_DEGREE];
        int j = 0;
        for (int i = 0; i < MAX_DEGREE; i++) {
            if (node.getArray()[i] == null) break;
            if (i > threshold){
                //add to new node
                newArray[j] = node.getArray()[i];
                j++;
                node.getArray()[i] = null;
            }

        }
        Node newNode = new Node(newArray, node.getStatus(), node.getParent());


        Mono middle = new Mono(node.getArray()[threshold].getValue(), node, newNode);

        node.getArray()[threshold] = null;

        informChildren(newNode);

        if (node.getParent() == null){
            //root is being broken.
            Mono[] newRootArray = new Mono[MAX_DEGREE];
            newRootArray[0] = middle;
            Node newRoot = new Node(newRootArray, Status.MIDDLE, null);
            root = newRoot;
            node.setParent(root);
            newNode.setParent(root);
        }else {
            // middle
            insert(node.getParent(), middle);
        }

    }


    private void informChildren(Node rightParent) {
        for (int i = 0; i < MAX_DEGREE; i++) {
            if(rightParent.getArray()[i] != null){
                rightParent.getArray()[i].getLeft().setParent(rightParent);
                rightParent.getArray()[i].getRight().setParent(rightParent);
            }
        }
    }

    private void reorganize(Node node, Mono newMono) {
        /*

        set the children in right pointer

         */
        Mono[] array = node.getArray();
        for (int i = 0; i < MAX_DEGREE; i++) {
            if (array[i] == newMono){
                if (i>0) array[i-1].setRight(array[i].getLeft());
                if (i+1 < MAX_DEGREE && array[i+1] != null) array[i+1].setLeft(array[i].getRight());
            }
        }
    }


    private boolean isFull(Node node) {
        if (node.getArray()[MAX_DEGREE-1] == null)return  false;
        return true;
    }


    private boolean insert(Node node, Mono mono) {
        for (int i = 0; i < MAX_DEGREE; i++) {

            if (node.getArray()[i] == null) { // if we are at the end of the list
                node.getArray()[i] = mono;
                return inserted(node, mono);
            }
            // if new value is lower than the next index
            else if (compare(node.getArray()[i].getValue(), mono.getValue()) == 0){
                /*

                put item in the right index

                and

                shifting all other items to the right


                 */

                Mono temp = mono;
                Mono temp2 = node.getArray()[i];
                for (int j = i; j < MAX_DEGREE; j++) {

                    node.getArray()[j] = temp;
                    temp = temp2;
                    if (j+1 >= MAX_DEGREE) break;
                    temp2 = node.getArray()[j+1];
                }
                return inserted(node, mono);
            }


        }
        return false;
    }

    private boolean inserted(Node node, Mono mono) {
        reorganize(node, mono);
        if (isFull(node)) {
            if (node.getStatus() == Status.MIDDLE){
                // middle break
                middleBreak(node);
            }else {
                // leaf
                breakNode(node);
            }
        }
        return true;
    }


    private int compare(String old, String newVal) {
        if (isNumber(old, newVal)) {
            int a = Integer.parseInt(old);
            int b = Integer.parseInt(newVal);
            if (a > b) return 0;
            return 1;
        }else {
            int l1 = old.length();
            int l2 = newVal.length();
            int size = Math.min(l1, l2);
            for (int i = 0; i < size; i++) {
                if (old.compareTo(newVal) > 0) return 0;
                return 1;

            }
            return 1;
        }

    }


    private boolean isNumber(String old, String newVal) {
        try{
            Integer.parseInt(old);
            Integer.parseInt(newVal);
            return true;

        }catch (NumberFormatException n){
            return false;
        }
    }


    public void buildTree(Node node){
        System.out.println(node.print());
        if (node.getStatus() == Status.MIDDLE){
            for (int i = 0; i < MAX_DEGREE; i++) {
                if (node.getArray()[i] != null){
                    if (node.getArray()[i].getLeft().getStatus() != Status.LEAF && i==0)
                        buildTree(node.getArray()[i].getLeft());
                    if (node.getArray()[i].getRight().getStatus() != Status.LEAF)
                        buildTree(node.getArray()[i].getRight());
                }
            }
        }
    }


    public void delete(String value){
        Node current = findBestNode(value);
        for (int i = 0; i < MAX_DEGREE; i++) {
            if (current.getArray()[i] == null){
                System.out.println("ERROR: value " + value + " does not exist.");
                return;
            } else if (current.getArray()[i].getValue().equals(value)) {
                // node is found
                values.remove(value);
                remove(current, value);
                return;
            }
        }


    }

    private void remove(Node node, String value) {
        for (int i = 0; i < MAX_DEGREE; i++) {
            if (node.getArray()[i] == null) return; // not found
            if (node.getArray()[i].getValue().equals(value)){
                //mono was found
                Mono deleting = node.getArray()[i];
                Mono sub = deleting;
                if (getSubstitute(node, deleting) != null)
                    sub = getSubstitute(node, deleting);
                //shift the rest of the array
                for (int j = i; j < MAX_DEGREE; j++) {
                    if (node.getArray()[j] == null) break;
                    node.getArray()[j] = node.getArray()[j+1];

                }
                // mono was deleted
                System.out.println("value <" + value + "> was deleted.");
                checkLeafSize(node, deleting);
                substitute(node, deleting, sub);
                break;
            }
        }
    }

    private Mono getSubstitute(Node node, Mono deleting) {
        for (int i = 0; i < MAX_DEGREE; i++) {
            if (node.getArray()[i] == deleting){
                if (i+1<MAX_DEGREE && node.getArray()[i+1] != null){
                    return node.getArray()[i+1];
                }else {
                    for (int j = 0; j < MAX_DEGREE; j++) {
                        if (node.getParent().getArray()[j] == null) return null;
                        if (node.getParent().getArray()[j].getLeft() == node){
                            return node.getParent().getArray()[j].getRight().getArray()[0];
                        }
                    }
                }
            }
        }
        return null;
    }


    private void checkLeafSize(Node node, Mono deleting) {
        if (node.getArray()[threshold-1] == null) {
            //node is less than half full
            Node[] sibArray = getSibling(node);
            Node leftSib = sibArray[0];
            Node rightSib = sibArray[1];
            if (leftSib != null && leftSib.getArray()[threshold] != null){
                //parity with left sibling
                for (int j = 0; j < MAX_DEGREE - 1; j++) {
                    if (leftSib.getArray()[j+1] == null){
                        //insert at the first

                        Mono temp = leftSib.getArray()[j];
                        leftSib.getArray()[j] = null;
                        Mono temp2 = node.getArray()[0];
                        for (int k = 0; k < MAX_DEGREE-1; k++) {
                            node.getArray()[k] = temp;
                            temp = temp2;
                            temp2 = node.getArray()[k+1];
                        }
                        break;
                    }
                }
                for (int i = 0; i < MAX_DEGREE; i++) {
                    if (node.getParent().getArray()[i].getRight() == node) {
                        node.getParent().getArray()[i].setValue(node.getArray()[0].getValue());
                        break;
                    }
                }
            }else if (rightSib != null && rightSib.getArray()[threshold] != null){
                //parity with right sib
                node.getArray()[threshold-1] = rightSib.getArray()[0];
                for (int i = 0; i < MAX_DEGREE; i++) {
                    if (node.getParent().getArray()[i].getRight() == rightSib){
                        for (int j = 0; j < MAX_DEGREE-1; j++) {
                            node.getParent().getArray()[i].getRight().getArray()[j] =
                                    node.getParent().getArray()[i].getRight().getArray()[j+1];
                        }
                        //sync parent
                        node.getParent().getArray()[i].setValue(node.getParent().
                                getArray()[i].getRight().getArray()[0].getValue());
                        break;
                    }
                }

            }else if (leftSib != null){
                //merge with left sib
                merge(leftSib, node);

            }else if (rightSib != null){
                //merge with right sib
                merge(node, rightSib);
            }
        }else {
            removeFromParent(node, deleting);
        }
        if (node.getParent().getArray()[threshold-1] == null){
            //reorganize the tree
            reorganizeTree();
        }
    }

    private void merge(Node left, Node right) {
        Node temp = right;
        for (int j = 0, k = 0; j < MAX_DEGREE && k < threshold; j++) {
            if (left.getArray()[j] == null){
                left.getArray()[j] =
                        right.getArray()[k];
                k++;
                right.getArray()[k] = null;
            }
        }
        for (int j = 0; j < MAX_DEGREE-1; j++) {
            if (left.getParent().getArray()[j].getRight() == temp){
                //shift all the items coming next
                if (left.getParent().getArray()[j+1] != null)
                    left.getParent().getArray()[j+1].setLeft(left.getParent().getArray()[j].getLeft());
                for (int k = j; k < MAX_DEGREE-1; k++) {
                    left.getParent().getArray()[k] = left.getParent().getArray()[k+1];
                }
                break;
            }
        }
    }


    private void reorganizeTree() {
        root = new Node();
        for (int i = 0; i < values.size(); i++) {
            add(root, values.remove(0));
        }

    }


    private void removeFromParent(Node node, Mono mono){
        for (int i = 0; i < MAX_DEGREE; i++) {
            if (node.getParent().getArray()[i] == null)break;
            if (node.getParent().getArray()[i].getValue().equals(mono.getValue())){
                node.getParent().getArray()[i].setValue(node.getParent().
                        getArray()[i].getRight().getArray()[0].getValue());
                return;
            }
        }
    }


    private void substitute(Node node, Mono mono, Mono sub){
         if (node.getParent() != null){
             for (int i = 0; i < MAX_DEGREE; i++) {
                 if (node.getParent().getArray()[i] == null) break;
                 if (node.getParent().getArray()[i].getValue().equals(mono.getValue())){
                     node.getParent().getArray()[i].setValue(sub.getValue());
                     break;
                 }
             }
             substitute(node.getParent(), mono, sub);
         }
    }


    private Node[] getSibling(Node node){
        Node[] arr = new Node[2];
        Node parent = node.getParent();
        for (int i = 0; i < MAX_DEGREE; i++) {
            if (parent.getArray()[i] == null)break;
            if (parent.getArray()[i].getLeft() == node){
                arr[1] = parent.getArray()[i].getRight();
                if (i>0){
                    arr[0] = parent.getArray()[i-1].getLeft();
                }
                return arr;
            }
            if (parent.getArray()[i].getRight() == node){
                arr[0] = parent.getArray()[i].getLeft();
                if (i<MAX_DEGREE-1 && parent.getArray()[i+1] != null){
                    arr[1] = parent.getArray()[i+1].getRight();
                }
                return arr;
            }
        }
        return arr;
    }


    public void find(String value){

        if (values.contains(value)){
            System.out.println("the way to find value is: ");
            Node current = findBestNode(value);
            System.out.print(root.toString());
            ArrayList<Node> arrayList = new ArrayList<>();
            do {
                arrayList.add(current);
                current = current
                        .getParent();
            }while (current.getParent() != null);
            for (int i = arrayList.size()-1; i >=0 ; i--) {
                System.out.print(" => " + arrayList.get(i));
            }
            System.out.println();

        }else {
            System.out.println("ERROR: value " + value + " does not exist!");
        }

    }



}
