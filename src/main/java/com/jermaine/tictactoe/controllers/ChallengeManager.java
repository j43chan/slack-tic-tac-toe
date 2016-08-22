package com.jermaine.tictactoe.controllers;

import com.jermaine.tictactoe.models.SlackRequest;
import com.jermaine.tictactoe.models.SlackResponse;
import com.jermaine.tictactoe.models.TicTacToe;
import java.util.concurrent.ConcurrentHashMap;

public class ChallengeManager {
    public ChallengeManager(){
    }

    public synchronized SlackResponse startChallenge(final SlackRequest slackRequest,
                           final String challengedUser,
                           ConcurrentHashMap<String, TicTacToe> gameRoomList){

        //check to see if a new challenge can be issued since only one game can be played per channel
        //use channel id instead of channel name incase something gets renamed.
        if( gameRoomList.containsKey(slackRequest.getChannel_id()) ){
            return new SlackResponse().setText("Challenged already issued, or a game is in progress; please wait for the game to start, or finish game before reissuing another challenge.");
        }

        //create a new game of tictactoe and associate it with the requested channel.
        TicTacToe newGameRoom = new TicTacToe();
        newGameRoom.setPlayer1Name(slackRequest.getUser_name());
        newGameRoom.setPlayer2Name(challengedUser); //we set the challenged user here so that later on we can verify who accepts the challenge.
        newGameRoom.setPlayer1UserId(slackRequest.getUser_id());
        gameRoomList.put( slackRequest.getChannel_id(), newGameRoom );

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
