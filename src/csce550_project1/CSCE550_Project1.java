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

    //static final String reserved_words[] = {"global", "local", "func", "if", "else", "while"};
    static final String other_tokens[] = {"{", "}", "(", ")", ";", ",", "IDENT", "SCALAR"};
    static final String operators[] = {"-", "*", "/", "+", "==", "!=", ":="};

    static final String reserved_words[] = {"else", "func", "global", "if", "local", "while"};

    public static LinkedList<LinkedList<SymbolTableEntry>> SymbolTable = new LinkedList<LinkedList<SymbolTableEntry>>();

    public static void main(String[] args) throws IOException {
        // TODO code application logic here

        FileInputStream in = null;
        try (BufferedReader br = new BufferedReader(new FileReader("test.txt"))) {
            String line;
            int count = 1;
            while ((line = br.readLine()) != null) {
                //System.out.println(line);
                addToSymbolTable(line,count);
                count++;

            }
        }

    }

    public static void addToSymbolTable(String line, int number) {

        String[] parsed = line.split("\\s+");

        boolean global = false;
        boolean local = false;
        boolean func = false;
        int indexST = 0;
        for (int i = 0; i < parsed.length; i++) {

            if (parsed[i].equals("global")) {
                //System.out.println("Global Detected");
                global = true;
                indexST = i;
                i = parsed.length + 1;
                break;

            }
            if (parsed[i].equals("local")) {
                //System.out.println("Local Detected");
                local = true;
                indexST = i;
                i = parsed.length + 1;
                break;
            }
            if (parsed[i].equals("func")) {
                //System.out.println("Function Detected");
                func = true;
                indexST = i;
                i = parsed.length + 1;
                break;
            }

        }
        SymbolTableEntry entry = new SymbolTableEntry();
        if (global == true) {

            if (parsed.length >= (indexST + 3) && parsed[indexST+2].equals(":=")) {

                entry = new SymbolTableEntry("global", parsed[indexST + 1], parsed[indexST + 3]);

            } else if (parsed.length >= (indexST + 3) && !parsed[indexST+2].equals(":=")) {

                    System.out.println("Syntax Error at line " + number + "\n");
                    global = false;
            }
            else {
                if(parsed[indexST + 1].contains(";")){
                    entry = new SymbolTableEntry("global", parsed[indexST + 1], "0");
                }
            }
            if(global == true){
            System.out.println(entry.toString());
            }
        } else if (local == true) {
             if (parsed.length >= (indexST + 3) && parsed[indexST+2].equals(":=")) {

                entry = new SymbolTableEntry("local", parsed[indexST + 1], parsed[indexST + 3]);

            } else if (parsed.length >= (indexST + 3) && !parsed[indexST+2].equals(":=")) {

                    System.out.println("Syntax Error at line " + number + "\n");
                    local = false;
            }
            else {
                if(parsed[indexST + 1].contains(";")){
                    entry = new SymbolTableEntry("local", parsed[indexST + 1], "0");
                }
            }
            if(local == true){
            System.out.println(entry.toString());
            }
        } else if (func == true) {

            String name = parsed[indexST + 1].substring(0, parsed[indexST + 1].indexOf("("));
            String parameters = parsed[indexST + 1].substring((parsed[indexST + 1].indexOf("(") + 1), parsed[indexST + 1].indexOf(")"));

            entry = new SymbolTableEntry("func", name, parameters, "");
            System.out.println(entry.toString());

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

}
