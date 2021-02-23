import java.util.Scanner;

public class main {

    public static void main(String[] args) {

        Scanner input = new Scanner(System.in);

        /*


        here you define the MAX DEGREE



         */
        int maxDegree = 3;
        BPlusTree tree = new BPlusTree(maxDegree);

        while (true){
            System.out.print(">> ");
            String st = input.nextLine();
            if (st.isEmpty()) continue;
            if (st.equals("exit") || st.equals("finish")) break;
            String[] com = st.split(" ");
            if (com.length > 1){
                String command = com[0];
                if (command.equals("add")){
                    for (int i = 1; i < com.length; i++) {
                        tree.add(tree.root, com[i]);
                        System.out.println("value <" + com[i] + "> was added.");
                    }
                }else if (command.equals("del") || command.equals("delete") || command.equals("remove")){
                    for (int i = 1; i < com.length; i++) {
                        tree.delete(com[i]);
                    }
                }else if (command.equals("find") || command.equals("search")){
                    for (int i = 1; i < com.length; i++) {
                        tree.find(com[i]);
                    }
                }else {
                    System.out.println("ERROR: invalid command. try again!");
                    continue;
                }

            }else if (st.equals("show") || st.equals("print")){
                System.out.println("------------ THE TREE-------------");
                tree.buildTree(tree.root);
            } else {
                System.out.println("ERROR: invalid command. try again!");
                continue;
            }
        }


    }





}



