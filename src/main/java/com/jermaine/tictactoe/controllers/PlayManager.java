package com.jermaine.tictactoe.controllers;

import com.jermaine.tictactoe.models.SlackRequest;
import com.jermaine.tictactoe.models.SlackResponse;
import com.jermaine.tictactoe.models.TicTacToe;
import java.util.concurrent.ConcurrentHashMap;

public class PlayManager {
    public SlackResponse startPlay(final SlackRequest slackRequest, final String inputRow, final String inputCol, ConcurrentHashMap<String,TicTacToe> gameRoomList){
        TicTacToe gameRoom = gameRoomList.get(slackRequest.getChannel_id());
        if( gameRoom == null ){
            return new SlackResponse()
                    .setText("There is no game associated with this channel; please challenge someone to start a game.")
                    .includeAvailableCommands();
        }

        if(false == gameRoom.hasGameStarted()){
            return new SlackResponse().setText("Game has not been accepted yet");
        }

        //make sure it is the players turn
        if(false == gameRoom.getCurrentUserId().equals(slackRequest.getUser_id())){
            return new SlackResponse().setText("It is not your turn!");
        }

        //make sure command is valid( i.e in the form of [row] [col] where 1 <= row <= 3 and 1 <= col <= 3
        Integer row;
        Integer col;

        try{
            row = Integer.parseInt(inputRow);
            col = Integer.parseInt(inputCol);
            if( row > 3 || row < 1 || col > 3 || col < 1){
                throw new NumberFormatException();
            }
        }
        catch (NumberFormatException e){
            return new SlackResponse()
                    .setText("you must specify a valid row col for play command")
                    .includeAvailableCommands();
        }

        //note: the column and rows indices passed in are NOT based 0.  I've done some play testing with non developer friends and most
        //of them preferred base 1 as input.
        row --;
        col --;

        if( false == gameRoom.playTurn(row, col) ){
            return new SlackResponse().setText("there is already a piece in that spot");
        }else {
            SlackResponse slackResponse = new SlackResponse()
                    .changeResponseTypeToInChannel()
                    .setText(gameRoom.getSlackRepresentationOfBoard())
                    .addAttachmentText(gameRoom.currentTurnInformation());

            //if a game has ended, remove it from the gameroom list so users can start another game in that channel.
            if(gameRoom.hasGameEnded()){
                gameRoomList.remove(slackRequest.getChannel_id());
            }else{
                slackResponse.includePlayCommand();
            }

            return slackResponse;
        }
    }
}
