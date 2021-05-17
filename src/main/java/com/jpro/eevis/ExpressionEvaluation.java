package com.jpro.eevis;

import java.util.*;

public class ExpressionEvaluation {

  public String equation;
  
  public class AssemblyLine {
    public int astId;
    public String instruction;
    public int rule;
    public String destinationType;
    public String sourceTypes;
  }
  
  Map<Integer, AssemblyLine> assemblyLines;

  // display initial types and values for variables that are
  // explicitly defined in the equation (don't include temps here)
  public class VariableRecord {
    public String name;
    public String type;
    public String value;
  }
  
  List<VariableRecord> variableTable;
  
  // bottom table
  public class EvaluationRow {
    public String operation;
    public String lhsType;
    public String rhsTypes;
    public String conversionRule;
    public String values;
    public String value;
    public String name;
  }
  
  List<EvaluationRow> evaluation;
  
  class ASTNode {
    String contents;
    List<Integer> assemblyIds = new ArrayList<>();
    List<Integer> variableAccesses = new ArrayList<>();
  }
  
  Map<Integer, ASTNode> asTree;
  
  class VariableAccess {
    public int astId;
    public int assemblyId;
    public String type;
    public String name;
    public String value;
  }




  public void parseEvaluation(List<String> input) {
  
    assemblyLines = new TreeMap<>();
    variableTable = new ArrayList<>();
    evaluation = new ArrayList<>();
    asTree =new TreeMap<>();
    
    List<Integer> traversalOrder = new ArrayList<>();
    List<VariableAccess> variableAccesses = new ArrayList<>();

    for (int n = 0; n < input.size(); ++n) {
      String line = input.get(n);
      String[] typeAndParameters = line.split("~!~");
      String type = typeAndParameters[0];
      String[] parameters;
                  
      if (typeAndParameters.length > 1) parameters = typeAndParameters[1].split("\\|");
      else {
        parameters = new String[1];
        parameters[0] = "";
      }
      
      if (type.equals("var_decl_node")) {
        VariableRecord record = new VariableRecord();
        record.name = parameters[0];
        record.type = parameters[1];
        record.value = parameters[2];
        variableTable.add(record);
      }
      else if (type.equals("traversal")) {
        String[] indices = parameters[0].replace("[", "").replace("]", "").split(",");
        for (int i = 0; i < indices.length; ++i) traversalOrder.add(Integer.parseInt(indices[i]));
      }
      else if (type.equals("ast_node")) {
        ASTNode node = new ASTNode();
        node.contents = parameters[5];
        asTree.put(Integer.parseInt(parameters[0]), node);
      }
      else if (type.equals("assembly")) {
        AssemblyLine aline = new AssemblyLine();
        aline.astId = Integer.parseInt(parameters[1]);
        aline.instruction = parameters[2];
        
        aline.rule = Integer.parseInt(parameters[3]) + 1;
        aline.destinationType = parameters[4];
        aline.sourceTypes = parameters[5].replace("[", "").replace("]", "");
        assemblyLines.put(Integer.parseInt(parameters[0]), aline);
      }
      else if (type.equals("variable_access")) {
        VariableAccess va = new VariableAccess();
        va.astId = Integer.parseInt(parameters[1]);
        va.assemblyId = Integer.parseInt(parameters[2]);
        va.type = parameters[3];
        va.name = parameters[4];
        va.value = parameters[5];
        variableAccesses.add(va);
      }
      else if (type.equals("equation")) equation = parameters[0];
      else if (type.equals("error")) equation = parameters[1];
      
    } // end loop through records
    
    // For ease of future reference, populate each AST node with the corresponding
    // assembly line ID and variable access ID
    for (Integer i : traversalOrder) {
      ASTNode node = asTree.get(i);
      // figure out associated assembly line
      Set<Integer> akeys = assemblyLines.keySet();
      for (Integer ai : akeys) {
        AssemblyLine aline = assemblyLines.get(ai);
        if (aline.astId == i) node.assemblyIds.add(ai);
      }
      // figure out associated variable access
      for (int vai = 0; vai < variableAccesses.size(); ++vai) {
        VariableAccess va = variableAccesses.get(vai);
        if (va.astId == i) node.variableAccesses.add(vai);
      }
    }

    // Now, go through the AST in traversal order and build the evaluation table row by row
    Map<String, String> variableValues = new TreeMap<>();
    for (VariableRecord record : variableTable) variableValues.put(record.name, record.value);
    
    for (Integer idx : traversalOrder) {
      ASTNode node = asTree.get(idx);
      
      // update variable values from any associated variable accesses
      for (Integer accessIdx : node.variableAccesses) {
        VariableAccess access = variableAccesses.get(accessIdx);
        variableValues.put(access.name, access.value);
      }
      
      // use associated assembly lines to build much of the row
      String[] vars;
      for (Integer assemblyIdx : node.assemblyIds) {
        EvaluationRow row = new EvaluationRow();
        AssemblyLine aline = assemblyLines.get(assemblyIdx);
        row.operation = aline.instruction;
        row.lhsType = aline.destinationType;
        row.rhsTypes = aline.sourceTypes;
        row.conversionRule = (aline.rule >= 1) ? ("" + aline.rule) : "None";
        
        // parse the involved variables from the assembly instruction
        vars = aline.instruction.split("\\s+");
        if (vars.length == 0) continue;
        
        // if the leftmost string is a variable, it gets the destination value
        if (variableValues.containsKey(vars[0])) row.value = variableValues.get(vars[0]);
        
        // now populate the source values
        String sourceValues = "";
        for (int n = 1; n < vars.length; ++n) {
          if (variableValues.containsKey(vars[n])) 
            sourceValues += ("[" + variableValues.get(vars[n]) + "]  ");
        }
        
        row.values = sourceValues;
        evaluation.add(row);
        
      }
    }
    
//    print();

  } // parseEvaluation()
  
  
  
  boolean isDeclaredVariable(String variable) {
    for (VariableRecord record : variableTable) {
      if (record.name.equals(variable)) return true;
    }
    return false;
  }
  
  
  
  // for testing purposes
  void print() {
    System.out.println(equation);
    System.out.println();
    for (VariableRecord record : variableTable) {
      System.out.println(record.name + ": " + record.type + " " + record.value);
    }
    System.out.println();
    for (EvaluationRow row : evaluation) {
      System.out.println(row.operation + "   " + row.lhsType + "   " + row.rhsTypes +
                         "   " + row.conversionRule + "   " + row.value + "   " + row.values);
    }
  }

}
