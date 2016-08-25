package com.jermaine.tictactoe.services;

import com.jermaine.tictactoe.exceptions.InvalidSlackRequest;
import com.jermaine.tictactoe.models.SlackRequest;
import com.jermaine.tictactoe.models.SlackResponse;
import com.jermaine.tictactoe.models.GameRoom;
import com.jermaine.tictactoe.utils.GifStrings;

import javafx.util.Pair;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class PlayManager {
    public SlackResponse startService(final SlackRequest slackRequest, final String playSlot, Map<String,GameRoom> gameRoomList) throws InvalidSlackRequest {
        if (slackRequest == null || slackRequest.getChannel_id() == null) {
            throw new InvalidSlackRequest("missing channel id");
        }

        Pair<Integer,Integer> playLocation = getRowColFromPlaySlot(playSlot);
        if(playLocation == null){
            return new SlackResponse()
                    .setText("you must specify a valid slot for play command");
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

            if (false == gameRoom.playTurn(playLocation.getKey(), playLocation.getValue())) {
                return new SlackResponse().setText("there is already a piece in that spot");
            } else {
                SlackResponse slackResponse = generateSlackResponse(gameRoom);

                //if a game has ended, remove it from the gameroom list so users can startService another game in that channel.
                if (gameRoom.hasGameEnded()) {
                    gameRoomList.remove(slackRequest.getChannel_id());
                }

                return slackResponse;
            }
        }
    }

    protected SlackResponse generateSlackResponse(GameRoom gameRoom){
        SlackResponse slackResponse = new SlackResponse()
                .changeResponseTypeToInChannel()
                .setText(gameRoom.getSlackRepresentationOfBoard())
                .addAttachmentText(gameRoom.getTurnInfo());

        if( gameRoom.isGameInWinState() ){
            slackResponse.addAttachment(null, null, GifStrings.WIN );
        }
        else if( gameRoom.isGameInDrawState() ){
            slackResponse.addAttachment(null, null, GifStrings.DRAW );
        }else{
            slackResponse.includePlayCommand();
        }
        return slackResponse;
    }

    protected Pair<Integer,Integer> getRowColFromPlaySlot(final String playSlot){
        Integer playSlotIndex;
        try {
            playSlotIndex = Integer.parseInt(playSlot);

            switch(playSlotIndex) {
                case 1:
                    return new Pair<>(0,0);
                case 2:
                    return new Pair<>(0,1);
                case 3:
                    return new Pair<>(0,2);
                case 4:
                    return new Pair<>(1,0);
                case 5:
                    return new Pair<>(1,1);
                case 6:
                    return new Pair<>(1,2);
                case 7:
                    return new Pair<>(2,0);
                case 8:
                    return new Pair<>(2,1);
                case 9:
                    return new Pair<>(2,2);
                default:
                    return null;
            }
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
