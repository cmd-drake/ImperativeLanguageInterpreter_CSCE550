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
        System.out.println("---------------Symbol Table----------------\n");
        printSymbolTable();
        System.out.println("---------------Code to Process Table----------------\n");
        System.out.println(nonSymbolTable);
        System.out.println("---------------Output----------------\n");
        performOperations();

    }

    public static void performOperations() {
        for (int i = 0; i < nonSymbolTable.size(); i++) {
            String line = nonSymbolTable.get(i);

            if (line.endsWith(";")) {
                String symbol = line.subSequence(0, line.length() - 1).toString();

                String value = findInSymbolTableByName(symbol);
                if (!value.equals("")) {
                    System.out.println(value);
                }
            }
        }
    }

    public static String findInSymbolTableByName(String symbolString) {
        String name = "";
        for (int i = 0; i < SymbolTable.size(); i++) {

            if (SymbolTable.get(i).type.equals("func")) {
                name = symbolString.substring(0, symbolString.indexOf("("));

            }
            if (symbolString.equals(SymbolTable.get(i).name) && (SymbolTable.get(i).type.equals("global") || SymbolTable.get(i).type.equals("local"))) {
                String value = SymbolTable.get(i).value;
                return value;
            } else if (name.equals(SymbolTable.get(i).name) && (SymbolTable.get(i).type.equals("func"))) {

                processFunction(SymbolTable.get(i));

            }

        }

        return "";
    }

    public static void processFunction(SymbolTableEntry entry) {

        
        
    }

    public static void printSymbolTable() {
        for (int i = 0; i < SymbolTable.size(); i++) {

            SymbolTableEntry x = SymbolTable.get(i);
            System.out.println(x);

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

                entry = new SymbolTableEntry("local", parsed[indexST + 1], parsed[indexST + 3]);
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
