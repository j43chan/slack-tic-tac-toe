package com.jermaine.tictactoe.services;

import com.jermaine.tictactoe.exceptions.InvalidSlackRequest;
import com.jermaine.tictactoe.models.SlackRequest;
import com.jermaine.tictactoe.models.SlackResponse;
import com.jermaine.tictactoe.models.GameRoom;
import com.jermaine.tictactoe.utils.GifStrings;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class AcceptManager {
    public SlackResponse startService(final SlackRequest slackRequest, final Map<String,GameRoom> gameRoomList) throws InvalidSlackRequest{
        if( slackRequest == null || slackRequest.getChannel_id() == null ){
            throw new InvalidSlackRequest("missing channel id");
        }
        GameRoom gameRoom = gameRoomList.get(slackRequest.getChannel_id());

        if( gameRoom == null ){
            return new SlackResponse()
                    .setText("you must be challenged before accepting a request");
        }

        /*
            NOTE: This is synchronized to prevent Concurrent startService/drop requests
            EXAMPLE: Thread 1 comes in and tries to accept the game
                     Thread 2 comes in and tries to drop game
                     We need to make sure these 2 events never interweave

            The Synchronization happens on object level so it will not block for requests
            with different channel ids.
         */
        synchronized (gameRoom) {

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
                    .includePlayCommand()
                    .addAttachment(null, gameRoom.getTurnInfo(), GifStrings.CHALLENGE_ACCEPTED);
        }
    }
}
