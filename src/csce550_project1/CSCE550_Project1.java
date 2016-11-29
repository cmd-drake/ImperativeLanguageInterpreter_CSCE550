/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package csce550_project1;

import java.io.FileInputStream;
import java.util.*;
import java.io.*;

/**
 *
 * @author CMD Drake
 */
public class CSCE550_Project1 {

    static final String other_tokens[] = {"{", "}", "(", ")", ";", ","};
    static final String operators[] = {"-", "*", "/", "+", "==", "!=", ":="};

    static final String reserved_words[] = {"else", "func", "global", "if", "local", "while"};

    //SymbolTable Helper Vars
    static int indexST = 0;
    static ArrayList<String> bodyList = new ArrayList<String>();

    static ArrayList<SymbolTableEntry> SymbolTable = new ArrayList<SymbolTableEntry>();

    static ArrayList<String> nonSymbolTable = new ArrayList<String>();

    public static void main(String[] args) throws IOException {
        // TODO code application logic here

        FileInputStream in = null;
        try (BufferedReader br = new BufferedReader(new FileReader("test.txt"))) {
            String line;
            int count = 1;
            while ((line = br.readLine()) != null) {

                String[] parsed = line.split("\\s+");

                String type = parseSymbolTable(parsed, line, false, br, count);

                addToSymbolTable(type, parsed, count);
                count++;

            }
        }
        System.out.println("---------------Symbol Table----------------");
        printSymbolTable();
        System.out.println("---------------Code to Process Table----------------");
        printnonSymbolTable();
        System.out.println("---------------Output----------------");
        performOperations();

    }

    public static void performOperations() {
        for (int i = 0; i < nonSymbolTable.size(); i++) {
            String line = nonSymbolTable.get(i);

            if (line.endsWith(";")) {
                String symbol = line.subSequence(0, line.length() - 1).toString();

                String value = findInSymbolTableByName(symbol);

                System.out.println("Result of: " + symbol + " = " + value);

            }
        }
    }

    public static String findInSymbolTableByName(String symbolString) {
        String name = "";

        for (int i = 0; i < SymbolTable.size(); i++) {

            if (SymbolTable.get(i).type.equals("func") && (symbolString.indexOf("(") > 0)) {
                name = symbolString.substring(0, symbolString.indexOf("("));

            }
            if (symbolString.equals(SymbolTable.get(i).name) && (SymbolTable.get(i).type.equals("global") || SymbolTable.get(i).type.equals("local"))) {
                String value = SymbolTable.get(i).value;

                return value;
            } else if (name.equals(SymbolTable.get(i).name) && (SymbolTable.get(i).type.equals("func"))) {

                String value = processFunction(symbolString, SymbolTable.get(i));
                return value;
            }

        }

        return "";
    }

    public static void updateSymbolTableEntry(String symbolString, String newVal) {
        for (int i = 0; i < SymbolTable.size(); i++) {

            if (symbolString.equals(SymbolTable.get(i).name) && (SymbolTable.get(i).type.equals("global") || SymbolTable.get(i).type.equals("local"))) {
                SymbolTable.get(i).value = newVal;

            }

        }

    }

    public static boolean doesSymbolExist(String symbolString) {
        for (int i = 0; i < SymbolTable.size(); i++) {

            if (symbolString.equals(SymbolTable.get(i).name) && (SymbolTable.get(i).type.equals("global") || SymbolTable.get(i).type.equals("local"))) {
                return true;

            }

        }
        return false;

    }

