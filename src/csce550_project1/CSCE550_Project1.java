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

    public static void main(String[] args) throws IOException {
        // TODO code application logic here
        Stack st = new Stack();

        FileInputStream in = null;
        try (BufferedReader br = new BufferedReader(new FileReader("test.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                //System.out.println(line);

            }
        }

    }

}
