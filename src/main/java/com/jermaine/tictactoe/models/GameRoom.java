package com.jermaine.tictactoe.models;

import java.util.Random;

public class GameRoom {
    protected int board[][] = {{0,0,0},{0,0,0},{0,0,0}};
    protected String player1Name;
    protected String player2Name;
    private String player1UserId;
    private String player2UserId;
    private String currentUserId;
    protected int currentToken = -1; // -1 for 0, 1 for X
    protected int currentPlayerTurn = -1; // 1 for player1, 2 for player 2
    private final static Random random = new Random();
    private final int BOARD_SIZE = 3;
    protected int turnsRemaining = 9; //when this reaches 0, the match is a draw
    protected volatile GAME_STATE gameState = GAME_STATE.CREATED;
    private int[] colCount = { 0, 0, 0};
    private int[] rowCount = {0, 0, 0};
    private int diagCount = 0;
    private int reverseDiagCount = 0;

    protected enum GAME_STATE {
        CREATED,
        STARTED,
        WIN,
        DRAW
    }

    @Override
    public String toString() {
        return getSlackRepresentationOfBoard();
    }

    public String getSlackRepresentationOfBoard() {
        StringBuilder boardString = new StringBuilder();
        boardString.append("```| 1 | 2 | 3 |\n|---+---+---|\n| 4 | 5 | 6 |\n|---+---+---|\n| 7 | 8 | 9 |```");
        boardString.setCharAt( 5, getTokenAsCharFromBoardPosition(0,0));
        boardString.setCharAt( 9, getTokenAsCharFromBoardPosition(0,1));
        boardString.setCharAt( 13, getTokenAsCharFromBoardPosition(0,2));

        boardString.setCharAt( 33, getTokenAsCharFromBoardPosition(1,0));
        boardString.setCharAt( 37, getTokenAsCharFromBoardPosition(1,1));
        boardString.setCharAt( 41, getTokenAsCharFromBoardPosition(1,2));

        boardString.setCharAt( 61, getTokenAsCharFromBoardPosition(2,0));
        boardString.setCharAt( 65, getTokenAsCharFromBoardPosition(2,1));
        boardString.setCharAt( 69, getTokenAsCharFromBoardPosition(2,2));
        return boardString.toString();
    }

    protected char getTokenAsCharFromBoardPosition(int row, int col){
        int token = board[row][col];
        if( token == -1){
            return 'O';
        }else if( token == 1 ){
            return 'X';
        }else{
            return ' ';
        }
    }

    public String getTurnInfo(){
        if(gameState == GAME_STATE.CREATED){
            return "Game has not been started!";
        }

        String currentPlayerName;

        if( currentPlayerTurn == 1 ){
            currentPlayerName = player1Name;
        }else{
            currentPlayerName = player2Name;
        }

        StringBuilder reply = new StringBuilder();
        if( gameState == GAME_STATE.WIN ){
            reply.append("Game has ended ")
                    .append(currentPlayerName)
                    .append(" wins!");
        }
        else if(gameState == GAME_STATE.DRAW){
            reply.append("Game has ended in a draw");
        }else{
            reply.append("<@")
                    .append(currentPlayerName)
                    .append("> it is your turn to play");
        }
        return reply.toString();
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

    protected int getNextToken(){
        if( currentToken == 1){
            currentToken = -1;
        }else{
            currentToken = 1;
        }
        return currentToken;
    }

    public boolean playTurn( int row, int col ){
        if( board[row][col] != 0){
            return false;
        }

        turnsRemaining --;

        int token = getNextToken();
        board[row][col] = token;

        if( checkWin(row, col, token) ){
            gameState = GAME_STATE.WIN;
        }
        else if( checkDraw() ){
            gameState = GAME_STATE.DRAW;
        }else{
            changeTurn();
        }

        return true;
    }

    protected boolean checkDraw(){
        return turnsRemaining == 0;
    }

    protected boolean checkWin(int row, int col, int token){
        if( row < 0
                || row >= BOARD_SIZE
                || col < 0
                || col >= BOARD_SIZE
                || (token != -1 && token != 0 && token != 1)){
            return false;
        }

        /**
         * keep track of running sum of each column, row and diag
         * since the peices are 1 and -1 respectively, a winning condition is met when one of the sums
         * reaches BOARD_SIZE;
         */
        rowCount[row] += token;
        colCount[col] += token;
        if( row == col ){
            diagCount += token;
        }

        if( (row == 0 && col == 2)
                || (row == 1 && col == 1)
                || (row == 2 && col == 0) ){
            reverseDiagCount +=token;
        }

        if( Math.abs(colCount[col]) == BOARD_SIZE
                || Math.abs(rowCount[row]) == BOARD_SIZE
                || Math.abs(diagCount) == BOARD_SIZE
                || Math.abs(reverseDiagCount) == BOARD_SIZE){
            return true;
        }

        return false;
    }

    protected void changeTurn(){
        if(gameState != GAME_STATE.STARTED){
            return;
        }

        if( currentPlayerTurn == 1){
            currentUserId = player2UserId;
            currentPlayerTurn = 2;
        }else{
            currentUserId = player1UserId;
            currentPlayerTurn = 1;
        }
    }

    public void startGame() {
        if( gameState != GAME_STATE.CREATED ){
            return;
        }
        //randomly assign who goes first
        currentPlayerTurn = random.nextInt(2) + 1;
        if( currentPlayerTurn == 1 ){
            currentUserId = player1UserId;
        }else{
            currentUserId = player2UserId;
        }

        gameState = GAME_STATE.STARTED;
    }

    public String getPlayer1UserId() {
        return player1UserId;
    }

    public void setPlayer1UserId(String player1UserId) {
        this.player1UserId = player1UserId;
    }

    public String getPlayer2UserId(){
        return player2UserId;
    }

    public void setPlayer2UserId(String player2UserId) {
        this.player2UserId = player2UserId;
    }

    public String getCurrentUserId() {
        return currentUserId;
    }

    public boolean hasGameStarted(){
        return gameState == GAME_STATE.STARTED;
    }

    public boolean hasGameEnded() {
        return gameState == GAME_STATE.WIN || gameState == GAME_STATE.DRAW;
    }

    public boolean isWaitingToBeAccepted(){
        return gameState == GAME_STATE.CREATED;
    }
}
