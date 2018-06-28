import java.io.*;
import java.util.*;
import java.util.Scanner;

public class Query {
    private String currentDir;
    private Scanner scanner = new Scanner(System.in);
    private PrintStream out = System.out;
    private List<String> readInput = new ArrayList<>();
    private String currentDBName = "none";

    private boolean hasDatabase = false;
    private boolean hasUSE = false;

    private boolean hasTables = false;


    private Databases currentdatabase;
    private PrintTable myPrint = new PrintTable();
    private Type myType = new Type();

    private List<String> dataBaseList;
    private List<String> tableList;

    private Map<String, File> dataBasesFileMap = new HashMap<String, File>();
    private List<String> dataBasesFileList = new ArrayList<String>();

    private Map<String, Databases> databasesMap = new HashMap<String, Databases>();
    private List<String> databaseNameList = new ArrayList<>();

    //This function
    Query() {
       currentDir = System.getProperty("user.dir");
       getDataBasesFileFolder();
       for(String item : dataBasesFileList) {
           //out.println(item);
           Databases db = new Databases();
           db.getTablesFilesFromFolder(dataBasesFileMap.get(item));
           db.getTablesFromFile();
           databasesMap.put(item, db);
       }
    }



    int getDataBasesFileFolder() {
        int folderNum = 0;
        dataBasesFileList.clear();
        dataBasesFileMap.clear();
        try{
            File file = new File(currentDir +"/DBLAB");
            File[] files = file.listFiles();
            if(files == null) {
                return 0;
            }
            for (File f : files) {
                if(f.isDirectory()) {
                    dataBasesFileList.add(f.getName());
                    dataBasesFileMap.put(f.getName(), f);
                    folderNum++;
                }
            }
            return folderNum;
        }
        catch (Exception e) {
            return 0;
        }
    }

    void readInput() {
        out.print("DB" + "[" + currentDBName + "]> ");
        String word = "";
        readInput.clear();
        while (true) {
            word = scanner.next();
            if (word.contains(",")) word = word.replace(",", "");
            if (word.contains("(")) word = word.replace("(", "");
            if (word.contains(")")) word = word.replace(")", "");
            if(word.equals("QUIT;") || word.equals("quit;")) {
                System.exit(1);
            }
            if(word.contains(";")) {
                word = word.replace(";","");
                readInput.add(word);
                break;
            }
            if(word.length() != 0) readInput.add(word);
        }
    }

    void printList(String title, List<String>content) {
        myPrint.printSingleColTable(title, content);
    }

    void showModel() {
        if(readInput.get(1).equals("DATABASES")) {
            //check safety!
            printList("Databases", dataBasesFileList);
            hasDatabase = true;
        }
        else if(hasDatabase && hasUSE && readInput.size() == 2
                && readInput.get(1).equals("TABLES")) {
            printList("Tables", tableList);
            hasTables = true;
        }
        else {
            out.println("Warning: InValid!");
        }
    }

    // get txt file and initialize the table
    //USE SHOW READ TABLELIST
    void useModel() {
        if (!hasDatabase){
            out.println("Warning: Please show database first!");
        }
        else if(readInput.size() != 2) {
            out.println("Warning: InValid length!");
        }
        else {
            currentdatabase = databasesMap.get(readInput.get(1));
            tableList = currentdatabase.getTablesNameList();
            if(currentdatabase == null) {
                out.println("Warning: You don't have this database!");
            }
            else {
                currentDBName = readInput.get(1);
            }
            hasUSE = true;
        }
    }

    //select id, gender, address,rank from players join teams on
    //players.team = teams.team where id > 10
    //SELECT * FROM players JOIN teams ON players.Team = teams.Team WHERE ID > 10;

