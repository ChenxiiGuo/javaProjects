/* This class is used to stored records and manipulate there records.
To store there records, I use a treemap. 
If the user don't set a key, then the key is row number.
If the user set a key, then "the key of this treemap" is this key.
I choose treemap, because of two reason. First, if you select or search a record by key, 
the time complexity will
be only o(logN). Second, hashmap/linkedhashmap seems good (O(1)), but it needs more memory.
Considering time complexity and memory, I choose treemap.
 */
import java.io.*;
import  java.util.*;

public class Table {
    private PrintStream out = System.out;
    //This is where I store my records.
    private Map<String, Record> records = new TreeMap<String, Record>();
    //This hashmap makes me easy to get the column number.
    //For example: ID->0 Name->1 Gender->2
    private Map<String, Integer> fieldNameMap = new HashMap<String, Integer>();
    private int colNum = 0;
    //hasKey means whether user set a key. If not, the default key is row number.
    private boolean hasKey = false;

    //If not key, then keyField = "null".
    //If has key, keyField will be the fieldName, like ID.
    private String keyField = "null";
    private List<String> fieldNames = new ArrayList<String>();
    //typeName is like Ingeter, String, String, Long
    private List<String> typeNames = new ArrayList<String>();

    //This is used to print. In my design, Table have no ability to print.
    //an List<List<String>> (a copy) and pass it to this class.
    private PrintTable printTable = new PrintTable();
    private Type typeCheck = new Type();

    //Construct a table from a file. The file have it own structure.
    Table (String dir){

        readWholeFile(dir);
    }

    // Construct a table from field names and types.
    // For example: "ID" "Name" "Salary" ":" "Integer" "String" "Integer"
    Table(String keyName, boolean hasKey, String ...fieldNameAndType) {
        this.hasKey = hasKey;
        if(hasKey) keyField = keyName;
        addColumn(fieldNameAndType);

    }


    //construct a table from a standard List file
    //It is similar to construct from a file. But I didn't choose to read
    //a file to a list then use this constructor, because it is a waste of memory.
    Table(List<List<String>> list) {
        if(!list.get(0).get(0).equals("null")) {
            hasKey = true;
            keyField = list.get(0).get(0);
        }
        fieldNames = list.get(1);
        colNum = fieldNames.size();
        for(int i = 0; i < fieldNames.size(); i++) {
            fieldNameMap.put(fieldNames.get(i), i);
        }
        typeNames = list.get(2);
        for(int i = 3; i < list.size(); i++) {
            Record newRecord = new Record(list.get(i));
            insert(newRecord);
        }

    }


    //if can't add columns, return false
    //fieldNameAndType contains both field name and type
    // For example: "ID" "Name" "Salary" ":" "Integer" "String" "Integer"
    private boolean addColumn(String ...fieldNameAndType) {
        int half = fieldNameAndType.length / 2;
        int origColNum = colNum;
        if(!fieldNameAndType[half].equals(":")) {
            out.println("\nWarning: Invalid input!! failed!");
            return false;
        }
        else {
            String[] fieldArray = new String[half];
            String[] typeArray = new String[half];
            System.arraycopy(fieldNameAndType,0,fieldArray,0,half);
            System.arraycopy(fieldNameAndType,half + 1,typeArray,0,half);

            for(String item : typeArray) {
                if(!typeCheck.isValidType(item)) {
                    out.println("\nWarning: Invalid type name!");
                    return false;
                }
            }

            colNum = colNum + half;
            for (int i = 0; i < fieldArray.length; i++) {
                fieldNameMap.put(fieldArray[i], origColNum + i);
                fieldNames.add(fieldArray[i]);
                typeNames.add(typeArray[i]);
            }
            for(Map.Entry<String, Record> item : records.entrySet()) {
                Record record = item.getValue();
                record.addCol(half);
            }
            return true;
        }

    }


    //This function is used to change the value, so it is OK to not be private
    //If insert successfully, return true, else return false
    //Before insert, I will check whether the record is valid.
    //If this table has key, I will check whether the key is already exist.
    boolean insert(Record newRecord) {
        if (colNum != newRecord.size() || colNum == -1) {
            out.println("Warning: "+ newRecord.toList() +" is Invalid Input!");
            out.println("This record has been ignored!");
            return false;
        }
        else if (records.containsKey(getKeyOrRow(newRecord))) {
            out.println("Warning: " +
                    "You can't have two same key if you set a key!");
            out.println("This record has been ignored!");
            return false;
        }
        else if (checkRecordType(newRecord) == false) {
            out.println("Warning: Your record's type has some problem!");
            return false;
        }
        else {
            records.put(getKeyOrRow(newRecord), newRecord);
            return true;
        }
    }

