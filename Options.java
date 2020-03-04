package calculator;

public class Options {
    private String option;
    private boolean terminate;

    Options(String option) {
        identifyOption(option);
    }

    private void identifyOption(String opt) {
        if (opt.matches("/help")) {
            terminate = false;
            option = "Input the equation in one line \n" +
                    " - - is treated as + \n" +
                    "+ - is treated as - ";
        } else if (opt.matches("/exit")) {
            terminate = true;
            option = "Bye!";
        } else if (opt.matches("/.+")) {
            terminate = false;
            option = "Unknown command";

        } else if (opt.matches("Invalid assignment")) {
            terminate = false;
            option = opt;
        } else if (opt.matches("Invalid identifier")) {
            terminate = false;
            option = opt;
        } else if (opt.matches("Unknown variable")) {
            terminate = false;
            option = opt;
        } else {
            terminate = false;
            option = "Invalid expression";
        }

    }

    public String getOption() {
        return option;
    }

    public boolean isTerminate() {
        return terminate;
    }
}