    void selectModel() {
        String[] chosenField;
        String logicSymbol = null;
        String cmpField = null;
        String cmpValue = null;
        String tableA = null;
        String tableB = null;
        String joinFieldTA = null;
        String joinFieldTB = null;
        int fromI = readInput.indexOf("FROM");
        int joinI = readInput.indexOf("JOIN");
        int whereI = readInput.indexOf("WHERE");
        int onI = readInput.indexOf("ON");
        if(!hasUSE) {
            out.println("Warning: Please USE a database first!");
            return;
        }
        if(fromI == -1 ) {
            out.println("Warning: Invalid command, where is your FROM?");
            return;
        }
        chosenField = new String[fromI - 1];
        //for(String item : readInput.toArray(new String[0])) out.println(item);
        System.arraycopy(readInput.toArray(new String[0]),
                1, chosenField, 0, fromI - 1);
        if(whereI != -1) {
            if (readInput.size() - whereI < 4
                    || !myType.isLogicSymbol(readInput.get(whereI + 2))) {
                out.println("Warning: Invalid command!");
                return;
            }
            else {
                cmpField = readInput.get(whereI + 1);
                logicSymbol = readInput.get(whereI + 2);
                cmpValue = readInput.get(whereI + 3);
            }
        }

        if(joinI != -1) {

            if(onI - joinI != 2 || !readInput.get(onI + 2).equals("=")) {
                out.println("Warning: Invalid command3!");
                return;
            }
            tableA = readInput.get(joinI - 1);
            tableB = readInput.get(joinI + 1);
            //add try
            try{
                joinFieldTA = readInput.get(onI + 1).split("\\.")[1];
                joinFieldTB = readInput.get(onI + 3).split("\\.")[1];
            }
            catch (Exception e) {
                out.println("Warning: Invalid join field!");
                return;
            }

        }
        else {
            tableA = readInput.get(fromI + 1);
        }
        if(tableA == null || joinI != -1 &&  tableB == null) {
            out.println("Warning: Please make sure you choose a right table!");
            return;
        }
        if (! myType.checkNoEmpty(cmpField, cmpValue, joinFieldTA, joinFieldTB,
                logicSymbol, tableA, tableB)
                || ! myType.checkNoEmpty(chosenField)) {
            out.println("Warning: Please use valid input! Single '(.)' are not allow");
            return;
        }
        currentdatabase.printSelectTable(cmpField, cmpValue, joinFieldTA,
                joinFieldTB, logicSymbol, tableA, tableB, chosenField);
    }

    void insertModel() {
        String insertTableName = null;
        String[] fields;
        String[] values;
        //for(String item : readInput.toArray(new String[0])) out.println(item);
        if (!readInput.contains("INTO") || !readInput.contains("VALUES")) {
            out.println("Warning: Invalid command! Where is your INTO or VALUES");
            return;
        }
        int intoI = readInput.indexOf("INTO");
        int valuesI = readInput.indexOf("VALUES");
        if (readInput.size() < intoI || readInput.size() < valuesI || intoI > valuesI) {
            out.println("Warning: Invalid command! Does your sequence right?");
            return;
        }
        insertTableName = readInput.get(intoI + 1);
        if (!tableList.contains(insertTableName)) {
            out.println("Warning: Invalid table name!");
            return;
        }
        fields = new String[valuesI - intoI];
        System.arraycopy(readInput.toArray
                (new String[0]), intoI + 1, fields, 0, fields.length);
        if (currentdatabase.isValidFieldNames(insertTableName, fields)) {
            out.println("Warning: Invalid field name!");
            return;
        }
        values = new String[readInput.size() - valuesI - 1];
        System.arraycopy(readInput.toArray
                (new String[0]), valuesI + 1, values, 0, values.length);

        if(!myType.checkNoEmpty(insertTableName) || !myType.checkNoEmpty(values)) {
            out.println("Warning: Please use valid input! Single '(.)' are not allow");
        }
        if(!currentdatabase.insertIntoTable(insertTableName, values)) {
            out.println("Warning: Insert failed! You values is invalid!");
        }
        else {
            out.println("Warning: Insert successfully!");
        }
    }

    void creatModel() {
        if(hasUSE == false) {
            out.println("Warning: choose a database first!");
            return;
        }
        String tableName, keyField = "null";
        List<String> fieldName, typeName;
        boolean hasKey = false;

        if(!readInput.contains("TABLE") || readInput.size() < 6) {
            out.println("Warning: Where is your table? or your data has problems!");
            return;
        }
        int tableI = readInput.indexOf("TABLE");
        tableName = readInput.get(tableI + 1);
        int size = readInput.size();
        if((size - tableI - 2 ) % 3 != 0) {
            out.println("Warning: You data is invalid!");
        }
        fieldName = new ArrayList<>();
        typeName = new ArrayList<>();
        for (int i = tableI + 2; i < readInput.size(); i++) {
            if(i % 3 == 0) {
                fieldName.add(readInput.get(i));
            }
            else if(i % 3 == 1) {
                typeName.add(readInput.get(i));
            }
            else {
                if(readInput.get(i).equals("KEY")) {
                    //delete
                    hasKey = true;
                    keyField = readInput.get(i - 2);
                }
                else if(!readInput.get(i).equals("NOT")) {
                    out.println("Warning: Invalid key status!");
                    return;
                }
            }
        }
        //bug! MUST SHOW TABLES
        if(!myType.checkNoEmpty(tableName, keyField) ||
                !myType.checkNoEmpty(fieldName.toString()) ||
                !myType.checkNoEmpty(typeName.toString())) {
            out.println("Warning: Invalid input!");
            return;
        }
        if (currentdatabase.creatTable
                (tableName, keyField, hasKey,fieldName,typeName)) {
            tableList = currentdatabase.getTablesNameList();
            out.println("Warning: Create successfully");
        }
        else {
            out.println("Warning: Fail to create this table!");
        }

    }

