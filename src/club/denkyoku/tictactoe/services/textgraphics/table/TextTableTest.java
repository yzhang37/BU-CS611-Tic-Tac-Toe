package club.denkyoku.tictactoe.services.textgraphics.table;

import club.denkyoku.tictactoe.services.output.terminal.ConsoleHelper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TextTableTest {

    @Test
    void getMonoArray() {
        int[] widths = new int[] {6, 6, 6};
        int[] heights = new int[] {2, 2, 2};
        TextTable textTable = new TextTable(heights, widths, BoxStyle.Engraved);
        var monoArray = textTable.GetMonoArray();
        ConsoleHelper.printScreen(monoArray.toScreen());
    }
}