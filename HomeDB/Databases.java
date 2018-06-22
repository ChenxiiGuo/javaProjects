/*This class is used to deal with tables relationship and put tables together
as a real database
 */
import  java.util.*;
import  java.io.*;
public class Databases {
    private PrintStream out = System.out;
    private Map<String, Table> allTableMap = new HashMap<String, Table>();



    private List<String> tablesNameList = new ArrayList<String>();
    private Map <String, File> tablesFileMap = new HashMap<>();
    private PrintTable myPrint = new PrintTable();
    private Type myType = new Type();


    //argument file is a folder
    //the return value is the number of txt files in this folder
    int getTablesFilesFromFolder (File file) {
        int tableNum = 0;
        if(file == null) return 0;
        tablesFileMap.clear();
        tablesNameList.clear();
        try {
            File[] files = file.listFiles();
            if(files == null) return 0;
            for(File f : files) {
                if(f.isFile() && f.getName().endsWith(".txt")) {
                    tablesFileMap.put(f.getName().split("\\.")[0], f);
                    tablesNameList.add(f.getName().split("\\.")[0]);
                    tableNum++;
                }
            }
            return tableNum;
        }
        catch (Exception e) {
            return 0;
        }
    }
    //use constructor Table(dir) to construct all tables
    //and put these table objects into map
    int getTablesFromFile() {
        int tableNum = 0;
        for(int i = 0; i < tablesNameList.size(); i++) {
            File newFile = tablesFileMap.get(tablesNameList.get(i));
            Table table = new Table(newFile.getAbsolutePath());
            allTableMap.put(tablesNameList.get(i), table);
            tableNum ++;
        }
        return tableNum;
    }

    //This function is used to 1. select table. 2.print
    //The arguments meaning is:

    //SELECT fields FROM tableAS JOIN tableBS
    // ON fieldsA = fieldsB WHERE cmpField symbol cmpValue;

    //SELECT ID, Team, Rank FROM players JOIN teams
    // ON players.Team = teams.Team WHERE ID > 10;
    //printSelectTable("Name", "Chenxi", null, null, ">", players, null, "ID", "Name");
    void printSelectTable(String cmpField, String cmpValue, String fieldA,
                          String fieldB, String symbol, String tableAS,
                          String tableBS, String ...fields) {
        Table tableA, tableB = null;
        Logic logic = null;
        tableA = allTableMap.get(tableAS);
        if(tableA == null) {
            out.println("Please make sure you choose a right table!");
            return;
        }
        if(tableBS != null){
            tableB = allTableMap.get(tableBS);
            if(tableB == null) {
                out.println("Please make sure you choose a right table!");
                return;
            }
        }
        Table jTable = tableB != null ? tableA.join(tableB, fieldA, fieldB) :
                tableA;
        if(jTable == null) return;
        if(symbol != null) {
            if(! myType.isLogicSymbol(symbol)) {
                out.println("This compare is illegal");
                return;
            }
            logic = new Logic(symbol);
        }
        Table logicTable = logic != null ?
                jTable.selectTableByLogic(cmpField, cmpValue, logic) : jTable;
        if(logicTable == null) return;
        Table res = logicTable.selectChosenField(fields);
        if(res != null) {
            myPrint.print(res.printToStringArray());
        }
    }


    // return value is the number of record which has been removed.
    int deleteFromTable(String tableName, String cmpField,
                        String cmpValue, String symbol) {
        Table table = allTableMap.get(tableName);
        if (table == null) {
            out.println("No such table!");
            return 0;
        }
        if(! myType.isLogicSymbol(symbol)) {
            out.println("This compare is illegal");
            return 0;
        }
        Logic logic = new Logic(symbol);
        return table.deleteByLoigc(cmpField, cmpValue, logic);
    }

    // return value is the number of record which has been updated.
    int updateData(String tableName, String chngField, String newValye,
                   String cmpField, String cmpValue, String symbol) {
        Table table = allTableMap.get(tableName);
        if (table == null) {
            out.println("No such table!");
            return 0;
        }
        if(! myType.isLogicSymbol(symbol)) {
            out.println("This compare is illegal");
            return 0;
        }
        Logic logic = new Logic(symbol);
        return table.updateTable(chngField,
                newValye, cmpField, cmpValue, logic);
    }

    //write table to txt file
    boolean saveTable(String tableName) {
        Table table = allTableMap.get(tableName);
        if (table == null) {
            out.println("No such table!");
            return false;
        }
        File file = tablesFileMap.get(tableName);
        return table.writeToFile(file.getAbsolutePath());
    }


    List<String> getTablesNameList() {
        return getCopyList(tablesNameList);
    }

    boolean insertIntoTable(String tableName, String ...data) {
        Table table = allTableMap.get(tableName);
        if (table == null) {
            out.println("No such table!");
            return false;
        }
        if(table == null) {
            out.println("Your table name has some problems!");
            return false;
        }
        return table.insert(data);

    }




    boolean isValidFieldNames(String tableName, String ...fields) {
        Table table = allTableMap.get(tableName);
        if (table == null) {
            out.println("No such table!");
            return false;
        }
        List<String> fieldList = table.getFieldNames();
        if(fieldList.size() != fields.length) return false;
        for(int i = 0; i < fieldList.size(); i++) {
            if(!fieldList.get(i).equals(fields[i])) {
                return false;
            }
        }
        return true;
    }