    void deleteModel() {
        String tableName;
        String cmpField;
        String cmpValue;
        String symbol;
        if(readInput.size() != 7||!readInput.get(1).equals("FROM")
                || !readInput.get(3).equals("WHERE")) {
            out.println("Warning: Invalid delete command");
            return;
        }
        tableName = readInput.get(2);
        //out.println(tableList);
        if (!tableList.contains(tableName)) {
            out.println("Warning: Invalid table name!");
            return;
        }
        cmpField = readInput.get(4);
        symbol = readInput.get(5);
        if (!myType.isLogicSymbol(symbol)) {
            out.println("Warning: Invalid symbol!");
            return;
        }
        cmpValue = readInput.get(6);
        if(!myType.checkNoEmpty(tableName, cmpField, cmpValue,symbol)) {
            out.println("Warning: Please use valid input! Single '(.)' are not allow");
        }
        int num = currentdatabase.deleteFromTable
                (tableName, cmpField, cmpValue, symbol);
        out.println(num + " records have been deleted!");
    }

    void updateModel() {
        String tableName, chngField, newValue, cmpField, cmpValue, symbol;
        if(readInput.size() != 10 || readInput.indexOf("UPDATE") != 0 ||
                readInput.indexOf("SET") != 2 || readInput.indexOf("WHERE") != 6) {
            out.println("Warning: Invalid update command");
            return;
        }
        tableName = readInput.get(1);
        if (!tableList.contains(tableName)) {
            out.println("Warning: Invalid table name!");
            return;
        }
        chngField = readInput.get(3);
        newValue = readInput.get(5);
        cmpField = readInput.get(7);
        cmpValue = readInput.get(9);
        symbol = readInput.get(8);
        if (!myType.isLogicSymbol(symbol)) {
            out.println("Warning: Invalid symbol!");
            return;
        }
        if(!myType.checkNoEmpty(tableName, chngField, newValue,
                cmpField, cmpValue, symbol)) {
            out.println("Warning: Please use valid input! Single '(.)' are not allow");
            return;
        }
        int num = currentdatabase.updateData(tableName, chngField,
                newValue, cmpField, cmpValue, symbol);
        out.println(num + " records have been updated!");

    }

    void saveModel() {
        if(readInput.size() != 2) {
            out.println("Warning: Invalid save command!");
            return;
        }
        String tableName = readInput.get(1);
        if (!tableList.contains(tableName)) {
            out.println("Warning: Invalid table name!");
            return;
        }
        if (currentdatabase.saveTable(tableName)) {
            out.println("Warning: Successfully saved!");
        }
    }

    void run() {
        out.println("Home-Made Database.........");
        out.println("ALL THE COMMAND SHOULD BE IN THE CAPITAL\n");
        out.println("This is an example:");
        out.println("SELECT ID, Name FROM players WHERE ID > 10;\n");
        while(true) {

            readInput();
            long startTime = System.currentTimeMillis();
            long endTime;
            try{
                if (readInput.get(0).equals("SHOW")) {
                    showModel();
                }
                else if (readInput.get(0).equals("USE")) {
                    useModel();
                }
                else if (readInput.get(0).equals("SELECT")) {
                    selectModel();
                }
                else if (readInput.get(0).equals("INSERT")) {
                    insertModel();
                }
                else if (readInput.get(0).equals("UPDATE")) {
                    updateModel();
                }
                else if(readInput.get(0).equals("SAVE")) {
                    saveModel();
                }
                else if (readInput.get(0).equals("DELETE")) {
                    deleteModel();
                }
                else if (readInput.get(0).equals("CREATE")) {
                    creatModel();
                }
                else{
                    out.println("Unknow command!");
                }
                endTime = System.currentTimeMillis();
                out.println("Running time："+(endTime-startTime)+"ms");
            }
            catch (Exception e) {
                out.println("Unknow Error! Please ontact cg17795@my.bristol.ac.uk.");
            }

        }
    }

    public static void main(String[] args) {
        Query program = new Query();
        program.run();
    }
}
