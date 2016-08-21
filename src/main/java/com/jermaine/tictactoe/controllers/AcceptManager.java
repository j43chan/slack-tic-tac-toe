package com.jermaine.tictactoe.controllers;

import com.jermaine.tictactoe.models.SlackRequest;
import com.jermaine.tictactoe.models.SlackResponse;
import com.jermaine.tictactoe.models.TicTacToe;

public class AcceptManager {
    public void accept(SlackRequest slackRequest, TicTacToe gameRoom, SlackResponse slackResponse){
        if( gameRoom.getGameInProgress() == true ){
            slackResponse
                    .changeResponseTypeToEphemeral()
                    .setText("Game is already in progress");
            return;
        }
        if( slackRequest.getUser_name().equals(gameRoom.getPlayer2Name()) ){
            gameRoom.setGameInProgress(true);
            slackResponse
                    .changeResponseTypeToInChannel()
                    .setText(gameRoom.toString())
                    .addAttachmentText("Challenged Accepted by " + slackRequest.getUser_name());




        }else{
            slackResponse
                    .changeResponseTypeToEphemeral()
                    .setText("You are not the one being challenged");
        }
    }
}
