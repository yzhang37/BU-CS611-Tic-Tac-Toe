package club.denkyoku.TicTacToe;

import java.util.Arrays;
import java.util.StringJoiner;

public class Game {
    protected Board board;
    protected Player[] players;
    protected int turn;
    // 控制人类玩家使用的光标位置。
    protected int cursor_x;
    protected int cursor_y;

    // 创建一局游戏。需要提供 Player 的对象列表，以及棋盘对象。
    public Game(Board board, Player[] players) {
        this.board = board;
        this.players = players;
        this.turn = 0;
        this.cursor_x = 0;
        this.cursor_y = 0;
    }

    public void nextTurn() {
        this.turn ++;
        if (this.turn >= this.players.length) {
            this.turn = 0;
        }
    }

    public Player getPlayerAt(int index) {
        return this.players[index];
    }

    public String[] renderBoard(boolean showCursor) {
        String[] lines = new String[2 * this.board.getHeight() + 1];
        StringJoiner joiner1, joiner2;
        for (int i = 0; i < this.board.getHeight(); ++i) {
            if (i == 0)
                joiner1 = new StringJoiner("┬", "┌", "┐");
            else
                joiner1 = new StringJoiner("┼", "├", "┤");

            joiner2 = new StringJoiner("│", "│", "│");

            for (int j = 0; j < this.board.getWidth(); ++j) {
                joiner1.add("─");
                char symbolForJoiner2 = ' ';
                if (showCursor && this.cursor_x == i && this.cursor_y == j) {
                    symbolForJoiner2 = '█';
                } else {
                    int cellValue = this.board.at(i, j);
                    if (cellValue > 0) {
                        symbolForJoiner2 = this.getPlayerAt(cellValue - 1).getSymbol();
                    }
                }
                joiner2.add(String.valueOf(symbolForJoiner2));
            }
            lines[2 * i] = joiner1.toString();
            lines[2 * i + 1] = joiner2.toString();
        }
        joiner1 = new StringJoiner("┴", "└", "┘");
        for (int j = 0; j < this.board.getWidth(); ++j) {
            joiner1.add("─");
        }
        lines[2 * this.board.getHeight()] = joiner1.toString();
        return lines;
    }

    public void printUI(boolean showCursor) {
        ConsoleHelper.CleanConsole();
        String[] boardString = this.renderBoard(showCursor);

        // 因为这里棋盘一定是等宽的，所以取第一个就是最大的大小。
        int maxBoardWidth = boardString[0].length();

        // 计算终端最大的高度，以计算具体需要打印多少个用户的信息。
        // 当前用户是必须要打印出来的。
        int maxPrintPlayers = ConsoleHelper.GetConsoleHeight() / 3;
        int firstPrintPlayer = 0;
        if (turn >= maxPrintPlayers) {
            firstPrintPlayer = turn - maxPrintPlayers + 1;
        }
        int maxPrintPlayersLines = Math.min(maxPrintPlayers * 3, this.players.length * 3);

        for (int lineId = 0, curPrintPlayer = firstPrintPlayer;
             lineId < Math.max(maxPrintPlayersLines, boardString.length);
             ++lineId) {

            StringBuilder sb = new StringBuilder();

            if (lineId < boardString.length) {
                // 如果在棋盘的部分，就输出棋盘的内容
                sb.append(boardString[lineId]);
            } else {
                // 否则就生成棋盘同样宽度的空白行
                sb.append(" ".repeat(maxBoardWidth));
            }

            if (curPrintPlayer < Math.min(firstPrintPlayer + 3, this.players.length)) {
                // 每 3 行的第 0 行是空白的。
                if (lineId % 3 == 1) {
                    sb.append("  ");
                    if (curPrintPlayer == this.turn) {
                        sb.append("▶");
                    } else {
                        sb.append(" ");
                    }
                    sb.append(this.getPlayerAt(curPrintPlayer).getSymbol());
                    sb.append(" ");
                    sb.append(this.getPlayerAt(curPrintPlayer).getName());
                } else if (lineId % 3 == 2) {
                    if (curPrintPlayer == this.turn) {
                        // 如果是当前玩家，就输出当前玩家的提示信息。
                        sb.append("     ");

                        if (this.getPlayerAt(curPrintPlayer).isHumanPlayer()) {
                            sb.append("Your turn");
                        } else {
                            sb.append("AI thinking...");
                        }
                    }
                    curPrintPlayer ++;
                }
            }
            ConsoleHelper.println(sb.toString());
        }
    }

    public void start() {
        int turnResult = 0;
        do {
            turnResult = this.oneTurn();
        } while (turnResult == 0);

        if (turnResult == -1) {
            ConsoleHelper.println("Draw!");
        } else {
            ConsoleHelper.println("Winner: " + this.getPlayerAt(turnResult - 1).getName());
        }
    }

    /**
     * 执行一次 Turn
     * @return 返回值：0 表示继续,
     *                >= 1 表示获胜的玩家的 id + 1
     *                -1 表示棋盘已满。
     */
    public int oneTurn() {
        this.printUI(false);
        // 现在轮到玩家来下棋。
        Player curTurnPlayer = this.getPlayerAt(this.turn);
        if (curTurnPlayer.isHumanPlayer()) {
            char[] buffer = new char[10];
            boolean redraw = false;
            boolean firstTouch = true;
            while (true) {
                if (redraw) {
                    ConsoleHelper.CleanConsole();
                    this.printUI(true);
                    redraw = false;
                }

                Arrays.fill(buffer, '\0');
                KeyHandler.getKey(buffer);

                if (firstTouch) {
                    redraw = true;
                    firstTouch = false;
                }
                // TODO: 可以增加暂停退出的功能。
                if (KeyHandler.isKeyUp(buffer)) {
                    this.cursor_x--;
                    if (this.cursor_x < 0) {
                        this.cursor_x = this.board.getWidth() - 1;
                    }
                    redraw = true;
                } else if (KeyHandler.isKeyDown(buffer)) {
                    this.cursor_x++;
                    if (this.cursor_x >= this.board.getWidth()) {
                        this.cursor_x = 0;
                    }
                    redraw = true;
                } else if (KeyHandler.isKeyLeft(buffer)) {
                    this.cursor_y--;
                    if (this.cursor_y < 0) {
                        this.cursor_y = this.board.getHeight() - 1;
                    }
                    redraw = true;
                } else if (KeyHandler.isKeyRight(buffer)) {
                    this.cursor_y++;
                    if (this.cursor_y >= this.board.getHeight()) {
                        this.cursor_y = 0;
                    }
                    redraw = true;
                } else if (KeyHandler.isEnter(buffer)) {
                    // TODO: 检查是否可以放置棋子
                    break;
                }
            }

        } else {
            // AI 玩家，就让 AI 来下棋。
            var move = curTurnPlayer.getMove(this.board);
            this.board.put(move.x, move.y, this.turn + 1);
        }

        // 判断游戏是否结束
        if (this.board.isFull()) {
            return -1;
        }
        int winner = this.board.check_win();
        if (winner > 0) {
            return winner;
        } else {
            this.nextTurn();
            return 0;
        }
    }
}