    //Insert "23", "Jordan", "Gender", like this kind of data.
    boolean insert(String ...StringArray) {
        if(StringArray.length != fieldNames.size()) {
            return false;
        }
        Record newRecord = new Record(StringArray);
        return insert(newRecord);
    }


    //Because this return value is the original data, I set it as private
    //I will have another function that work as a socket for other class.
    private Record selectByKeyOrRow(String keyOrRow) {
        if (records.get(keyOrRow) == null) {
            out.println("Warning :No result!");
        }
        return records.get(keyOrRow);
    }

    //If no key, it is possible that there are two same record
    //so the return value is a list
    private List<Record> selectByRecord(Record selRecord) {
        List<Record> res = new ArrayList<Record>();
        if (hasKey) {
            String selkey = selRecord.getData(fieldNameMap.get(keyField));
            Record record = selectByKeyOrRow(selkey);
            if(record != null) {
                res.add(record);
            }
        }
        else {
            List<Integer> rowNum = getRowNumByRecord(selRecord);
            for(Integer item : rowNum) {
                Record record = selectByKeyOrRow(Integer.toString(item));
                if(record != null) {
                    res.add(record);
                }            }
        }
        return res;
    }

    // field name = id, targetString = 23, so it will return "23", "Jordan", "Bulls"
    // This function was written before I did extension.
    // Actually, it has never been used in extension part.
    private List<Record> selectByOneField(String fieldName, String targetString) {
        List<Record> res = new ArrayList<Record>();
        if (fieldNameMap.containsKey(fieldName) == false) return res;
        if (hasKey && fieldName.equals(keyField)) {
            res.add(records.get(targetString));
        }
        else {
            for(Map.Entry<String, Record> item : records.entrySet()) {
                Record temp = item.getValue();
                if(temp.getData(fieldNameMap.get(fieldName)).equals(targetString)) {
                    res.add(temp);
                }
            }

        }

        return res;
    }

    //This is for extension, and also the most important function.
    //fieldName logic cmpValue : "ID" > 25;
    //For String, the permit logic symbol is = and ><.
    //For Integer, the permit logic symbol is =, >, < and ><
    //Select records by logic and return them.
    //Since its return value is not a deep copy， it is private.
    private List<Record> selectByLogic(String fieldName,
                                       String cmpValue, Logic logic) {
        List<Record> res = new ArrayList<Record>();
        if (fieldNameMap.get(fieldName) == null) {
            out.println("Warning: Your field name in comparison is wrong!");
            return res;
        }
        else if (!typeCheck.checkType(cmpValue,
                typeNames.get(fieldNameMap.get(fieldName)))) {
            out.println("Warning: Your type has error!");
            return res;
        }
        String type = typeNames.get(fieldNameMap.get(fieldName));
        if (logic.setLogicType(type) == false) {
            out.println("Warning: You can't used < or > to string!");
            return res;
        }
        if (logic.getLogic().equals("=") && fieldName.equals(keyField)) {
            Record temp = selectByKeyOrRow(cmpValue);
            if(temp != null) {
                res.add(temp);
            }
        }
        else {
            for (Map.Entry<String, Record> item : records.entrySet()) {
                Record temp = item.getValue();
                //List<String> fieldNamesCopy = new ArrayList<>(fieldNames);
                if (logic.checkLogic(
                        temp.getData(fieldNameMap.get(fieldName)), cmpValue)) {
                    res.add(temp);
                }
            }
        }
        return res;
    }

    // Select record by data itself, like "23", "Jordan", "Male"
    private List<Record> selectByString (String ...StringArray) {
        List<Record> res = new ArrayList<Record>();
        if (StringArray.length != colNum) {
            return res;
        }
        else {
            Record selRecord = new Record(StringArray);
            return selectByRecord(selRecord);
        }
    }

    private List<Integer> getRowNumByRecord(Record selRecord) {
        List<Integer> res = new ArrayList<Integer>();
        for (int i = 0; i < records.size(); i++) {
            if (isSameRecord(records.get(Integer.toString(i)), selRecord)) {
                res.add(i);
            }
        }
        return res;
    }

    // return the number of records that have been changed
    // when there is a key, then this function need to make sure that
    // changing key's value will not generate two record have same key
    int updateByKey(String key, String fieldName, String newValue) {
        Record recordToChange = selectByKeyOrRow(key);
        if (recordToChange == null) return 0;
        if (!fieldNames.contains(fieldName)) {
            out.println("Warning: No such field!");
            return 0;
        }
        if (hasKey && fieldName.equals(keyField)
                && selectByKeyOrRow(newValue) != null) {
            out.println("Warning: Invalid! Can't have two same key!");
            return 0;
        }
        else if (!typeCheck.checkType(newValue,
                typeNames.get(fieldNameMap.get(fieldName)))) {
            return 0;
        }
        recordToChange.changeData(fieldNameMap.get(fieldName), newValue);
        return 1;
    }

