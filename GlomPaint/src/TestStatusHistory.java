
//This class is used to test StatusHistory

public class TestStatusHistory {



    private int testNumber = 1;

    void claim(boolean b) {
        if (!b) throw new Error("Test " + testNumber + " fails");
        testNumber++;
    }

    void testForInteger() {
        StatusHistory<Integer> testInt = new StatusHistory<>();
        testInt.addToHistory(1);
        testInt.addToHistory(2);
        testInt.addToHistory(3);
        testInt.addToHistory(4);
        testInt.addToHistory(5);
        claim(testInt.size() == 5);
        int status =testInt.undo();
        claim(status == 4);
        status = testInt.undo();
        claim(status == 3);
        testInt.undo();
        testInt.undo();
        claim(testInt.undo() == null);
        claim(testInt.redo() == 1);
        testInt.redo();
        testInt.redo();
        testInt.redo();
        claim(testInt.redo() == 5);
        claim(testInt.redo() == null);
        System.out.println("Test For Integer Finished!");
    }

    void testForString() {
        StatusHistory<String> testString = new StatusHistory<>();
        claim(testString.redo() == null);
        claim(testString.undo() == null);
        testString.addToHistory("After");
        testString.addToHistory("Bag");
        claim(testString.undo().equals("After"));
        testString.redo();
        claim(testString.undo().equals("After"));
        testString.redo();
        testString.addToHistory("Cat");
        testString.addToHistory("Dog");
        testString.addToHistory("Elephant");
        claim(testString.size() == 5);
        testString.undo();
        testString.undo();
        testString.undo();
        testString.addToHistory("Fly");
        claim(testString.redo() == null);

    }

    void test() {
        testForInteger();
        testForString();

        System.out.println("Test Finished!");
    }


    void run(String[] args){
        if(args.length != 0){
            System.err.println("There should no argument");
            System.exit(1);
        }
        test();

    }

    public static void main(String[] args){
        TestStatusHistory program = new TestStatusHistory ();
        program.run(args);
    }
}
