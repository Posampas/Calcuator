package calculator;


import java.util.ArrayDeque;
import java.util.Arrays;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ParserV2 {
    String commandString;  // initial command send form state machine
    String[] splitedEquation = new String[2];

    private boolean isEquation;
    private boolean isAssigment;
    private boolean isVariables;
    private boolean isVariableKnown;
    private String  variableName;
    private boolean isCommand;

    private boolean isError;
    private String errorMs;
    private ArrayDeque <String> equationStack;


    private String[] parshedEquation;

    ParserV2(String command) {
        isEquation = false;
        isVariables = false;
        isVariableKnown = false;
        isCommand = false;
        isError = false;
        this.commandString = command;
        typeCheck();   // Check weather the command is equation or command
    }

    //    Main function for actual parching of the equation
    void parseEquation() {
//      1. Clean the equation from unnecessary signs and spaces
        commandString = replaceAllUnnecessary(commandString);


//      2. Check the syntax of the equation return true if syntax is bad
        if (checkForBadSyntax(commandString)) {
            isError = true;
            errorMs = "Invalid expression";
            return;
        }

//      3. Atomize the equation

        splitedEquation = atomize(commandString);

//        System.out.println(Arrays.toString(splitedEquation));

        //4. Check equation for variables

        String[] variablesArry = isVariablesInEquation(splitedEquation);

        isVariables = isVar(variablesArry);

//      5. Check weather the it is assigment or equation and checking the naming of the variables

        isAssigment = isAssigment(commandString);

//        System.out.println("isVariables " + isVariables);
        if (!isVariables) {
            if (isAssigment) {
                isError = true;
                errorMs = "Incorrect assigment";
                return;
            }
        }

        Pattern number = Pattern.compile("[0-9]");
        Pattern variable = Pattern.compile("[A-Za-z]");

        if (isVariables) {
            if (isAssigment) {
                if (splitedEquation[1].charAt(0) != '=') {
                    isError = true;
                    errorMs = "Incorrect assigment";
                    return;
                }
                if (splitedEquation[1].charAt(0) == '=') {
                    if (variablesArry[0].charAt(0) == '-') {
                        isError = true;
                        errorMs = "Incorrect assigment";
                        return;
                    } else {
                        if (!checkVariableName(variablesArry[0], number, variable)) {
                            isError = true;
                            errorMs = "Invalid identifier";
                            return;
                        } else {
                            for (int i = 2; i < variablesArry.length; i++) {
                                if (variablesArry[i].charAt(0) != '-') {
                                    if (checkVariableName(variablesArry[i], number, variable)) {
                                        if (!checkIsVariableKnown(variablesArry[i])) {
                                            isError = true;
                                            errorMs = "Unknown variable";
                                            return;
                                        } else {
                                            splitedEquation[i] = String.valueOf(StateMachine.getValue(variablesArry[i]));
                                        }
                                    } else {
                                        isError = true;
                                        errorMs = "Incorrect assigment";
                                        return;
                                    }
                                }
                            }
                        }
                    }

                }
            } else {
                for (int i = 0; i < variablesArry.length; i++) {
                    if (variablesArry[i].charAt(0) != '-') {
                        if (checkVariableName(variablesArry[i], number, variable)) {
                            if (!checkIsVariableKnown(variablesArry[i])) {
                                isError = true;
                                errorMs = "Unknown variable";
                                return;
                            } else {                     // if varaible known replace it with its value
                                splitedEquation[i] = String.valueOf(StateMachine.getValue(variablesArry[i]));
                            }
                        } else {
                            isError = true;
                            errorMs = "Incorrect identifier";
                            return;
                        }
                    }
                }

            }
        }


//       6. Translate the equation to polish notation

        if(isAssigment){
            variableName = variablesArry[0];
            equationStack = translateToRPN(Arrays.copyOfRange(splitedEquation,2,splitedEquation.length));
        }else {
            equationStack = translateToRPN(splitedEquation);
        }


//      Parsing Done


    }

    private ArrayDeque<String> translateToRPN(String[] splitedEquation) {

        ArrayDeque<String> result = new ArrayDeque<>();
        ArrayDeque<String> signStack = new ArrayDeque<>();
        ArrayDeque<String> equationStack = new ArrayDeque<>(Arrays.asList(splitedEquation));

        while (!equationStack.isEmpty()) {
            if (equationStack.getFirst().matches("-?[0-9]+")) {
                result.offerLast(equationStack.pop());
            } else if (equationStack.getFirst().matches("[-+*/]")) {
                if (signStack.isEmpty() || signStack.getFirst().equals("(")) {
                    signStack.push(equationStack.pop());
                } else {
                    if (getSignPriority(equationStack.getFirst())
                            < getSignPriority(signStack.getFirst())) {
                        signStack.push(equationStack.pop());
                    } else {
                        result.offerLast(signStack.pop());
                    }
                }
            } else if (equationStack.getFirst().equals("(")) {
                signStack.push(equationStack.pop());
            } else if (equationStack.getFirst().equals(")")) {
                equationStack.pop();
                while (!signStack.getFirst().equals("(")) {
                    result.offerLast(signStack.pop());
                }
                signStack.pop();
            }
        }
        while (!signStack.isEmpty()) {
            result.offerLast(signStack.pop());
        }


        return result;
    }

    private int getSignPriority(String singn) {
        if (singn.equals("+")) {
            return 2;
        } else if (singn.equals("-")) {
            return 2;
        } else if (singn.equals("/")) {
            return 1;
        } else {
            return 1;
        }
    }

    private boolean isVar(String[] varaiblesArray) {
        for (int i = 0; i < varaiblesArray.length; i++) {
            if (varaiblesArray[i].charAt(0) != '-') {
                return true;
            }
        }
        return false;
    }


    private boolean checkVariableName(String variableName, Pattern number, Pattern variables) {

        Matcher num = number.matcher(variableName);
        Matcher var = variables.matcher(variableName);

        if (num.find() && var.find()) {
            return false;
        } else if (var.find())
            return true;
        else
            return false;
    }

    private boolean isAssigmentCorrect(String[] splitedEquation) {
        if (!splitedEquation[1].equals("=")) {
            return false;
        }

        return true;
    }

    private String[] atomize(String commandString) {

        String[] splitedArray = new String[2];
        String pat = "([0-9A-Za-z]+)|(-)|(\\+)|([0-9]+)|(=)|(\\*)|(/)|([()])";
        Matcher split = Pattern.compile(pat).matcher(commandString);

        int count = 0;


        while (split.find()) {

            if (count >= splitedArray.length) {
                splitedArray = extendArr(splitedArray, splitedArray.length);
            }
            if (split.group(1) != null) {
                splitedArray[count++] = split.group(1);
            } else if (split.group(2) != null) {
                splitedArray[count++] = split.group(2);
            } else if (split.group(3) != null) {
                splitedArray[count++] = split.group(3);
            } else if (split.group(4) != null) {
                splitedArray[count++] = split.group(4);
            } else if (split.group(5) != null) {
                splitedArray[count++] = split.group(5);
            } else if (split.group(6) != null) {
                splitedArray[count++] = split.group(6);
            } else if (split.group(7) != null) {
                splitedArray[count++] = split.group(7);
            } else if (split.group(8) != null) {
                splitedArray[count++] = split.group(8);
            }
        }


        // remove the tailing nulls form array

        return removeTailingNulls(count, splitedArray);

    }

    private String[] removeTailingNulls(int count, String[] arr) {

        return Arrays.copyOf(arr, count);
    }

    private String[] extendArr(String[] arr, int len) {
        return Arrays.copyOf(arr, len * 2);

    }


    private boolean isAssigment(String commandString) {

        if (commandString.contains("=")) {
            return true;
        }
        return false;
    }

    private boolean checkForBadSyntax(String commandString) {
        String pattern = "(\\*\\*+)|(//+)|(==)|([0-9]+ [0-9]+)|(=.+?=)|(\\) ?\\()";

        Matcher syntaxCheck = Pattern.compile(pattern).matcher(commandString);

        if (syntaxCheck.find()) {
            return true;
        }

        if (commandString.length() == 1 ){
            return !commandString.matches("[0-9]|[A-Za-z]");
        }

        // checking the brackets

        int countLeft = 0;
        int countRight = 0;
        for (int i = 0; i < commandString.length(); i++) {
            if (commandString.charAt(i) == '(') {
                countLeft++;
            } else if (commandString.charAt(i) == ')') {
                countRight++;
            }
        }

        if (countRight != countLeft) {
            return true;
        }

        return false;
    }

    private String replaceAllUnnecessary(String equation) {
        boolean twoSigninrow = true;

        while (twoSigninrow) {

            equation = equation.replaceAll("\\+-", "-")
                    .replaceAll("-\\+", "-")
                    .replaceAll("\\++", "+")
                    .replaceAll("--", "+")
                    .replaceAll(" ", " ")
                    .replaceAll("=\\+ ", "=")
                    .replaceAll("(\\(\\))|(\\( +\\))", "(0)");
            twoSigninrow = false;
            for (int i = 0; i < equation.length() - 1; i++) {
                if (((equation.charAt(i) == '+') && (equation.charAt(i + 1) == '-'))) {
                    twoSigninrow = true;
                    break;
                }
                if (((equation.charAt(i) == '-') && (equation.charAt(i + 1) == '+'))) {
                    twoSigninrow = true;
                    break;
                }
                if (((equation.charAt(i) == '-') && (equation.charAt(i + 1) == '-'))) {
                    twoSigninrow = true;
                    break;
                }
            }
        }
        return equation;
    }

    // Check weather the command is equation or command
    private void typeCheck() {
        if (commandString.charAt(0) == '/') {     // commands always stars with "/" and can contain only latin letters
            isCommand = true;

        } else {
//             Check for not allowed signs in equation
            Matcher equationErrorMatcher = Pattern.compile("[^0-9a-zA-Z\\-+= /*()]").matcher(commandString);
            if (equationErrorMatcher.find()) {
                isError = true;
                errorMs = "Invalid expression";
            } else {
                isEquation = true;

            }
        }
    }

    //    Search the equation for variables
    private String[] isVariablesInEquation(String[] splitedEquation) {


        Pattern pattern = Pattern.compile("[A-Za-z]");

        String[] arr = new String[splitedEquation.length];

        for (int i = 0; i < splitedEquation.length; i++) {
            Matcher varibale = pattern.matcher(splitedEquation[i]);
            if (varibale.find()) {
                arr[i] = splitedEquation[i];
            } else {
                arr[i] = "-";
            }
        }
        return arr;
    }

    //    Check are the variables in equation in map
    private boolean checkIsVariableKnown(String variable) {
        return StateMachine.isVariable(variable);
    }


    public String getCommandString() {
        return commandString;
    }

    public Boolean getCommand() {
        return isCommand;
    }

    public Boolean getError() {
        return isError;
    }

    public String getErrorMs() {
        return errorMs;
    }

    public String[] getParshedEquation() {
        return parshedEquation;
    }

    public Boolean getEquation() {
        return isEquation;
    }

    public Boolean getVariables() {
        return isVariables;
    }

    public Boolean getVariableKnown() {
        return isVariableKnown;
    }

    public boolean isAssigment() {
        return isAssigment;
    }

    public String getVariableName() {
        return variableName;
    }

    public ArrayDeque getEquationStack() {
        return equationStack;
    }
}
