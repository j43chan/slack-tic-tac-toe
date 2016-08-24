package com.jermaine.tictactoe.controllers;

import com.jermaine.tictactoe.exceptions.InvalidSlackRequest;
import com.jermaine.tictactoe.models.SlackRequest;
import com.jermaine.tictactoe.models.SlackResponse;
import com.jermaine.tictactoe.models.GameRoom;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class ChallengeManager {
    public ChallengeManager(){
    }

    public SlackResponse startService(final SlackRequest slackRequest,
                                      final String challengedUser,
                                      Map<String, GameRoom> gameRoomList) throws InvalidSlackRequest{

        if( slackRequest == null || slackRequest.getChannel_id() == null ){
            throw new InvalidSlackRequest("missing channel id");
        }

        //check to see if a new challenge can be issued since only one game can be played per channel
        //use channel id instead of channel name incase something gets renamed.
        if( gameRoomList.containsKey(slackRequest.getChannel_id()) ){
            return new SlackResponse().setText("Challenged already issued, or a game is in progress; please wait for the game to startService, or finish game before reissuing another challenge.");
        }

        //create a new game of tictactoe and associate it with the requested channel.
        GameRoom newGameRoom = new GameRoom();
        newGameRoom.setPlayer1Name(slackRequest.getUser_name());
        newGameRoom.setPlayer1UserId(slackRequest.getUser_id());
        newGameRoom.setPlayer2Name(challengedUser); //we set the challenged user here so that later on we can verify who accepts the challenge.

        //use `putIfAbsent` instead of `put` so that 2 threads coming in cannot clobber each others' game room
        if( null != gameRoomList.putIfAbsent( slackRequest.getChannel_id(), newGameRoom ) ){
            return new SlackResponse().setText("Challenged already issued, or a game is in progress; please wait for the game to startService, or finish game before reissuing another challenge.");
        }

        StringBuilder challengeMsg = new StringBuilder(slackRequest.getUser_name());
        challengeMsg = challengeMsg.append(" has issued a ttt challenge to ")
                        .append(challengedUser)
                        .append("\n")
                        .append("<@")
                        .append(challengedUser)
                        .append("> ")
                        .append("type ```/ttt accept``` to start the game!");

        return new SlackResponse()
                .changeResponseTypeToInChannel()
                .setText(challengeMsg.toString());
    }
}
