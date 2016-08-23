package com.jermaine.tictactoe.controllers;

import com.jermaine.tictactoe.exceptions.InvalidSlackRequest;
import com.jermaine.tictactoe.models.SlackRequest;
import com.jermaine.tictactoe.models.SlackResponse;
import com.jermaine.tictactoe.models.GameRoom;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class PlayManager {
    public SlackResponse startPlay(final SlackRequest slackRequest, final String playSlot, Map<String,GameRoom> gameRoomList) throws InvalidSlackRequest {
        if (slackRequest == null || slackRequest.getChannel_id() == null) {
            throw new InvalidSlackRequest("missing channel id");
        }

        GameRoom gameRoom = gameRoomList.get(slackRequest.getChannel_id());
        if (gameRoom == null) {
            return new SlackResponse()
                    .setText("There is no game associated with this channel; please challenge someone to start a game.");
        }

        /*
            NOTE: This is synchronized to prevent Concurrent play/play requests
            This is to prevent players from making more then 1 move per turn.

            The Synchronization happens on object level so it will not block for requests
            with different channel ids.
         */
        synchronized (gameRoom) {

            if (false == gameRoom.hasGameStarted()) {
                return new SlackResponse().setText("Game has not been accepted yet.");
            }

            //make sure it is the players turn
            if (false == gameRoom.getCurrentUserId().equals(slackRequest.getUser_id())) {
                return new SlackResponse().setText("It is not your turn!");
            }

            //make sure command is valid( i.e in the form of [row] [col] where 1 <= row <= 3 and 1 <= col <= 3
            Integer playSlotIndex;
            int[] rowColOutParam = {-1, -1};

            try {
                playSlotIndex = Integer.parseInt(playSlot);

                if (false == getRowCol(rowColOutParam, playSlotIndex)) {
                    throw new InvalidSlackRequest("Invalid Slot index for play command");
                }

            } catch (NumberFormatException | InvalidSlackRequest e) {
                return new SlackResponse()
                        .setText("you must specify a valid slot for play command")
                        .includeAvailableCommands();
            }

            if (false == gameRoom.playTurn(rowColOutParam[0], rowColOutParam[1])) {
                return new SlackResponse().setText("there is already a piece in that spot");
            } else {
                SlackResponse slackResponse = new SlackResponse()
                        .changeResponseTypeToInChannel()
                        .setText(gameRoom.getSlackRepresentationOfBoard())
                        .addAttachmentText(gameRoom.getTurnInfo());

                //if a game has ended, remove it from the gameroom list so users can start another game in that channel.
                if (gameRoom.hasGameEnded()) {
                    gameRoomList.remove(slackRequest.getChannel_id());
                } else {
                    slackResponse.includePlayCommand();
                }

                return slackResponse;
            }
        }
    }

    protected boolean getRowCol(int[] outParamRowCol, int position) {
        switch(position) {
            case 1:
                outParamRowCol[0] = 0;
                outParamRowCol[1] = 0;
                break;
            case 2:
                outParamRowCol[0] = 0;
                outParamRowCol[1] = 1;
                break;
            case 3:
                outParamRowCol[0] = 0;
                outParamRowCol[1] = 2;
                break;
            case 4:
                outParamRowCol[0] = 1;
                outParamRowCol[1] = 0;
                break;
            case 5:
                outParamRowCol[0] = 1;
                outParamRowCol[1] = 1;
                break;
            case 6:
                outParamRowCol[0] = 1;
                outParamRowCol[1] = 2;
                break;
            case 7:
                outParamRowCol[0] = 2;
                outParamRowCol[1] = 0;
                break;
            case 8:
                outParamRowCol[0] = 2;
                outParamRowCol[1] = 1;
                break;
            case 9:
                outParamRowCol[0] = 2;
                outParamRowCol[1] = 2;
                break;
        }

        if( outParamRowCol[0] >= 0 && outParamRowCol[1] >= 0){
            return true;
        }
        return false;
    }
}
