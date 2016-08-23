package com.jermaine.tictactoe.controllers;

import com.jermaine.tictactoe.exceptions.InvalidSlackRequest;
import com.jermaine.tictactoe.models.SlackRequest;
import com.jermaine.tictactoe.models.SlackResponse;
import com.jermaine.tictactoe.models.TicTacToe;

import java.util.Map;

public class DropChallengeManager {
    public SlackResponse start(final SlackRequest slackRequest, Map<String,TicTacToe> gameRoomList) throws InvalidSlackRequest{
        if( slackRequest == null || slackRequest.getChannel_id() == null ){
            throw new InvalidSlackRequest("missing channel id");
        }

        TicTacToe gameRoom = gameRoomList.get(slackRequest.getChannel_id());
        if(gameRoom == null){
            return new SlackResponse().setText("the current channel has no game in progress");
        }

        synchronized (gameRoom.getLock()) {
            if (false == gameRoom.isWaitingToBeAccepted() ){
                return new SlackResponse().setText("Cannot Drop a Game that is in progress");
            }
            if( false == gameRoom.getPlayer1Name().equals(slackRequest.getUser_name())) {
                return new SlackResponse().setText("cannot drop a game that you didn't create");
            }

            gameRoomList.remove(slackRequest.getChannel_id());
            return new SlackResponse()
                    .changeResponseTypeToInChannel()
                    .setText("game has been dropped");
        }
    }
}