    //If no key, t is possible that there are identical records in this table.
    //The return value is the number of record that has been changed.
    int updateByRecord (Record oldRecord, String fieldName, String newValue) {
        int res = 0;
        if(hasKey) {
            return updateByKey(oldRecord.getData(fieldNameMap.get(keyField)),
                    fieldName, newValue);
        }
        else {
            List<Record> selRecord = selectByRecord(oldRecord);
            for(Record item : selRecord) {
                if(typeCheck.checkType(newValue,
                        typeNames.get(fieldNameMap.get(fieldName)))) {
                    item.changeData(fieldNameMap.get(fieldName), newValue);
                    res++;
                }
            }
            return res;
        }
    }

    //  ":" is used between the string of record and fieldName
    // StringArray should be "field1 field2...fieldN : fieldName newValue
    int updataByString (String ...StringArray) {
        if(StringArray.length != colNum + 3
                || !StringArray[StringArray.length -3].equals(":")) {
            return 0;
        }
        int len = StringArray.length;
        String[] newString = new String[len - 3];
        System.arraycopy(StringArray, 0, newString, 0, len- 3);
        Record oldRecord = new Record(newString);
        return updateByRecord(oldRecord,
                StringArray[len - 2], StringArray[len - 1]);
    }

    // return the number of key that has been removed
    private int removeKey(String ...keyArray) {
        int res = 0;
        for (int i = 0; i < keyArray.length; i++) {
            if (records.containsKey(keyArray[i])) {
                res++;
                records.remove(keyArray[i]);
            }
        }
        return res;
    }

    // Since this function is used to change the table, it is not private
    // return value is the number of record that have been deleted
    // If no key, I will delete it by row number. The row number is the key.
    // But I need to update other key(row number), so the time complexity is O(n).
    // If there is key, the time complexity is O(logN)
    int deleteByKey (String ...keyArray) {
        if (hasKey) {
            return removeKey(keyArray);
        }
        else {
            int origRecordNum = records.size();
            int numOfRemove = removeKey(keyArray);

            if(numOfRemove == 0) return numOfRemove;

            int nextNoEmptyKey = 0;

            int currentSize = origRecordNum - numOfRemove;
            for (int i = 0; i < currentSize; i++) {
                String index = Integer.toString(i);
                while(nextNoEmptyKey < i ||
                       !records.containsKey(Integer.toString(nextNoEmptyKey))){
                    nextNoEmptyKey++;

                }

                records.put(index,
                        records.get(Integer.toString(nextNoEmptyKey)));
                nextNoEmptyKey++;
            }

            for(int i = origRecordNum - numOfRemove; i < origRecordNum; i++) {
                String index = Integer.toString(i);
                records.remove(index);
            }

            return numOfRemove;
        }
    }


    int deleteByRecord (Record delRecord) {
        if(delRecord.size() != colNum) return 0;
        List<String> delArray = new ArrayList<String>();

        if (!hasKey) {
            for (int i = 0; i < records.size(); i++) {
                if(isSameRecord(records.get(Integer.toString(i)), delRecord)) {
                    delArray.add(Integer.toString(i));
                }
            }
            return deleteByKey((String[])delArray.toArray(new String[0]));
        }
        else {
            String delKey = delRecord.getData(fieldNameMap.get(keyField));
            return deleteByKey(delKey);
        }
    }

    int deleteByString(String ...StringArray) {
        Record delRecord = new Record(StringArray);
        return deleteByRecord(delRecord);
    }

    // like deleteByOneField(ID, "23") removes the "23" "Jordan" "Bulls"
    int deleteByOneField(String fieldName, String targetValue) {
        List<Record> toDelete = selectByOneField(fieldName, targetValue);
        for(Record item : toDelete) {
            deleteByRecord(item);
        }
        return toDelete.size();
    }

    //This is for extension
    //Two step: 1. get the records which are selected by logic 2. remove them
    int deleteByLoigc(String fieldName, String cmpValue, Logic logic) {
        List<Record> toDelete = selectByLogic(fieldName, cmpValue, logic);
        int res = 0;
        for (int i = 0; i < toDelete.size(); i++) {
            res += deleteByRecord(toDelete.get(i));
        }
        return res;
    }

    //give a record, return its key or row number
    private String getKeyOrRow (Record newRecord) {
        if (!hasKey) {
            return Integer.toString(records.size());
        }
        else {
            return newRecord.getData(fieldNameMap.get(keyField));
        }
    }

    private boolean isSameRecord(Record recordA, Record delRecord) {
        if(recordA.size() != delRecord.size()) {
            return false;
        }
        else {
            for(int i = 0; i < recordA.size(); i++) {
                if(!recordA.getData(i).equals(delRecord.getData(i))) {
                    return false;
                }
            }
            return true;
        }
    }

