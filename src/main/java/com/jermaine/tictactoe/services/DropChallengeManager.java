package com.jermaine.tictactoe.services;

import com.jermaine.tictactoe.exceptions.InvalidSlackRequest;
import com.jermaine.tictactoe.models.SlackRequest;
import com.jermaine.tictactoe.models.SlackResponse;
import com.jermaine.tictactoe.models.GameRoom;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class DropChallengeManager {
    public SlackResponse startService(final SlackRequest slackRequest, Map<String,GameRoom> gameRoomList) throws InvalidSlackRequest{
        if( slackRequest == null || slackRequest.getChannel_id() == null ){
            throw new InvalidSlackRequest("missing channel id");
        }

        GameRoom gameRoom = gameRoomList.get(slackRequest.getChannel_id());
        if(gameRoom == null){
            return new SlackResponse().setText("the current channel has no game in progress");
        }

        /*
            NOTE: This is synchronized to prevent Concurrent startService/drop requests
            The Synchronization happens on object level so it will not block for requests
            with different channel ids.
         */
        synchronized (gameRoom) {

            if( false == gameRoomList.containsKey(slackRequest.getChannel_id())){
                //room has been removed,  possible do to someone dropping the game concurrently
                return new SlackResponse()
                        .setText("Cannot Drop Challenge, game has already been dropped");
            }


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
