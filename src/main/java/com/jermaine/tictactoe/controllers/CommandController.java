package com.jermaine.tictactoe.controllers;

import com.jermaine.tictactoe.exceptions.InvalidSlackRequest;
import com.jermaine.tictactoe.models.SlackRequest;
import com.jermaine.tictactoe.models.SlackResponse;
import com.jermaine.tictactoe.models.GameRoom;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.ErrorController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.concurrent.ConcurrentHashMap;

@RestController
public class CommandController implements ErrorController {
    public final static String SLACK_TOKEN = "KmL99uWHnNnIj8TwAkhFqc6B";
    private final static ConcurrentHashMap<String, GameRoom> gameRoomList = new ConcurrentHashMap<>(); //mapping for channel names to games

    @Autowired
    private ChallengeManager challengeManager;

    @Autowired
    private AcceptManager acceptManager;

    @Autowired
    private PlayManager playManager;

    @Autowired
    private DropChallengeManager dropChallengeManager;

    @RequestMapping(value="/tictactoe")
    public SlackResponse tictactoe(SlackRequest request){
        //check to make sure token is from registered team
        if( request.getToken() == null || false == request.getToken().equals(SLACK_TOKEN) ){
            return new SlackResponse().setText("Your slack team is not supported! Please check your token!");
        }

        if( request.getChannel_id() == null){
            return new SlackResponse().setText("invalid channel id");
        }

        if( request.getText() == null ){
            return new SlackResponse().setText("make sure you specify a command");
        }

        String token[] = request.getText().split(" ");
        String command = token[0];
        if( command != null ){
            command = command.toLowerCase();
        }

        SlackResponse slackResponse;

        try {
            switch (command) {
                case "challenge":
                    if( token.length != 2 ){
                        throw new InvalidSlackRequest("invalid command");
                    }
                    slackResponse = challengeManager.startService(request, token[1], gameRoomList);
                    break;
                case "accept":
                    slackResponse = acceptManager.startService(request, gameRoomList);
                    break;
                case "play":
                    if( token.length != 2 ){
                        throw new InvalidSlackRequest("invalid command");
                    }
                    slackResponse = playManager.startService(request, token[1], gameRoomList);
                    break;
                case "drop":
                    slackResponse = dropChallengeManager.startService(request, gameRoomList);
                    break;
                case "help":
                    slackResponse = new SlackResponse().includeAvailableCommands();
                    break;
                default:
                    throw new InvalidSlackRequest("Invalid Command");
            }
        }
        catch (InvalidSlackRequest e){
            //invalid parameters passed in, print the command list for user
            slackResponse = new SlackResponse()
                    .includeAvailableCommands()
                    .setText("invalid command/parameters");
        }

        return slackResponse;
    }

    @Override
    public String getErrorPath() {
        return null;
    }
}