    private boolean checkRecordType(Record r) {
        for(int i = 0; i < r.size(); i++) {
            if(!typeCheck.checkType(r.getData(i), typeNames.get(i))) {
                return false;
            }
        }
        return true;
    }


    // if successfully saved, return true, else return false;
    boolean writeToFile(String dir) {
        try{
            File file;
            file = new File(dir);
            FileWriter fw = new FileWriter(file,false);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(hasKey ? keyField + "\n" : "null\n");
            bw.write(fieldNames.toString(), 1,
                    fieldNames.toString().length() -2);
            bw.write("\n");
            bw.write(typeNames.toString(), 1,
                    typeNames.toString().length() -2);
            bw.write("\n");
            for(Map.Entry<String, Record> item : records.entrySet()) {
                Record temp = item.getValue();
                String[] stringRecord = new String[temp.size()];
                for(int i = 0; i < temp.size(); i++) {
                    bw.write(temp.getData(i));
                    if(i != temp.size() - 1) bw.write(", ");
                }
                bw.write("\n");
            }
            bw.close();
            fw.close();
            return true;
        } catch (Exception e) {
            out.println("Warning: Fail to write!");
            return false;
        }
    }

    // I seperate the read file function to two part.
    // First part is used to read key and fieldName
    // Second part is used to read records.
    // I do this, because it will be easy for me to only read another
    // file's records (if they have same field name and record)
    void readWholeFile(String dir) {
        readKeyFieldType(dir);
        readData(3, dir);
    }

    void readKeyFieldType(String dir) {
        try{
            File file;
            file = new File(dir);

            Scanner readin = new Scanner(file);
            keyField = readin.nextLine();
            hasKey = !keyField.equals("null");

            String slineFiled = readin.nextLine();
            String[] newFieldName = slineFiled.split(", ");

            String slineType = readin.nextLine();
            String[] newTypeName = slineType.split(", ");

            fieldNames.clear();
            fieldNames = Arrays.asList(newFieldName);
            fieldNameMap.clear();
            for(int i = 0; i < fieldNames.size(); i++) {
                fieldNameMap.put(fieldNames.get(i), i);
            }
            colNum = fieldNames.size();

            typeNames.clear();
            typeNames = Arrays.asList(newTypeName);
            if(!typeCheck.isValidTypeList(typeNames, colNum)) {
                out.println("Warning: Wrong type! Please do it again!");
                return;
            }

            colNum = newFieldName.length;
            readin.close();
        }
        catch (Exception e) {
            out.println("Warning: Fail to read!");
        }
    }

    // if length = -1, means read all
    // the first line is 0
    void readData(int offset, String dir) {
        try{
            File file;
            file = new File(dir);
            Scanner readin = new Scanner(file);
            while(offset > 0) {
                String sline = readin.nextLine();
                offset--;
            }
            while (readin.hasNext()) {
                String sline = readin.nextLine();
                String[] newLine = sline.split(", ");
                insert(newLine);
            }
            readin.close();
        }
        catch (Exception e) {
            out.println("Warning: Fail to read!");
        }
    }

    int size() {
        return records.size();
    }

    //Remove all the thing in this table.
    void clear() {
        records.clear();
        fieldNameMap.clear();
        colNum = 0;
        hasKey = false;
        keyField = null;
        fieldNames.clear();

    }

    //For safe, if for other class, I will use this function to make a copy.
    List<Record> getACopyOfRecords(List<Record> origRecords) {
        List<Record> newRecord = new ArrayList<>();
        for(Record item : origRecords) {
            Record copy = new Record(item.toList());
            newRecord.add(copy);
        }
        return newRecord;
    }





    // print the table to an array list.
    // All the data are copy, so it is safe to return is to be not private.
    List<List<String>> printToStringArray() {
        List<List<String>> res = new ArrayList<List<String>>();
        List<String> keyList = new ArrayList<String>();

        keyList.add(keyField);
        List<String> tempFieldName = new ArrayList<String>(fieldNames);

        List<String> tempTypeName = new ArrayList<String>(typeNames);
        res.add(keyList);
        res.add(tempFieldName);
        res.add(tempTypeName);
        if (hasKey) {
            for(Map.Entry<String, Record> item : records.entrySet()) {
                Record temp = item.getValue();
                res.add(temp.toList());
            }
        }
        else {
            for(int i = 0; i < records.size(); i++) {
                Record temp = records.get(Integer.toString(i));
                res.add(temp.toList());
            }
        }
        return res;
    }




    //*********************************************************************
    //****FOR EXTENSION************************FOR EXTENSION***************
    //*********************************************************************



