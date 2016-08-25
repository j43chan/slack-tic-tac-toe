package com.jermaine.tictactoe.services;

import com.jermaine.tictactoe.exceptions.InvalidSlackRequest;
import com.jermaine.tictactoe.models.SlackRequest;
import com.jermaine.tictactoe.models.SlackResponse;
import com.jermaine.tictactoe.models.GameRoom;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class StatusManager {
    public SlackResponse startService(final SlackRequest slackRequest, Map<String,GameRoom> gameRoomList) throws InvalidSlackRequest {
        if (slackRequest == null || slackRequest.getChannel_id() == null) {
            throw new InvalidSlackRequest("missing channel id");
        }

        GameRoom gameRoom = gameRoomList.get(slackRequest.getChannel_id());
        if (gameRoom == null) {
            return new SlackResponse()
                    .setText("There is no game associated with this channel; please challenge someone to start a game.");
        }

        /**
         * prevent game state from interweaving.
         */
        synchronized (gameRoom) {
            if (false == gameRoom.hasGameStarted()) {
                return new SlackResponse().setText("Game has not been accepted yet.");
            }

            return new SlackResponse()
                    .changeResponseTypeToInChannel()
                    .setText(gameRoom.getSlackRepresentationOfBoard())
                    .addAttachmentText(gameRoom.getTurnInfo());
        }
    }
}
