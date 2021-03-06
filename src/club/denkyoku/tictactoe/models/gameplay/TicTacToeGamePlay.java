package club.denkyoku.tictactoe.models.gameplay;

import club.denkyoku.tictactoe.models.board.Board;
import club.denkyoku.tictactoe.models.board.Slot;
import club.denkyoku.tictactoe.models.gameplay.helpers.BoardRender;
import club.denkyoku.tictactoe.models.gameplay.helpers.TurnBased;
import club.denkyoku.tictactoe.models.player.Move;
import club.denkyoku.tictactoe.models.player.Player;
import club.denkyoku.tictactoe.services.input.KeyHandler;
import club.denkyoku.tictactoe.services.output.controls.MessageDialog;


public class TicTacToeGamePlay extends GamePlay {
    protected final String[] pauseGameMessages = new String[]{
            "Game is paused.",
            "What do you want to do?",
    };
    protected final MessageDialog.Button[] pauseGameButtons = new MessageDialog.Button[]{
            new MessageDialog.Button("Resume", 'R'),
            new MessageDialog.Button("Quit", 'Q'),
    };
    protected final MessageDialog.Button[] restartGameButtons = new MessageDialog.Button[]{
            new MessageDialog.Button("Have another try", 'T'),
            new MessageDialog.Button("Back to menu", 'B'),
    };

    protected final int boardSize;
    protected final Board<Slot> board;
    protected final Player[] players;
    protected int turn;
    protected int cursor_x;
    protected int cursor_y;

    /**
     * Create a new TicTacToe game
     * @param boardSize The size of the TicTacToe board.
     * @param players A list of <code>Player</code> objects.
     */
    public TicTacToeGamePlay(int boardSize, Player[] players) {
        this.boardSize = boardSize;
        this.players = players;
        this.board = new Board<>(boardSize, boardSize);
        this.turn = 0;
        this.cursor_x = 0;
        this.cursor_y = 0;

        this.doPlayerStatistics(players);
    }

    @Override
    public void start() {
        while (true) {
            // first reset the game states.
            this.reset();
            // print the first UI.
            this.printUI(false);

            boolean gameOver = false;
            int exitCode;
            Player winner;
            do {
                // do one turn
                exitCode = this.oneTurn();

                // means user want to quit
                if (exitCode == -2) {
                    GamePlay.showGameStatistics(this.players);
                    return;
                }
                // check if the game is over
                winner = this.checkWinner();

                if (winner != null) {
                    gameOver = true;
                } else {
                    // check if the game is draw
                    if (this.board.isFull()) {
                        gameOver = true;
                    }
                    this.nextTurn();
                }

            } while (!gameOver);

            // Reprint the board once before ending.
            this.printUI(false);

            String[] messages;
            GamePlay.doGameStatistics(this.players, winner);

            if (winner == null) {
                messages = new String[]{
                        "You draw the game",
                        "Please restart the game.",
                };
            } else {
                if (this.hasAI && !winner.isHumanPlayer()) {
                    messages = new String[] {
                            "You lose!",
                            "[Computer] beats you.",
                            "Would you like to have another try?",
                    };
                } else if (this.onlyOneHuman && this.hasAI) {
                    messages = new String[] {
                            "You win!",
                            "Congratulations! You beat the Computer!",
                            "Now do you want to play again?",
                    };
                } else if (!this.onlyOneHuman && this.hasAI) {
                    messages = new String[] {
                            String.format("%s win!", winner.getName()),
                            "Congratulations! You beat the Computer!",
                            "Now do you want to play again?",
                    };
                } else {
                    messages = new String[] {
                            String.format("%s win!", winner.getName()),
                            "Congratulations! You beat your friends.",
                            "Now do you want to play again?",
                    };
                }
            }
            int msgRet = MessageDialog.show(messages, restartGameButtons, 0, 1);
            if (msgRet == 1) {
                GamePlay.showGameStatistics(this.players);
                break;
            }
        }
    }



