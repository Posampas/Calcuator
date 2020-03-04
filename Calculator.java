package calculator;

import java.math.BigInteger;
import java.util.ArrayDeque;

public class Calculator {
    private ArrayDeque<String> equation;
    private ArrayDeque<String> resultStack;
    private BigInteger result;

    Calculator(ArrayDeque equation) {
        this.equation = equation;
        resultStack = new ArrayDeque();
        result = BigInteger.ONE;
    }

    void calculate() {
        String[] compute = new String[3];
        boolean freshNumbesInArray = false;

        while (!equation.isEmpty()) {
            if (equation.size() >= 3) {
                for (int i = 0; i < 3; i++) {
                    compute[i] = equation.pop();
                    freshNumbesInArray = true;
                }
                while (!goodForCalcuation(compute)) {
                    if (!equation.isEmpty()) {
                        resultStack.offerLast(compute[0]);
                        compute[0] = compute[1];
                        compute[1] = compute[2];
                        compute[2] = equation.pop();
                    } else {
                        break;
                    }
                }

                if (goodForCalcuation(compute)) {
                    resultStack.offerLast(smallCalculate(compute));
                    freshNumbesInArray = false;

                }

                if (freshNumbesInArray) {
                    for (int i = 0; i < 3; i++) {
                        resultStack.offerLast(compute[i]);
                    }
                    freshNumbesInArray = false;
                }
            } else {
                while (!equation.isEmpty()) {
                    resultStack.offerLast(equation.pop());
                }

            }


            if (equation.isEmpty() && resultStack.size() == 1) {
                break;
            }


            if (equation.isEmpty()) {
                equation = resultStack.clone();
                resultStack.clear();
            }

        }

        result = new BigInteger(resultStack.pop());



    }

    private String smallCalculate(String[] calcArr) {
        BigInteger firstNum = new BigInteger(calcArr[0]);
        BigInteger secondNum = new BigInteger(calcArr[1]);

        if (calcArr[2].equals("+")) {
            return String.valueOf(firstNum.add(secondNum));

        } else if (calcArr[2].equals("-")) {
            return String.valueOf(firstNum.subtract(secondNum));

        } else if (calcArr[2].equals("*")) {
            return String.valueOf(firstNum.multiply(secondNum));
        } else {
            return String.valueOf(firstNum.divide(secondNum));
        }

    }

    private boolean goodForCalcuation(String[] arr) {
        if (arr[0].matches("-?[0-9]+"))
            if (arr[1].matches("-?[0-9]+"))
                if (arr[2].matches("[-+/*]"))
                    return true;
        return false;

    }

    BigInteger getResult() {
        return result;
    }
}
