/*This class is the basic unit of the whole database.
Arraylist is used to store a piece of data. (Like "23", "Jordan", "Bulls")

 */

import  java.util.*;
public class Record {
    private List<String> datas = new ArrayList<>();


    Record(String ...StringArray) {
        setRecord(StringArray);
    }

    Record(List<String> list) {
        String[] StringArray = list.toArray(new String[0]);
        setRecord(StringArray);
    }

    public int size() {
        return datas.size();
    }

    void setRecord(String ...StringArray) {
        datas.clear();
        datas.addAll(Arrays.asList(StringArray));
    }

    void changeData(int index, String newData) {
        if(index >= datas.size()) {
            return;
        }
        datas.set(index, newData);
    }

    void addCol(int num) {
        for(int i = num; i > 0; i--) {
            datas.add("null");
        }
    }

    // For safe, make copy
    List<String> toList() {
        List<String> res = new ArrayList<>();
        for(int i = 0; i < datas.size(); i++) {
            res.add(datas.get(i));
        }
        return res;
    }

    String getData(int index) {
        return datas.get(index);
    }


    private  int testNumber = 0;
    void claim(boolean b) {
        if (!b) throw new Error("Test " + testNumber + " fails");
        testNumber++;
    }

    void test() {
        System.out.println("Test start!");
        claim(size() == 3);
        changeData(0, "55");
        claim(getData(0).equals("55"));
        addCol(5);
        claim(size() == 8);
        setRecord("23", "Jordan", "Bulls");
        claim(size() == 3);
        claim(getData(1).equals("Jordan"));
        System.out.println("Test finish");
    }

    public static void main(String[] args) {
        Record program = new Record("35" ,"Curry", "GSW");
        program.test();
    }





}
