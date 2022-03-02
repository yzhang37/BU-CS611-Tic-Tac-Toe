package club.denkyoku.TicTacToe;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;

import biz.source_code.utils.RawConsoleInput;


public class KeyHandler {
    // The character array used for reading stream.
    public static char[] bufferUnix = new char[10];
    // the buffer reader used on Linux and Mac
    public static BufferedReader brUnix = new BufferedReader(new InputStreamReader(System.in));

    /**
     * The raw function for reading a key from
     * the console on Windows.
     * @param wait: type of <code>boolean</code>,
     *            if <code>true</code>,
     *            the function will wait until a key is pressed.
     * @return The raw input key's ASCII code (int).
     */
    private int rawGetKeyWindows(boolean wait) {
        try {
            return RawConsoleInput.read(wait);
        } catch (IOException e) {}
        return 0;
    }

    private void rawReadKeyBuffer() {
        Arrays.fill(KeyHandler.bufferUnix, '\0');
        try {
            brUnix.read(KeyHandler.bufferUnix);
        } catch (IOException e) {}
    }

    public final int run() {
        if (RawConsoleInput.getIsWindows()) {
            int key = this.rawGetKeyWindows(true);
        } else {
            // make call to read key stream
            this.rawReadKeyBuffer();

            var bf = KeyHandler.bufferUnix;
            if (bf[0] == 27 && bf[1] == 91 && bf[2] == 65 && bf[3] == 0)
                this.onKeyUp();
            else if (bf[0] == 27 && bf[1] == 91 && bf[2] == 66 && bf[3] == 0)
                this.onKeyDown();
            else if (bf[0] == 27 && bf[1] == 91 && bf[2] == 67 && bf[3] == 0)
                this.onKeyRight();
            else if (bf[0] == 27 && bf[1] == 91 && bf[2] == 68 && bf[3] == 0)
                this.onKeyLeft();
            else if (bf[0] == 27 && bf[1] == 0)
                this.onKeyEsc();
            else if (bf[0] == 10 && bf[1] == 0)
                this.onKeyEnter();
        }
        return 0;
    }

    void onKeyEsc() {}
    void onKeyF1() {}
    void onKeyF2() {}
    void onKeyF3() {}
    void onKeyF4() {}
    void onKeyF5() {}
    void onKeyF6() {}
    void onKeyF7() {}
    void onKeyF8() {}
    void onKeyF9() {}
    void onKeyF10() {}
    void onKeyF11() {}
    void onKeyF12() {}
    void onKeyEnter() {}
    void onKeyUp() {}
    void onKeyDown() {}
    void onKeyLeft() {}
    void onKeyRight() {}


    /**
     * Exit the Raw key input status.
     */
    public void exitInput() {
        try {
            RawConsoleInput.resetConsoleMode();
        } catch (IOException e) {}
    }
}

// TODO: isKeyEnter
// TODO: isKeyEsc
// TODO: isKeyUp
// TODO: isKeyDown
// TODO: isKeyLeft
// TODO: isKeyRight
