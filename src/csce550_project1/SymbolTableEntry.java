/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package csce550_project1;

import java.util.ArrayList;
import java.util.Stack;

/**
 *
 * @author CMD Drake
 */
public class SymbolTableEntry {

    //Main Info
    String type;
    String name;
    //END MAIN INFO

    //VARIABLES 
    String value;
    //END VARIABLES 

    //FUNCTIONS 
    String parameters;
    ArrayList<String> body;
   
    //END FUNCTIONS
    
    Stack<SymbolTableEntry> st = new Stack<SymbolTableEntry>();
    
    SymbolTableEntry() {
        type = "";
        name = "";
        value = "";
        parameters = "";
        body = new ArrayList<String>();

    }

    SymbolTableEntry(String type, String name, String value) {

        this.type = type;
        this.name = name;
        this.value = value;

    }

    SymbolTableEntry(String type, String name, String parameters, ArrayList<String> body) {

        this.type = type;
        this.name = name;
        this.parameters = parameters;
        this.body = body;

    }

    public String toString() {

        return printSymbolTable();
    }

    private String printSymbolTable() {
        String entry = "";
        entry = entry + "Type: " + type + "\n";
        entry = entry + "Name: " + name + "\n";

        if (type == "global" || type == "local") {
            entry = entry + "Value: " + value + "\n";
        } else if (type == "func") {
            entry = entry + "Parameters: " + parameters + "\n";
            entry = entry + "Body: " + body + "\n";

        }

        return entry;

    }
    
    public void updateEntry(){
        st.push(this);
        
    }

}