    // This function will return a table, it is a copy, so it is safe.
    Table selectTableByLogic(String fieldName, String cmpValue, Logic logic) {
        String[] fieldAndType = new String[fieldNames.size() * 2 + 1];
        System.arraycopy(fieldNames.toArray(new String[0]),
                0, fieldAndType,0,fieldNames.size());
        fieldAndType[fieldNames.size()] = ":";
        System.arraycopy(typeNames.toArray(new String[0]),
                0,fieldAndType, fieldNames.size() + 1, typeNames.size());
        Table newTable = new Table(keyField, hasKey, fieldAndType);
        List<Record> selected = selectByLogic(fieldName, cmpValue, logic);
        if(selected.size() == 0) return null;
        List<Record> copyList = getACopyOfRecords(selected);
        for(Record item : copyList) {
            newTable.insert(item);
        }
        return newTable;
    }

    // The return table is not the original table, it is safe to be not private.
    // first step: generate a List<List<String>> which contains all the information
    // of original tables
    // second step: choose the selected column
    // third step: generate a new table by new List
    Table selectChosenField(String ...chosenFields) {
        if(chosenFields.length == 0) {
            out.println("Warning: You didn't choose any field!");
            return null;
        }
        List<List<String>> content = printToStringArray();

        List<List<String>> newContent = new ArrayList<List<String>>();

        List<Integer> choseFieldNum = new ArrayList<>();
        String keyInfield = content.get(0).get(0);
        boolean isKeyIn = false;
        if(! chosenFields[0].equals("*")){
            for(String item : chosenFields) {
                if(keyInfield.equals(item)) {
                    isKeyIn = true;
                }
                if(fieldNameMap.get(item) == null) {
                    out.println("Warning: Your chosen part is invalid!");
                    return null;
                }

                choseFieldNum.add(fieldNameMap.get(item));
            }
        }
        else {
            for(int i = 0; i < fieldNames.size(); i++) {
                choseFieldNum.add(i);
            }
        }
        List<String> nullList = new ArrayList<>();
        nullList.add("null");
        newContent.add(isKeyIn ? content.get(0) : nullList);

        for(int i = 1; i < content.size(); i++) {
            List<String> newLine = new ArrayList<>();
            for(int j = 0; j < choseFieldNum.size(); j++) {
                newLine.add(content.get(i).get(choseFieldNum.get(j)));
            }
            newContent.add(newLine);
        }
        return new Table(newContent);

    }

    //chngField is the field that need to be changed, newValue is the new value
    //cmpFiled is compare field, cmpValue is compare value
    int updateTable(String chngFiled, String newValue, String cmpField,
                    String cmpValue, Logic logic) {
        List<Record> toUpdate = selectByLogic(cmpField, cmpValue, logic);
        int res = 0;
        if (toUpdate.size() == 0) {
            out.println("Warning: No record can be updated!");
            return 0;
        }
        for(int i = 0; i < toUpdate.size(); i++) {
            res += updateByRecord(toUpdate.get(i), chngFiled, newValue);
        }
        return res;
    }


    // This is inner join.
    // newTable is another table.
    // This table will join newTable on thisField = newField
    Table join(Table newTable, String thisField, String newField) {
        List<List<String>> joinContent = new ArrayList<List<String>>();
        List<List<String>> thisContent = printToStringArray();
        List<List<String>> newContent = newTable.printToStringArray();
        if(fieldNameMap.get(thisField) == null) {
            out.println("Warning: No such field name!");
            return null;
        }
        int thisIndex = fieldNameMap.get(thisField);
        int newIndex = -1;
        for (int i = 0; i < newContent.get(1).size(); i ++) {
            if(newContent.get(1).get(i).equals(newField)) {
                newIndex = i;
                break;
            }
        }
        if(newIndex == -1) {
            out.println("Warnning: No such field name");
            return null;
        }
        joinContent.add(thisContent.get(0));
        joinContent.add(combineTwoList(thisContent.get(1),
                newContent.get(1), newIndex));
        joinContent.add(combineTwoList(thisContent.get(2),
                newContent.get(2), newIndex));

        for (int i = 3; i < thisContent.size(); i++) {
            for (int j = 3; j < newContent.size(); j++) {
                String newTableValue = newContent.get(j).get(newIndex);
                if (thisContent.get(i).get(thisIndex).equals(newTableValue)) {
                    List<String> combineLine =
                            combineTwoList(thisContent.get(i),
                                    newContent.get(j), newIndex);
                    joinContent.add(combineLine);
                    break;
                }
            }
        }
        return new Table(joinContent);
    }

    List<String> combineTwoList(List<String> list1,
                                List<String> list2, int index) {
        List<String> res = new ArrayList<>(list1);
        List<String> temp = new ArrayList<>(list2);

        temp.remove(index);
        res.addAll(temp);
        return res;
    }

    List<String> getFieldNames() {
        List<String> res = new ArrayList<>();
        for(String item : fieldNames) {
            res.add(item);
        }
        return res;
    }




    // TEST Part
    private int testNumber = 0;

