package com.jermaine.tictactoe.controllers;

import com.jermaine.tictactoe.models.SlackResponse;
import com.jermaine.tictactoe.models.TicTacToe;

public class PlayManager {
    public void startPlay(TicTacToe gameRoom, final String inputRow, final String inputCol, SlackResponse slackResponse){
        if( gameRoom == null ){
            slackResponse.changeResponseTypeToEphemeral().includeAvailableCommands();
            return;
        }

        if(false == gameRoom.getGameInProgress()){
            slackResponse.changeResponseTypeToEphemeral()
                    .setText("Game has not been accepted yet");
            return;
        }

        //make sure command is valid( i.e in the form of [row] [col] where 1 <= row <= 3 and 1 <= col <= 3
        Integer row = null;
        Integer col = null;

        try{
            row = Integer.parseInt(inputRow);
            col = Integer.parseInt(inputCol);
            if( row > 3 || row < 1 || col > 3 || col < 1){
                throw new NumberFormatException();
            }
        }
        catch (NumberFormatException e){
            slackResponse.changeResponseTypeToEphemeral()
                    .setText("you must specify a valid row col for play command")
                    .includeAvailableCommands();
            return;
        }

        //base row and col base 0 for array
        row --;
        col --;

        if( false == gameRoom.playTurn(row, col) ){
            slackResponse.changeResponseTypeToEphemeral().setText("there is already a peice in that spot");
            return;
        }else {

            slackResponse
                    .changeResponseTypeToInChannel()
                    .setText(gameRoom.toString());

            if( hasWon(gameRoom) ){
                slackResponse.addAttachmentText("game has ended!");
            }
        }

    }

    private boolean hasWon(final TicTacToe game){
        return false;
    }
}
