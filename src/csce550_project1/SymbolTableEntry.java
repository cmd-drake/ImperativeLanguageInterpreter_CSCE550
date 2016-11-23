/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package csce550_project1;

import java.util.ArrayList;

/**
 *
 * @author CMD Drake
 */
public class SymbolTableEntry {

    int symbolTableType;
    
    //Main Info
    String type;
    String name;   
    //END MAIN INFO
    
    //VARIABLES 
    String value; 
    //END VARIABLES 
    
    //FUNCTIONS 
    ArrayList parameters; 
    AST body; 
    
    //END FUNCTIONS
    
    
}