    public static String performOp(String lhs, String op, String rhs) {

        int result = 0;
        String lval = findInSymbolTableByName(lhs);
        String rval = findInSymbolTableByName(rhs);
        int lint;
        if (!lval.equals("")) {
            lint = Integer.parseInt(findInSymbolTableByName(lhs));
        } else {
            lint = Integer.parseInt(lhs);
        }
        int rint = 0;
        if (!op.equals(":=")) {

            if (!rval.equals("")) {
                rint = Integer.parseInt(findInSymbolTableByName(rhs));
            } else {
                rint = Integer.parseInt(rhs);
            }
        }
        switch (op) {
            case "+":
                result = lint + rint;
                break;
            case "-":
                result = lint - rint;
                break;
            case "*":
                result = lint * rint;
                break;
            case "/":
                result = lint / rint;
                break;
            case ":=":
                updateSymbolTableEntry(lhs, rhs);
                return rhs;
            default:
                return "";
        }

        return "" + result;
    }

    public static String processFunction(String symbolString, SymbolTableEntry entry) {

        String param = symbolString.substring(symbolString.indexOf("(") + 1, symbolString.length() - 1);

        String value = findInSymbolTableByName(param);

        SymbolTableEntry add = new SymbolTableEntry();
        add = new SymbolTableEntry("local", entry.parameters, value);

        SymbolTable.add(add);
        String lhs = "";
        String op = "";
        String rhs = "";
        for (int i = 0; i < entry.body.size() - 1; i++) {

            String line = entry.body.get(i);
            String[] parsed = line.split("\\s+");
            print(line);
            for (int j = 0; j < parsed.length; j++) {
                boolean conditionalflag = false;

                if (parsed[j].equals("if")) {
                    lhs = parsed[j + 1];
                    op = parsed[j + 2];
                    rhs = parsed[j + 3];
                    rhs = rhs.substring(0, rhs.indexOf(")"));
                    boolean lhsVerified = false;
                    if (lhs.equals(param)) {
                        lhsVerified = true;
                    }
                    String lhsV = "";
                    if (lhsVerified == false) {
                        lhsV = findInSymbolTableByName(param);
                    } else {
                        lhs = value;
                    }

                    if (isOperator(op)) {
                        switch (op) {
                            case "==":
                                if (lhsV.equals(rhs)) {
                                    conditionalflag = true;

                                    if (parsed.length == (j + 5)) {
                                        String temp = parsed[j + 4].substring(0, parsed[j + 4].indexOf(";"));
                                        //if condition with non-variables
                                        if (findInSymbolTableByName(temp).equals("")) {

                                            return parsed[j + 4].substring(0, parsed[j + 4].indexOf(";"));

                                        } else {//if condition with variables

                                            return (findInSymbolTableByName(parsed[j + 4].substring(0, parsed[j + 4].indexOf(";"))));

                                        }

                                    } else if (parsed.length == (j + 7)) { //if with a operation to calc.
                                        String result = performOp(parsed[j + 4], parsed[j + 5], parsed[j + 6].substring(0, parsed[j + 6].indexOf(";")));
                                        print("hello" + parsed[j + 4]);
                                        //updateSymbolTableEntry()
                                        return (result);
                                    }
                                } else if (!lhsV.equals(rhs)) {//not equal
                                    conditionalflag = false;
                                } else {//syntax error, could not parse
                                    print("Syntax Error");
                                }
                                break;
                            case "!=":

                                if (!lhsV.equals(rhs)) {
                                    conditionalflag = true;

                                    if (parsed.length == (j + 5)) {
                                        String temp = parsed[j + 4].substring(0, parsed[j + 4].indexOf(";"));
                                        if (findInSymbolTableByName(temp).equals("")) {
                                            return parsed[j + 4].substring(0, parsed[j + 4].indexOf(";"));

                                        } else {
                                            return (findInSymbolTableByName(parsed[j + 4].substring(0, parsed[j + 4].indexOf(";"))));

                                        }

                                    } else if (parsed.length == (j + 7)) {

                                        return (performOp(parsed[j + 4], parsed[j + 5], parsed[j + 6].substring(0, parsed[j + 6].indexOf(";"))));
                                    }
                                } else if (lhsV.equals(rhs)) {
                                    conditionalflag = false;
                                } else {
                                    print("Syntax Error");
                                }

                                break;
                            default:
                                print("Syntax Error");
                                break;
                        }
                    } else {
                        print("Syntax Error");
                    }

                    if (conditionalflag == false) {
                        //print("else");
                        String elseline = entry.body.get(i + 1);

                        String[] elseparsed = elseline.split("\\s+");
                        String elselhs = "";
                        String elseop = "";
                        String elserhs = "";
                        for (int k = 0; k < elseparsed.length; k++) {

                            if (elseparsed[k].equals("else")) {
                                elselhs = elseparsed[k + 1];
                                elseop = elseparsed[k + 2];
                                elserhs = elseparsed[k + 3];
                            }

                        }

                        //print(elselhs + "," + elseop + "," + elserhs);
                        return (performOp(elselhs, elseop, elserhs));

                    }

                    break;

                } else if (parsed[j].equals("while")) {
                    //System.out.println("while Detected");
                    lhs = parsed[j + 1];
                    op = parsed[j + 2];
                    rhs = parsed[j + 3];
                    int count = 0;
                    switch (op) {
                        case "==":
                            lhs = lhs.substring(1, lhs.length());
                            rhs = rhs.substring(0, rhs.indexOf(")"));
                            int l = i;

                            if (doesSymbolExist(lhs) == true && doesSymbolExist(rhs) == false) {
                                String left = findInSymbolTableByName(lhs);
                                while (left.equals(rhs)) {
                                    String whileLine = entry.body.get(l + 1);

                                    String[] whileArr = whileLine.split("\\s+");
                                    int aIndex = 0;
                                    for (int a = 0; a < whileArr.length; a++) {
                                        if (!whileArr[a].equals("")) {
                                            aIndex = a;
                                            a = whileArr.length + 1;
                                        }
                                    }
                                    String result = performOp(whileArr[aIndex + 2], whileArr[aIndex + 3], whileArr[aIndex + 4].substring(0, whileArr[aIndex + 4].indexOf(";")));

                                    updateSymbolTableEntry(whileArr[aIndex], result);

                                    l++;
                                    if (whileLine.substring(whileLine.length() - 1).equals("}")) {
                                        count = l;
                                        l = i;
                                    }
                                    left = findInSymbolTableByName(lhs);

                                }
                            } else if (doesSymbolExist(lhs) == true && doesSymbolExist(rhs) == true) {
                                String left = findInSymbolTableByName(lhs);
                                String right = findInSymbolTableByName(rhs);
                                while (left.equals(right)) {
                                    String whileLine = entry.body.get(l + 1);

                                    String[] whileArr = whileLine.split("\\s+");
                                    int aIndex = 0;
                                    for (int a = 0; a < whileArr.length; a++) {
                                        if (!whileArr[a].equals("")) {
                                            aIndex = a;
                                            a = whileArr.length + 1;
                                        }
                                    }
                                    String result = performOp(whileArr[aIndex + 2], whileArr[aIndex + 3], whileArr[aIndex + 4].substring(0, whileArr[aIndex + 4].indexOf(";")));

                                    updateSymbolTableEntry(whileArr[aIndex], result);

                                    l++;
                                    if (whileLine.substring(whileLine.length() - 1).equals("}")) {
                                        count = l;
                                        l = i;
                                    }
                                    left = findInSymbolTableByName(lhs);
                                    right = findInSymbolTableByName(rhs);
                                }
                            }
                            break;
                        case "!=":
                            lhs = lhs.substring(1, lhs.length());
                            rhs = rhs.substring(0, rhs.indexOf(")"));
                            l = i;

                            if (doesSymbolExist(lhs) == true && doesSymbolExist(rhs) == false) {
                                String left = findInSymbolTableByName(lhs);
                                while (!left.equals(rhs)) {
                                    String whileLine = entry.body.get(l + 1);

                                    String[] whileArr = whileLine.split("\\s+");
                                    int aIndex = 0;
                                    for (int a = 0; a < whileArr.length; a++) {
                                        if (!whileArr[a].equals("")) {
                                            aIndex = a;
                                            a = whileArr.length + 1;
                                        }
                                    }
                                    String result = performOp(whileArr[aIndex + 2], whileArr[aIndex + 3], whileArr[aIndex + 4].substring(0, whileArr[aIndex + 4].indexOf(";")));

                                    updateSymbolTableEntry(whileArr[aIndex], result);

                                    l++;
                                    if (whileLine.substring(whileLine.length() - 1).equals("}")) {
                                        count = l;

                                        l = i;
                                    }
                                    left = findInSymbolTableByName(lhs);

                                }
                            } else if (doesSymbolExist(lhs) == true && doesSymbolExist(rhs) == true) {
                                String left = findInSymbolTableByName(lhs);
                                String right = findInSymbolTableByName(rhs);
                                while (!left.equals(right)) {
                                    String whileLine = entry.body.get(l + 1);

                                    String[] whileArr = whileLine.split("\\s+");
                                    int aIndex = 0;
                                    for (int a = 0; a < whileArr.length; a++) {
                                        if (!whileArr[a].equals("")) {
                                            aIndex = a;
                                            a = whileArr.length + 1;
                                        }
                                    }
                                    String result = performOp(whileArr[aIndex + 2], whileArr[aIndex + 3], whileArr[aIndex + 4].substring(0, whileArr[aIndex + 4].indexOf(";")));

                                    updateSymbolTableEntry(whileArr[aIndex], result);

                                    l++;
                                    if (whileLine.substring(whileLine.length() - 1).equals("}")) {
                                        count = l;

                                        l = i;
                                    }
                                    left = findInSymbolTableByName(lhs);
                                    right = findInSymbolTableByName(rhs);
                                }

                            }

                            break;
                    }

                    //i = count;
                    
                    
                }
                //print("plz:" + line);
            }

        }

        SymbolTable.remove(add);
        return "Value not computed";
    }

