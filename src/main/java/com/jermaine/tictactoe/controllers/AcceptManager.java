package com.jermaine.tictactoe.controllers;

import com.jermaine.tictactoe.models.SlackRequest;
import com.jermaine.tictactoe.models.SlackResponse;
import com.jermaine.tictactoe.models.TicTacToe;

import java.util.Map;

public class AcceptManager {
    public SlackResponse accept(final SlackRequest slackRequest, final Map<String,TicTacToe> gameRoomList){
        TicTacToe gameRoom = gameRoomList.get(slackRequest.getChannel_id());

        if( gameRoom == null ){
            return new SlackResponse()
                    .setText("you must be challenged before accepting a request");
        }

        if( gameRoom.hasGameStarted() ){
            return new SlackResponse()
                    .setText("Game is already in progress");
        }

        if( false == slackRequest.getUser_name().equals(gameRoom.getPlayer2Name()) ){
            return new SlackResponse()
                    .setText("You are not the one being challenged");
        }

        gameRoom.setPlayer2UserId(slackRequest.getUser_id());
        gameRoom.startGame();

        return new SlackResponse()
                .changeResponseTypeToInChannel()
                .setText(gameRoom.getSlackRepresentationOfBoard())
                .addAttachmentText(gameRoom.currentTurnInformation())
                .includePlayCommand();
    }
}
