package com.jermaine.tictactoe.controllers;

import com.jermaine.tictactoe.models.SlackRequest;
import com.jermaine.tictactoe.models.SlackResponse;
import com.jermaine.tictactoe.models.TicTacToe;
import org.springframework.boot.autoconfigure.web.ErrorController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;

@RestController
public class CommandController implements ErrorController {
    public final static String SLACK_TOKEN = "KmL99uWHnNnIj8TwAkhFqc6B";
    private static HashMap<String, TicTacToe> gameRoomList = new HashMap<>();
    private ChallengeManager challengeManager = new ChallengeManager();
    private AcceptManager acceptManager = new AcceptManager();
    private PlayManager playManager = new PlayManager();
    private SurrenderManager surrenderManager = new SurrenderManager();

    @RequestMapping(value="/tictactoe/update")
    public void update(SlackRequest request){

    }

    @RequestMapping(value="/tictactoe")
    public SlackResponse tictactoe(SlackRequest request){
        SlackResponse slackResponse = new SlackResponse();
        //check to make sure token is from registered team
        if( request.getToken()!= null && false == request.getToken().equals(SLACK_TOKEN) ){
            return slackResponse
                    .changeResponseTypeToEphemeral()
                    .setText("Your slack team is not supported!");
        }


        if( request.getText() == null ){
            return slackResponse
                    .changeResponseTypeToEphemeral()
                    .setText("make sure you specify a command");
        }
        String token[] = request.getText().split(" ");
        String command = token[0];

        switch( command ){
            case "challenge":
                challengeManager.startChallenge( request, slackResponse, token[1], gameRoomList );
                break;
            case "accept":
                acceptManager.accept(request, gameRoomList.get(request.getChannel_id()), slackResponse);
                break;
            case "play":
                playManager.startPlay(gameRoomList.get(request.getChannel_id()), token[1], token[2], slackResponse);
                break;
            case "surrender":
                surrenderManager.surrender();
                break;
            case "help":
            default:
                slackResponse.includeAvailableCommands();
        }

        return slackResponse;
    }

    @Override
    public String getErrorPath() {
        return null;
    }
}