    public static void printSymbolTable() {
        for (int i = 0; i < SymbolTable.size(); i++) {

            SymbolTableEntry x = SymbolTable.get(i);
            System.out.println(x);

        }
    }

    public static void printnonSymbolTable() {
        for (int i = 0; i < nonSymbolTable.size(); i++) {

            System.out.println(nonSymbolTable.get(i));

        }
    }

    public static String parseSymbolTableBody(String[] parsed, boolean parseLocation) {
        String type = "";
        for (int i = 0; i < parsed.length - 1; i++) {

            if (parsed[i].equals("global")) {
                //System.out.println("Global Detected");
                type = "global";
                indexST = i;
                i = parsed.length + 1;
                break;

            }
            if (parsed[i].equals("local")) {
                //System.out.println("Local Detected");
                type = "local";
                indexST = i;
                i = parsed.length + 1;
                break;
            }

        }

        return type;
    }

    public static String parseSymbolTable(String[] parsed, String line, boolean parseLocation, BufferedReader br, int count) throws IOException {
        String type = "";
        for (int i = 0; i < parsed.length; i++) {

            if (parsed[i].equals("global")) {
                //System.out.println("Global Detected");
                type = "global";
                indexST = i;
                i = parsed.length + 1;
                break;

            }
            if (parsed[i].equals("local")) {
                //System.out.println("Local Detected");
                type = "local";
                indexST = i;
                i = parsed.length + 1;
                break;
            }
            if (parseLocation == false) {
                if (parsed[i].equals("func")) {
                    //System.out.println("Function Detected");
                    type = "func";
                    indexST = i;
                    bodyList = getFunctionBody(br, count);
                    i = parsed.length + 1;
                    break;
                }
            }
            if (type == "") {
                if (!line.equals("")) {
                    //System.out.println(line);
                    nonSymbolTable.add(line);
                }

            }

        }

        return type;
    }