    void claim(boolean b) {
        if (!b) throw new Error("Test " + testNumber + " fails");
        testNumber++;
    }

    void testConstructor() {
        out.println( _FUNC_() + " Start!");
        claim(colNum == 3);
        out.println( _FUNC_() + " Finished!");
    }

    // testInsert will test whether the insert function works well
    // If there is key, I will test that no two same key.
    void testInsert() {
        out.println( _FUNC_() + " Start!");
        Record invaildRecord1 = new Record("11", "Thompson", "Male","Tiger");
        Record invaildRecord2 = new Record();
        Record validRecord1 = new Record("30", "Stephen", "Male");
        Record validRecord2 = new Record("35", "Durant", "Male");
        Record validRecord3 = new Record("23", "Green", "Male");
        claim(insert(invaildRecord1) == false);
        claim(insert(invaildRecord2) == false);
        claim(records.size() == 0);
        claim(insert(validRecord1) == true);
        claim(insert(validRecord2) == true);
        claim(insert(validRecord3) == true);
        claim(records.size() == 3);
        claim( insert("11", "Thompson", "Male", "Egg") == false);
        claim(records.size() == 3);
        claim(insert("11", "Thompson", "Male") == true);
        claim(records.size() == 4);

        if(hasKey == true) {
            claim(insert("11", "Thompson", "Male") == false);
            claim(insert("11", "Doggy", "Male") == false);
            Record repRecord = new Record("30", "Kitty", "Femail");
            claim(insert(repRecord) == false);
            claim(records.size() == 4);
        }
        out.println( _FUNC_() + " Finished!");
    }
    //Now the sequence is:
    //0. "30", "Stephen", "Male"
    //1. "35", "Durant", "Male"
    //2. "23", "Green", "Male"
    //3. "11", "Thompson", "Male"
    void testDelete() {
        out.println( _FUNC_() + " Start!");
        if(hasKey == false) {
            claim(deleteByKey(Integer.toString(0)) == 1);
            claim(records.size() == 3);
            claim(deleteByKey(Integer.toString(4)) == 0);
            claim(records.size() == 3);
            claim(deleteByString("30", "Stephen", "Male") == 0);
            claim(records.size() == 3);
            claim(deleteByString("11", "Thompson", "Male") == 1);
            claim(records.size() == 2);
            Record delRecord = new Record("23", "Green", "Male");
            claim(deleteByRecord(delRecord) == 1);
            claim(records.size() == 1);
            insert("35", "Durant", "Male");
            claim(records.size() == 2);
            claim(deleteByString("35", "Durant", "Male") == 2);
            claim(records.size() == 0);
            insert("30", "Stephen", "Male");
            claim(deleteByOneField("Name", "Stephen") == 1);

        }
        else {
            claim(deleteByKey("30","35","23", "11") == 4);
            claim(records.size() == 0);
        }
        insert("30", "Stephen", "Male");
        insert("35", "Durant", "Male");
        insert("23", "Green", "Male");
        insert("11", "Thompson", "Male");
        claim(records.size() == 4);

        out.println( _FUNC_() + " Finished!");

    }
    //0. "30", "Stephen", "Male"
    //1. "35", "Durant", "Male"
    //2. "23", "Green", "Male"
    //3. "11", "Thompson", "Male"
    //4. "30", "Stephen", "Male"
    //5. "30", "Stephen", "Male"
    //6. "25", "Durant", "Female"
    void testMultiDelete() {
        out.println( _FUNC_() + " Start!");
        insert("30", "Stephen", "Male");
        insert("30", "Stephen", "Male");
        insert("25", "Durant", "Female");
        claim(deleteByString("30", "Stephen", "Male") == 3);
        claim(deleteByOneField("Name", "Durant") == 2);
        claim(deleteByOneField("Gender", "Male") == 2 );
        claim(records.size() == 0);
        insert("30", "Stephen", "Male");
        insert("35", "Durant", "Male");
        insert("23", "Green", "Male");
        insert("11", "Thompson", "Male");
        out.println( _FUNC_() + " Finished!");
    }

    //Now the sequence is:
    //0. "30", "Stephen", "Male"
    //1. "35", "Durant", "Male"
    //2. "23", "Green", "Male"
    //3. "11", "Thompson", "Male"
    void testSelect() {
        out.println( _FUNC_() + " Start!");
        if(hasKey == false) {
            Record test1 = selectByKeyOrRow(Integer.toString(0));
            claim(test1.getData(fieldNameMap.get("Name")).equals("Stephen"));
            Record test2 = new Record("30", "Stephen", "Male");
            claim(selectByRecord(test2).get(0) == test1);
            List<Record> test3 = selectByString("30", "Stephen", "Male");
            claim(test3.get(0) == test1);
            insert("11", "Thompson", "Male");
            List<Record> testArray;
            testArray = selectByOneField("ID", "30");
            claim(testArray.size() == 1);
            Record test4 = testArray.get(0);
            claim(test4.getData(0).equals("30"));
            testArray = selectByOneField("ID", "11");
            claim(testArray.size() == 2);
            Record test5 = testArray.get(0);
            Record test6 = testArray.get(1);
            claim(test5.getData(0).equals("11"));
            claim(test6.getData(0).equals("11"));
            deleteByKey(Integer.toString(4));
        }
        else {
            Record test1 = selectByKeyOrRow("1");
            claim(test1 == null);
            Record test2 = selectByKeyOrRow("30");
            claim(test2.getData(1).equals("Stephen") == true);
        }

        out.println( _FUNC_() + " Finished!");

    }

