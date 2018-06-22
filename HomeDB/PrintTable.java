import java.util.ArrayList;
import java.util.List;

public class PrintTable {

    void print(List<List<String>> content) {
        int sum = 0;
        List<Integer> getDataWidth = getWidth(content);
        List<String> typeNames = content.get(2);
        for(Integer item : getDataWidth) {
            sum += item;
        }
        printLine(sum, getDataWidth.size());
        for(int i = 1; i < content.size(); i++) {
            if(i == 2) continue;
            for(int j = 0; j < getDataWidth.size(); j++) {
                System.out.print("|");
                String data = content.get(i).get(j);
                String space = addSpace("", getDataWidth.get(j) - data.length());
                if(typeNames != null && (typeNames.get(j).equals("String") || i == 1)) {
                    System.out.print(data + space);
                }
                else {
                    System.out.print(space + data);
                }
            }
            System.out.print("|\n");
            if(i == 1) {
                printLine(sum, getDataWidth.size());
            }

        }
        printLine(sum, getDataWidth.size());
        System.out.println("Totally " + (content.size() - 3) + " rows");
    }

    void printSingleColTable(String title, List<String> content) {
        int width = title.length();
        for(String item : content) {
            width = Math.max(width, item.length());
        }
        printLine(width, 1);

        title = addSpace(title, width);
        System.out.println("|" + title + "|");
        printLine(width, 1);
        for(String item : content) {

            item = addSpace(item, width);
            System.out.println("|" + item + "|");
        }
        printLine(width, 1);

    }

    String addSpace(String s, int length) {
        StringBuffer sb = new StringBuffer(s);
        for(int i = 0; i < length - s.length(); i++) {
            sb.append(" ");
        }
        return sb.toString();
    }


    //content's first list is key's field name
    //content's second line is field name
    //content's third line is type

    List<Integer> getWidth(List<List<String>> content) {
        List<Integer> widthList = new ArrayList<Integer>();
        for(int i = 0; i < content.get(1).size(); i++) widthList.add(0);
        for(int i = 1; i < content.size(); i++) {
            if(i == 2) continue;
            for(int j = 0; j < content.get(i).size(); j++) {
                widthList.set(j, Math.max(widthList.get(j), content.get(i).get(j).length()));
            }
        }
        return widthList;
    }

    void printLine(int numOfChar, int numOfContent) {
        System.out.print("+");
        while(numOfChar + numOfContent - 1 > 0) {
            numOfChar--;
            System.out.print("-");
        }
        System.out.print("+\n");
    }
}
