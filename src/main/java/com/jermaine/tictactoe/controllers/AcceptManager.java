package com.jermaine.tictactoe.controllers;

import com.jermaine.tictactoe.exceptions.InvalidSlackRequest;
import com.jermaine.tictactoe.models.SlackRequest;
import com.jermaine.tictactoe.models.SlackResponse;
import com.jermaine.tictactoe.models.TicTacToe;
import java.util.Map;

public class AcceptManager {
    public SlackResponse accept(final SlackRequest slackRequest, final Map<String,TicTacToe> gameRoomList) throws InvalidSlackRequest{
        if( slackRequest == null || slackRequest.getChannel_id() == null ){
            throw new InvalidSlackRequest("missing channel id");
        }
        TicTacToe gameRoom = gameRoomList.get(slackRequest.getChannel_id());

        if( gameRoom == null ){
            return new SlackResponse()
                    .setText("you must be challenged before accepting a request");
        }

        synchronized (gameRoom.getLock()) {

            if(false == gameRoomList.containsKey(slackRequest.getChannel_id())){
                //room has been removed, possibly do to someone dropping the game concurrently
                return new SlackResponse()
                        .setText("Cannot accept Challenge, game has been dropped");
            }

            if (false == gameRoom.isWaitingToBeAccepted()) {
                return new SlackResponse()
                        .setText("Game is not in a state to be accepted");
            }

            if (false == slackRequest.getUser_name().equals(gameRoom.getPlayer2Name())) {
                return new SlackResponse()
                        .setText("You are not the one being challenged");
            }

            gameRoom.setPlayer2UserId(slackRequest.getUser_id());
            gameRoom.startGame();

            return new SlackResponse()
                    .changeResponseTypeToInChannel()
                    .setText(gameRoom.getSlackRepresentationOfBoard())
                    .addAttachmentText(gameRoom.getTurnInfo())
                    .includePlayCommand();
        }
    }
}