    //Now the sequence is:
    //0. "30", "Stephen", "Male"
    //1. "35", "Durant", "Male"
    //2. "23", "Green", "Male"
    //3. "11", "Thompson", "Male"
    //4. "11", "Thompson", "Male"

    void testMultiSelect() {
        out.println( _FUNC_() + " Start!");
        insert("11", "Thompson", "Male");
        claim(selectByOneField("Gender", "Male").size() == 5);
        claim(selectByString("11", "Thompson", "Male").size() == 2);
        deleteByKey("4");
        out.println( _FUNC_() + " Finished!");
    }



    //Now before test start the sequence is:
    //0. "30", "Stephen", "Male"
    //1. "35", "Durant", "Male"
    //2. "23", "Green", "Male"
    //3. "11", "Thompson", "Male"
    //4. "11", "Thompson", "Male"

    void testUpdate() {
        out.println( _FUNC_() + " Start!");
        if(hasKey == false) {
            insert("11", "Thompson", "Male");
            claim(updateByKey("1", "ID", "0") == 1);
            claim(records.get("1").getData(fieldNameMap.get("ID")).equals("0"));
            claim(updateByKey("9","ID","99") == 0 );
            Record testRecord1 = new Record("0", "Durant", "Male");
            claim(updateByRecord(testRecord1, "ID", "35") == 1);
            claim(records.get("1").getData(fieldNameMap.get("ID")).equals("35"));
            Record testRecord2 = new Record("0", "Durant", "Male","banana");
            claim(updateByRecord(testRecord2, "ID", "35") == 0);
            claim(updataByString("11", "Thompson",
                    "Male",":","Gender","Female") == 2);
            claim(records.get("3").getData(fieldNameMap.get("Gender")).equals("Female"));
            claim(updataByString("11", "Thompson", "Male",
                    ",","Gender","Male") == 0);
            claim(updataByString("11", "Thompson", "Male",
                    ":","Gender","male","banana") == 0);
            claim(updateByKey("3", "Gender", "Male") == 1);
            claim(records.get("3").getData(fieldNameMap.get("Gender")).equals("Male"));
        }
        else {
            claim(updateByKey("30","Name", "Pabu") == 1);
            claim(updateByKey("30","Name", "Stephen") == 1);
            claim(updateByKey("30", "ID","23") == 0);
        }
        out.println( _FUNC_() + " Finished!");
    }



    //Now before selection the table is:
    //0. "30", "Stephen", "Male"
    //1. "35", "Durant", "Male"
    //2. "23", "Green", "Male"
    //3. "11", "Thompson", "Male"
    //4. "11", "Thompson". "Male"
    //5. "23", "Green", "Male"
    //6. "01", "Stephen", "Male"

    void testMultiUpdate() {
        out.println( _FUNC_() + " Start!");
        insert("23", "Green", "Male");
        insert("10000", "Stephen", "Male");
        List<Record> selRecord = selectByOneField("Name", "Stephen");
        claim(selRecord.size() == 2);
        claim(selRecord.get(0).getData(0).equals("30"));
        claim(selRecord.get(1).getData(0).equals("10000"));
        selRecord = selectByString("23", "Green", "Male");
        claim(selRecord.size() == 2);
        claim(isSameRecord(selRecord.get(0), selRecord.get(1)) == true);
        out.println( _FUNC_() + " Finished!");

    }

    void  testAddColumn() {
        out.println( _FUNC_() + " Start!");
        addColumn("Team", "Age",":","String","Integer");
        claim(fieldNames.get(3).equals("Team"));
        claim(fieldNames.get(4).equals("Age"));
        claim(colNum == 5);
        claim(fieldNameMap.get("Team") == 3);
        claim(fieldNameMap.get("Age") == 4);
        out.println( _FUNC_() + " Finished!\n");
    }



