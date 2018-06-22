/*
This is used to pass logic from input to other function.
I set it because I support relative complex logic.
For integer and long, my function support <, =, >, ><
For string, my function support = and ><
 */

public class Logic {
    private String currentLogic;
    //String and Integer use different logic
    private String logicType;

    //new Loigc is logic symbol.
    Logic(String newLogic) {
        currentLogic = newLogic;
    }

    boolean setLogicType(String type) {
        if(type.equals("String") &&(currentLogic.equals(">") || currentLogic.equals("<"))) {
            return false;
        }
        logicType = type;
        return true;
    }

    String getLogic() {
        return currentLogic;
    }

    boolean checkLogic(String target, String compared) {
        if(logicType.equals("String")) {
            return checkLoigcForString(target, compared);
        }
        else {
            return checkLogicForNum(target, compared);
        }
    }

    boolean checkLoigcForString(String target, String compared) {
        if (currentLogic.equals("=")) {
            return target.equals(compared);
        }
        else if (currentLogic.equals("><")) {
            return !target.equals(compared);
        }
        else {
            System.out.println("Illegal compare!");
            return false;
        }

    }



    boolean checkLogicForNum(String target, String compared) {
        long targetL = Long.parseLong(target);
        long comparedL = Long.parseLong(compared);
        if (currentLogic.equals("=")) {
            return targetL == comparedL;
        }
        else if (currentLogic.equals("<")) {
            return targetL < comparedL;
        }
        else if (currentLogic.equals(">")) {
            return targetL > comparedL;
        }
        else if (currentLogic.equals("><")) {
            return targetL != comparedL;
        }
        else return false;
    }

}
