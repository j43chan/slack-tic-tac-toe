package com.jermaine.tictactoe.controllers;

import com.jermaine.tictactoe.models.SlackRequest;
import com.jermaine.tictactoe.models.SlackResponse;
import com.jermaine.tictactoe.models.TicTacToe;

import java.util.HashMap;

public class ChallengeManager {
    public ChallengeManager(){
    }

    boolean startChallenge(final SlackRequest slackRequest, SlackResponse slackResponse, final  String challengedUser, HashMap<String, TicTacToe> gameRoomList){
        //check to see if a new challenge can be issued, since only one game can be played per channel
        if( gameRoomList.containsKey(slackRequest.getChannel_id()) ){
            slackResponse
                    .changeResponseTypeToEphemeral()
                    .setText("Challenged already issued, or A game is in progress; please either end the game by surrendering, or finish game before reissuing another challenge.")
                    .includeAvailableCommands();

            return false;
        }

        TicTacToe ttt = new TicTacToe();
        ttt.setPlayer1Name(slackRequest.getUser_name());
        ttt.setPlayer2Name(challengedUser);
        ttt.setGameInProgress(false);
        gameRoomList.put( slackRequest.getChannel_id(), ttt );
        slackResponse
                .changeResponseTypeToInChannel()
                .setText(slackRequest.getUser_name() + " has issued a ttt challenged to " + challengedUser +"\n" + challengedUser + " type /ttt accept to start the game!");

        return true;
    }
}