    public static ArrayList<String> getFunctionBody(BufferedReader br, int count) throws IOException {
        ArrayList<String> bodyList = new ArrayList<String>();

        boolean flag = false;

        String line;
        int start = 1;
        int end = 0;

        while (flag == false) {
            line = br.readLine();

            if (line.charAt(line.length() - 1) == '{') {
                start++;
            }
            if (line.charAt(line.length() - 1) == '}') {
                end++;
            }

            if (start == end) {
                flag = true;
            }
            bodyList.add(line);
            count++;
        }
        return bodyList;

    }

    public static void addToSymbolTable(String type, String[] parsed, int number) {
        boolean global = false;
        boolean local = false;
        boolean func = false;

        if (type == "global") {
            global = true;
        }
        if (type == "local") {
            local = true;
        }
        if (type == "func") {
            func = true;

        }

        SymbolTableEntry entry = new SymbolTableEntry();
        if (global == true) {

            if (parsed.length >= (indexST + 3) && parsed[indexST + 2].equals(":=")) {
                String value = parsed[indexST + 3].subSequence(0, parsed[indexST + 3].length() - 1).toString();
                entry = new SymbolTableEntry("global", parsed[indexST + 1], value);
                SymbolTable.add(entry);

            } else if (parsed.length >= (indexST + 3) && !parsed[indexST + 2].equals(":=")) {

                System.out.println("Syntax Error at line " + number + "\n");
                global = false;
            } else if (parsed[indexST + 1].contains(";")) {
                String symbol = parsed[indexST + 1].subSequence(0, parsed[indexST + 1].length() - 1).toString();
                entry = new SymbolTableEntry("global", symbol, "0");
                SymbolTable.add(entry);
            }
            if (global == true) {
                // System.out.println(entry.toString());
            }

        } else if (local == true) {
            if (parsed.length >= (indexST + 3) && parsed[indexST + 2].equals(":=")) {

                entry = new SymbolTableEntry("local", parsed[indexST + 1], parsed[indexST + 3].substring(0, parsed[indexST + 3].indexOf(";")));
                SymbolTable.add(entry);
            } else if (parsed.length >= (indexST + 3) && !parsed[indexST + 2].equals(":=")) {

                System.out.println("Syntax Error at line " + number + "\n");
                local = false;
            } else if (parsed[indexST + 1].contains(";")) {
                String symbol = parsed[indexST + 1].subSequence(0, parsed[indexST + 1].length() - 1).toString();
                entry = new SymbolTableEntry("local", symbol, "0");
                SymbolTable.add(entry);
            }
            if (local == true) {
                // System.out.println(entry.toString());
            }

        } else if (func == true) {

            String name = parsed[indexST + 1].substring(0, parsed[indexST + 1].indexOf("("));
            String parameters = parsed[indexST + 1].substring((parsed[indexST + 1].indexOf("(") + 1), parsed[indexST + 1].indexOf(")"));

            entry = new SymbolTableEntry("func", name, parameters, bodyList);
            SymbolTable.add(entry);

            // System.out.println(entry.toString());
            String[] bodyArr = new String[bodyList.size()];

            for (int i = 0; i < bodyList.size(); i++) {
                bodyArr = bodyList.get(i).split("\\s+");
                type = parseSymbolTableBody(bodyArr, true);
                addToSymbolTable(type, bodyArr, number);

            }

        }

    }

    public static boolean isReservedWord(String keyword) {
        List<String> temp = Arrays.asList(reserved_words);

        boolean contains = temp.contains(keyword);

        return (contains);
    }

    public static boolean isOperator(String keyword) {
        List<String> temp = Arrays.asList(operators);

        boolean contains = temp.contains(keyword);

        return (contains);
    }

    public static boolean isOtherToken(String keyword) {
        List<String> temp = Arrays.asList(other_tokens);

        boolean contains = temp.contains(keyword);

        return (contains);
    }

    public static void print(String str) {
        System.out.println(str);
    }

}