    // if without key, row number will be the actually key.
    // So searching by row number's time complexity is O(N)
    void  testWithOutKey() {
        out.println("****Test without key start! ****");
        testConstructor();

        testInsert();

        testDelete();
        testMultiDelete();

        testSelect();
        testMultiSelect();

        testUpdate();
        testMultiUpdate();
        testAddColumn();

        writeToFile(System.getProperty("user.dir") +
                "/DBLAB/NonExtension/DEFAULT.txt");
        printTable.print(printToStringArray());
        clear();
        readWholeFile(System.getProperty("user.dir") +
                "/DBLAB/NonExtension/DEFAULT.txt");
        printTable.print(printToStringArray());


        out.println("****Test without key finished! ****\n\n");
    }



    void testWithKey() {
        out.println("****Test with key start! ******************************");
        out.println("****Please ignore the warning, " +
                "there should have some warning ****");
        testInsert();
        testDelete();
        testUpdate();
        //autoInsert();
        insert("24", "Jordan", "Male");
        writeToFile(System.getProperty("user.dir") +
                "/DBLAB/NonExtension/DEFAULT.txt");
        readWholeFile(System.getProperty("user.dir") +
                "/DBLAB/NonExtension/DEFAULT.txt");
        out.println("****Test with key finished!****\n\n");

    }



    void run1(String[] args){
        if(args.length != 0){
            System.err.println("There should no argument");
            System.exit(1);
        }
        testWithOutKey();

    }

    void run2(String[] args) {
        if(args.length != 0){
            System.err.println("There should no argument");
            System.exit(1);
        }
        //print();
        out.println("****Read from file and initialization test start!" +
                " ****************");
        printTable.print(printToStringArray());
        out.println("****Read from file and initialization test finished! ****");
    }

    void run3(String[] args) {
        if(args.length != 0){
            System.err.println("There should no argument");
            System.exit(1);
        }
        testWithKey();
    }

    void run4(String[] args) {
        if(args.length != 0){
            System.err.println("There should no argument");
            System.exit(1);
        }
        out.println("****Test for data structure start! " +
                "*******************************");
        out.println("****Please ignore the warning, " +
                "there should have some warning ****");

        out.println("\nThis test need some time." +
                " Because there is a database contains 200000 records\n");
        out.println("Generating records by backtracking\n");
        autoInsert();
        out.println("Generate records successfully!\n");
        out.println("Select by key, start!\n" +
                "SELECT * FROM tablbe WHERE ID = 19823");
        long startTime = System.currentTimeMillis();
        Record record1 = selectByOneField("ID", "19823").get(0);
        long endTime = System.currentTimeMillis();
        long period1 = endTime - startTime;
        out.println("Select by key successfully!\n"
                + record1.toList() + " is answer!");
        out.println("Select by key, running time："+period1+"ms\n");

        out.println("Select by key, start!\nS" +
                "ELECT * FROM tablbe WHERE Name = AotGLQ");
        startTime = System.currentTimeMillis();
        Record record2 = selectByOneField("Name", "AotGLQ").get(0);
        endTime = System.currentTimeMillis();
        long period2 = endTime - startTime;
        out.println("Select by key successfully!\n"
                + record2.toList() + " is answer!");
        out.println("Select not by key, running time："+period2+"ms\n");

        long times = period2 / (period1 == 0 ? 1 : (int)period1);
        out.println("Select by key is " + times
                + " times quicker than not by key!");
        out.println("This is because of treemap！");
        out.println("****Test for data structure end! *******************");


    }

    // This is used to generate a very large table
    private List<String> myRandomName = new ArrayList<>();
    private String lab =
            "AopqrstBCDbcdefgEFGHIJKLMNOPQRSTUVWXYZahijklmnuvwxyz";

    // This is used to insert 200000 records automatically.
    void autoInsert() {
        String s = "";
        generateName(0, s);
        for(int i = 0; i < 200000; i++) {
            insert(Integer.toString(i), myRandomName.get(i), "Male");
        }
    }

    //Using backtrack to generate 200000 different names
    void generateName(int index, String s) {
        if(myRandomName.size() > 200000) return;
        if(index > 35) return;
        if(s.length() == 6){
            myRandomName.add(s);
            return;
        }
        s += lab.charAt(index);
        generateName(index + 1, s);
        s = s.substring(0, s.length() -1);
        generateName(index + 1, s);
    }




    public static void main(String[] args){
        Table program1 = new Table(null,false, "ID", "Name", "Gender",
                ":", "Integer", "String", "String");
        program1.run1(args);

        Table program2 = new Table(
                System.getProperty("user.dir") + "/DBLAB/NonExtension/initialization.txt");
        program2.run2(args);

        Table program3 = new Table("ID",true, "ID", "Name", "Gender",
                ":", "Integer", "String", "String");
        program3.run3(args);

        Table program4 = new Table("ID", true, "ID", "Name", "Gender",
                ":", "Integer", "String", "String");
        program4.run4(args);


    }

    public String _FUNC_() {
        StackTraceElement traceElement = ((new Exception()).getStackTrace())[1];
        return traceElement.getMethodName();
    }




}