    public int oneTurn() {
        // first print the game without cursor.
        this.printUI(false);

        Player curTurnPlayer = this.players[this.turn];

        TurnBased.TurnBasedDataSync dataSync = new TurnBased.TurnBasedDataSync();
        KeyHandler keyHandler = new TurnBased.TurnBasedKeyHandler(dataSync);

        if (curTurnPlayer.isHumanPlayer()) {
            boolean redraw = false;
            boolean firstTouch = true;

            while (dataSync.keepRun) {
                if (redraw) {
                    this.printUI(true);
                    redraw = false;
                }

                dataSync.reset();
                keyHandler.run();

                if (firstTouch) {
                    redraw = true;
                    firstTouch = false;
                }

                if (dataSync.doExit) {
                    if (MessageDialog.show(pauseGameMessages, pauseGameButtons, 0, 0) == 1) {
                        keyHandler.exitInput();
                        return -2;
                    }
                } else if (dataSync.doMoveUp) {
                    this.cursor_x--;
                    if (this.cursor_x < 0) {
                        this.cursor_x = this.board.getWidth() - 1;
                    }
                    redraw = true;
                } else if (dataSync.doMoveDown) {
                    this.cursor_x++;
                    if (this.cursor_x >= this.board.getWidth()) {
                        this.cursor_x = 0;
                    }
                    redraw = true;
                } else if (dataSync.doMoveLeft) {
                    this.cursor_y--;
                    if (this.cursor_y < 0) {
                        this.cursor_y = this.board.getHeight() - 1;
                    }
                    redraw = true;
                } else if (dataSync.doMoveRight) {
                    this.cursor_y++;
                    if (this.cursor_y >= this.board.getHeight()) {
                        this.cursor_y = 0;
                    }
                    redraw = true;
                } else if (dataSync.doEnter) {
                    if (this.checkCanPut(this.cursor_x, this.cursor_y)) {
                        humanSelectMove(curTurnPlayer);
                        break;
                    }
                }
            }

            keyHandler.exitInput();
        } else {
            // AI player
            Move move = curTurnPlayer.getMove(this.board, this.players, null);
            this.board.put(move.x, move.y, new Slot(curTurnPlayer));
        }
        return 0;
    }

    /**
     * Called when human select a slot.
     */
    protected void humanSelectMove(Player curTurnPlayer) {
        this.board.put(this.cursor_x, this.cursor_y, new Slot(curTurnPlayer));
    }

    /**
     * Function used to switch to next player.
     */
    protected void nextTurn() {
        this.turn ++;
        if (this.turn >= this.players.length) {
            this.turn = 0;
        }
    }

    protected boolean checkCanPut(int x, int y) {
        return this.board.at(x, y) == null;
    }

    /**
     * Function used to check if the game have a winner.
     * @return The winner player. If there's no winner, return null.
     */
    protected Player checkWinner() {
        boolean same;
        Slot first;

        // line check
        for (int i = 0; i < this.boardSize; i++) {
            first = this.board.at(i, 0);

            // if the first slot is null, skip this line
            if (first == null)
                continue;

            same = true;
            for (int j = 1; j < this.boardSize; j++) {
                Slot cur = this.board.at(i, j);
                if (cur == null || cur.getPlayer() != first.getPlayer()) {
                    same = false;
                    break;
                }
            }

            if (same) {
                return first.getPlayer();
            }
        }

        // column check
        for (int j = 0; j < this.boardSize; j++) {
            first = this.board.at(0, j);

            // if the first slot is null, skip this column
            if (first == null)
                continue;

            same = true;
            for (int i = 1; i < this.boardSize; i++) {
                Slot cur = this.board.at(i, j);
                if (cur == null || cur.getPlayer() != first.getPlayer()) {
                    same = false;
                    break;
                }
            }

            if (same) {
                return first.getPlayer();
            }
        }

        // NW to SE check
        same = true;
        first = this.board.at(0, 0);
        if (first != null) {
            for (int i = 1; i < this.boardSize; i++) {
                Slot cur = this.board.at(i, i);
                if (cur == null || cur.getPlayer() != first.getPlayer()) {
                    same = false;
                    break;
                }
            }
            if (same) {
                return first.getPlayer();
            }
        }


        // NE to SW check
        same = true;
        first = this.board.at(0, this.boardSize - 1);
        if (first != null) {
            for (int i = 1; i < this.boardSize; i++) {
                Slot cur = this.board.at(i, this.boardSize - i - 1);
                if (cur == null || cur.getPlayer() != first.getPlayer()) {
                    same = false;
                    break;
                }
            }
            if (same) {
                return first.getPlayer();
            }
        }

        return null;
    }

    /**
     * Function used to print the game UI.
     * @param showCursor Whether to show the cursor.
     */
    protected void printUI(boolean showCursor) {
        String[] boardString = BoardRender.drawRectBoard(this.board,
                showCursor, this.cursor_x, this.cursor_y, null);
        TurnBased.drawUI(boardString, this.players, this.turn, null, null);
    }

    public void reset() {
        this.cursor_x = this.cursor_y = 0;
        this.turn = 0;
        this.board.clear();
    }
}
