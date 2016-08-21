package com.jermaine.tictactoe.models;

public class TicTacToe {
    private char board[][] = {{' ', ' ', ' '}, {' ', ' ', ' '}, {' ', ' ', ' '}};
    private String player1Name;
    private String player2Name;
    private char currentToken = 'x';
    private String player1UserId;
    private String player2UserId;
    private char player1Token = 'x';
    private char player2Token = 'o';
    private short playerTurnIndex = 0;
    private boolean gameInProgress = false;

    @Override
    public String toString() {

        StringBuilder boardString = new StringBuilder();
        boardString.append("```| 1 | 2 | 3 |\n|---+---+---|\n| 4 | 5 | 6 |\n|---+---+---|\n| 7 | 8 | 9 |```");
        boardString.setCharAt( 5, board[0][0]);
        boardString.setCharAt( 9, board[0][1]);
        boardString.setCharAt( 13, board[0][2]);

        boardString.setCharAt( 33, board[1][0]);
        boardString.setCharAt( 37, board[1][1]);
        boardString.setCharAt( 41, board[1][2]);

        boardString.setCharAt( 61, board[2][0]);
        boardString.setCharAt( 65, board[2][1]);
        boardString.setCharAt( 69, board[2][2]);
        return boardString.toString();
    }

    public String getPlayer1Name() {
        return player1Name;
    }

    public void setPlayer1Name(String player1Name) {
        this.player1Name = player1Name;
    }

    public String getPlayer2Name() {
        return player2Name;
    }

    public void setPlayer2Name(String player2Name) {
        this.player2Name = player2Name;
    }

    public boolean getGameInProgress() {
        return gameInProgress;
    }

    public void setGameInProgress(boolean gameInProgress) {
        this.gameInProgress = gameInProgress;
    }

    private char getNextToken(){
        if( currentToken == 'x'){
            currentToken = 'o';
        }else{
            currentToken = 'x';
        }

        return currentToken;
    }

    public boolean playTurn( int row, int col ){
        if( board[row][col] != ' '){
            return false;
        }
        board[row][col] = getNextToken();
        return true;
    }
}
