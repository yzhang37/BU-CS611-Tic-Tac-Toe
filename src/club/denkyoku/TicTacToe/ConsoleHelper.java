package club.denkyoku.TicTacToe;

import java.io.IOException;
import java.io.PrintWriter;

public class ConsoleHelper {
    protected static final PrintWriter printWriter = new PrintWriter(System.out, true);

    static void CleanConsole() {
        printWriter.print("\033[H\033[2J");
    }

    static void println(String str) {
        printWriter.println(str);
    }

    static void print(String str) {
        printWriter.print(str);
    }

    static void print(char c) {
        printWriter.print(c);
    }

    static int GetConsoleWidth() {
        try {
            var result = ShellHelper.shell(new String[]{"tput cols"});
            if (result.ret == 0) {
                return Integer.parseInt(result.output);
            }
        } catch (IOException e) {}
        return 80; //default value
    }

    static int GetConsoleHeight() {
        try {
            var result = ShellHelper.shell(new String[]{"tput lines"});
            if (result.ret == 0) {
                return Integer.parseInt(result.output);
            }
        } catch (IOException e) {}
        return 24; //default value
    }
}
