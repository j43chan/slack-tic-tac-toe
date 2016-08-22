package com.jermaine.tictactoe.controllers;

import com.jermaine.tictactoe.models.SlackRequest;
import com.jermaine.tictactoe.models.SlackResponse;
import com.jermaine.tictactoe.models.TicTacToe;

import java.util.Map;

public class DropChallengeManager {
    public SlackResponse start(SlackRequest slackRequest, Map<String,TicTacToe> gameRoomList){
        TicTacToe gameRoom = gameRoomList.get(slackRequest.getChannel_id());
        if(gameRoom == null){
            return new SlackResponse().setText("the current channel has no game in progress");
        }

        if(gameRoom.isWaitingToBeAccepted() && gameRoom.getPlayer1Name().equals(slackRequest.getUser_name()) ){
            gameRoomList.remove(slackRequest.getChannel_id());
            return new SlackResponse()
                    .changeResponseTypeToInChannel()
                    .setText("game has been dropped");
        }

        return new SlackResponse().setText("cannot drop a game that you didn't create, or is in progress");
    }
}
