package calculator;

import java.math.BigInteger;
import java.util.ArrayDeque;
import java.util.HashMap;

class StateMachine {
    private boolean isWorking;
    private boolean input;
    private boolean output;
    private boolean calculation;
    private boolean options;
    private boolean error;
    private boolean parsingState;
    private boolean assigment;
    private ArrayDeque<String> parsedEquation;
    private String variableName;
    private StringBuilder str;
    static private HashMap<String, BigInteger> variables;

    StateMachine() {
        this.isWorking = true;
        variables = new HashMap<>();
        str = new StringBuilder();
        stateZero();

    }
    private void stateZero(){
        this.input = true;
        this.output = false;
        this.calculation = false;
        this.options = false;
        this.error = false;
        this.parsingState = false;
        this.assigment = false;
        this.variableName = null;
        str.delete(0,str.length());

    }

    void workLoop() {

        while (isWorking) {
            stateZero();


            if (input) {
                Input in = new Input();

                str.append(in.getInput());
                if ( !str.toString().equals("")) {
                    input = false;
                    parsingState = true;
                }
            }

            if (parsingState) {
                ParserV2 parser = new ParserV2(str.toString());
                str.delete(0,str.length());

                if (parser.getEquation()) {
                    parser.parseEquation();

                    if (parser.isAssigment()) {
                        assigment = true;
                        calculation = true;
                        parsedEquation = parser.getEquationStack();
                        variableName = parser.getVariableName();

                    } else if (!parser.isAssigment()) {
                        calculation = true;
                        parsedEquation = parser.getEquationStack();
                    }
                }

                if (parser.getCommand()) {
                    options = true;
                    str.append(parser.getCommandString());

                }
                if (parser.getError()) {
                    error = true;
                    calculation = false;
                    str.append(parser.getErrorMs());
                    output = true;
                }

                parsingState = false;
            }

            if (output) {

                if(error){
                    Output.printOptions(str.toString());
                }
                error = false;
                output= false;
            }

            if (options) {
                Options opt = new Options(str.toString());
                if (opt.isTerminate()) {
                    Output.printOptions(opt.getOption());
                    stateZero();
                    isWorking = false;
                }
                if (!opt.isTerminate()) {
                    Output.printOptions(opt.getOption());

                }
                options = false;

            }
            if (calculation) {
                Calculator calc = new Calculator(parsedEquation);
                calc.calculate();
                if (assigment) {
                    addVariables(variableName, calc.getResult());
                    assigment = false;

                } else {
                    Output.printOptions(calc.getResult().toString());

                }
                calculation = false;
            }
        }

    }

    private static void addVariables(String key, BigInteger value) {

        variables.put(key, value);
    }

    static boolean isVariable(String key) {
        return variables.containsKey(key);
    }

    static BigInteger getValue(String key) {
        return variables.get(key);
    }

    /*public static void print() {
        for (var elem : variables.entrySet()) {
            System.out.println(elem.getKey() + ":" + elem.getValue());

        }
    }*/
}
