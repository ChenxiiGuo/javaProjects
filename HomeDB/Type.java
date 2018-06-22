/*
This class is a "police". It is used to check type, symbol...
 */
import java.util.List;
import java.util.regex.Pattern;

public class Type {
    String pattern = "^[-\\+]?[\\d]*$";

    boolean isLogicSymbol(String s) {
        if (s.equals("<") || s.equals(">") || s.equals("=") || s.equals("><")) {
            return true;
        }
        else {
            return false;
        }
    }

    boolean isValidType(String s) {
        if (s.equals("Integer") || s.equals("Long") ||s.equals("String")) {
            return true;
        }
        else {
            return false;
        }
    }

    // In my table, the type of data is stored in a list, like "Integer, String, Long".
    // This function is used to make sure no invalid type. My design only support:Integer, String, Long
    boolean isValidTypeList(List<String> list, int colNum) {
        if(list.size() != colNum) return false;
        for(String item : list) {
            if(!isValidType(item)) {
                return false;
            }
        }
        return true;
    }

    boolean isInteger(String s) {
        return checkType(s, "Integer");

    }

    boolean isLong (String s) {
        return checkType(s, "Long");

    }

    // give a string s and a type, check whether it is valid type.
    // s = jk, type = Integer, this will return false
    // s = 1, type = Long, this will return true
    boolean checkType(String s, String type) {
        if(s.equals("null")) return true;
        if(s.length() == 0) return true;
        try{
            s = s.charAt(0)== '+' ? s.split("\\+")[1] : s;
            if(type.equals("Integer")) {
                return Pattern.matches(pattern, s) && Integer.toString(Integer.parseInt(s)).equals(s);
            }
            else if (type.equals("Long")) {
                return Pattern.matches(pattern, s) && Long.toString(Long.parseLong(s)).equals(s);
            }
            else {
                return true;
            }
        }
        catch (Exception e) {
            return false;
        }
    }


    //In query, sometimes the input by user is invalid. This is used to check if there is "".
    boolean checkNoEmpty(String ...Arguments) {
        for (int i = 0; i < Arguments.length; i++) {
            if (Arguments[i] == null) continue;
            if (Arguments[i].replace(" ", "").length() == 0) {
                return false;
            }
        }
        return true;
    }

    private  int testNumber = 0;
    void claim(boolean b) {
        if (!b) throw new Error("Test " + testNumber + " fails");
        testNumber++;
    }

    void test() {
        System.out.println("Test start!");
        claim(isInteger("A9") == false);
        claim(isInteger("*&776") == false);
        claim(isInteger("fino") == false);
        claim(isInteger("2147483648") == false );
        claim(isInteger("-2147483649") == false);
        claim(isInteger("-2147483648") == true);
        claim(isInteger("2147483647") == true );
        claim(isInteger("+4") == true);
        claim(isInteger("") == true);
        claim(isInteger("+") == false);
        claim(isInteger("-") == false);

        claim(isLong("A9") == false);
        claim(isLong("*&776") == false);
        claim(isLong("fino") == false);
        claim(isLong("9223372036854775808") == false );
        claim(isLong("-9223372036854775809") == false);
        claim(isLong("-9223372036854775808") == true);
        claim(isLong("9223372036854775807") == true );
        claim(isLong("+4") == true);
        claim(isLong("") == true);
        claim(isLong("+") == false);
        claim(isLong("-") == false);
        System.out.println("Test finish!");



    }

    void run() {
        test();
    }
    public static void main(String[] args) {
        Type program = new Type();
        program.run();

    }
}
