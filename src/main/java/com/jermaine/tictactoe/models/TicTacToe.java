package com.jermaine.tictactoe.models;

import java.util.ArrayList;
import java.util.Vector;

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
        boardString.append("```");
        boardString.append("|").append(board[0][0]).append("|").append(board[0][1]).append("|").append(board[0][2]).append("|").append("\n");
        boardString.append("|").append(board[1][0]).append("|").append(board[1][1]).append("|").append(board[1][2]).append("|").append("\n");
        boardString.append("|").append(board[2][0]).append("|").append(board[2][1]).append("|").append(board[2][2]).append("|").append("\n");
        boardString.append("```");
        return boardString.toString();
    }

    public Vector<SlackMsgButton> generateBoardButtonsForRow(int row){
        Vector<SlackMsgButton> buttons = new Vector<>();
        for( int j = 0; j < 3; j ++ ){
            SlackMsgButton newButton = new SlackMsgButton();
            newButton.text = getButtonText(board[row][j]);
            buttons.add( newButton );
        }
        return buttons;
    }

    private String getButtonText( char token){
        if( token =='x'){
            return ":X:";
        }else if( token =='o'){
            return ":O:";
        }else{
            return "----";
        }
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