    boolean creatTable(String tableName, String keyField, boolean hasKey,
                       List<String> fields, List<String> types) {
        String[] fieldArray = fields.toArray(new String[0]);
        String[] typeArray = types.toArray(new String[0]);
        String[] fieldAndTypeArray =
                new String[fieldArray.length + typeArray.length + 1];
        System.arraycopy(fieldArray, 0,
                fieldAndTypeArray, 0, fieldArray.length);
        fieldAndTypeArray [fieldArray.length] = ":";
        System.arraycopy(typeArray, 0,
                fieldAndTypeArray, fieldArray.length + 1, typeArray.length);
        try{
            Table table = new Table(keyField, hasKey, fieldAndTypeArray);
            allTableMap.put(tableName, table);
            tablesNameList.add(tableName);
            return true;
        }
        catch (Exception e) {
            return false;
        }
    }




    List<String> getCopyList(List<String> list) {
        List<String> res = new ArrayList<>();
        for(String item : list) {
            res.add(item);
        }
        return res;
    }


    private  int testNumber = 0;
    void claim(boolean b) {
        if (!b) throw new Error("Test " + testNumber + " fails");
        testNumber++;
    }

    void testGetTablesFilesFromFolder() {
        out.println( _FUNC_() + " Start!");
        String currentDir = System.getProperty("user.dir");
        File file = new File(currentDir +"/DBLAB/NBA");
        claim(getTablesFilesFromFolder(file) == 3);
        claim(tablesNameList.contains("players"));
        claim(tablesNameList.contains("teams"));
        claim(tablesNameList.contains("detail"));
        out.println( _FUNC_() + " end!\n");
    }

    void testGetTablesFromFile() {
        out.println( _FUNC_() + " Start!");
        claim(getTablesFromFile() == 3);
        out.println( _FUNC_() + " end!\n");
    }

    void testPrintSelectTable() {
        out.println( _FUNC_() + " Start!");
        out.println("Simple print");
        printSelectTable(null, null, null, null, null, "players", null, "*");
        out.println("Conditional select");
        printSelectTable("Name", "Chenxi", null, null,
                ">", "players", null, "ID", "Name");
        out.println("This is complex join table");
        printSelectTable("ID", "10", "Team",
                "Team", ">", "players", "teams", "*");
        out.println("Following warning are caused by intentionally invalid input!");
        printSelectTable("ID", "10", "Teasd",
                "Team", ">", "players", "teams", "*");
        printSelectTable(null, null, null, null,
                null, "players", null, "monkey");
        out.println( _FUNC_() + " end!\n");

    }
    void testDeleteFromTable () {
        out.println( _FUNC_() + " Start!");
        printSelectTable(null, null, null, null, null, "players", null, "*");
        claim(deleteFromTable("players", "Name", "Pabu", "=") == 1);
        printSelectTable(null, null, null, null, null, "players", null, "*");
        out.println("You can see Pabu is removed!");
        out.println("Following warning are caused by intentionally invalid input!");
        claim(deleteFromTable("players", "Name", "Curry", "*") == 0);
        claim(deleteFromTable("pls", "Name", "Curry", "*") == 0);
        out.println( _FUNC_() + " end!\n");
    }

    void testUpdateData () {
        out.println( _FUNC_() + " Start!");
        printSelectTable(null, null, null, null, null, "players", null, "*");
        claim(updateData("players", "Gender", "Male",
                "Name", "Chenxi", "=") == 1);
        printSelectTable(null, null, null, null, null, "players", null, "*");
        out.println("You can see Chenxi's Gender changed");
        claim(updateData("players", "Team", "Bristol",
                "ID", "0", ">") == 5);
        printSelectTable(null, null, null, null, null, "players", null, "*");
        out.println("You shoud notice that all team is Bristol now!");
        out.println("Following warning are caused by intentionally invalid input!");
        updateData("", "Team", "Bristol",
                "ID", "0", ">");
        updateData("players", "Team", "Bristol",
                "ID", "0", "^");
        updateData("players", "Salary", "Bristol",
                "ID", "0", ">");
        out.println( _FUNC_() + " end!\n");
    }

    void testinsertIntoTable() {
        out.println( _FUNC_() + " Start!");
        out.println("Following warning are caused by intentionally invalid input!");
        claim(insertIntoTable("players","35", "Durant", "Male", "GSW") ==true);
        claim(insertIntoTable("players","35", "Durant", "Male", "GSW") ==false);
        claim(insertIntoTable("play","3", "David", "Male", "GSW") ==false);
        claim(insertIntoTable("players","3", "David", "Male") ==false);
        claim(insertIntoTable("players","3", "David", "Male","3","k") ==false);
        out.println( _FUNC_() + " end!\n");
    }

    void test() {
        System.out.println("Test Start!");
        testGetTablesFilesFromFolder();
        testGetTablesFromFile();
        testPrintSelectTable();
        testDeleteFromTable();
        testUpdateData();
        testinsertIntoTable();
        System.out.println("Test end!\n");
    }

    void run(String[] args){
        if(args.length != 0){
            System.err.println("There should no argument");
            System.exit(1);
        }
        test();
    }

    public static void main(String[] args) {
        Databases program = new Databases();
        program.run(args);
    }

    public String _FUNC_() {
        StackTraceElement traceElement =((new Exception()).getStackTrace())[1];
        return traceElement.getMethodName();
    }

}
