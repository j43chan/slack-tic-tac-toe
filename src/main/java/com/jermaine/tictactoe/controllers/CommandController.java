package com.jermaine.tictactoe.controllers;

import com.jermaine.tictactoe.models.SlackRequest;
import com.jermaine.tictactoe.models.SlackResponse;
import com.jermaine.tictactoe.models.TicTacToe;
import org.springframework.boot.autoconfigure.web.ErrorController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.concurrent.ConcurrentHashMap;

@RestController
public class CommandController implements ErrorController {
    public final static String SLACK_TOKEN = "KmL99uWHnNnIj8TwAkhFqc6B";
    private final static ConcurrentHashMap<String, TicTacToe> gameRoomList = new ConcurrentHashMap<>(); //mapping for channel names to games
    private ChallengeManager challengeManager = new ChallengeManager();
    private AcceptManager acceptManager = new AcceptManager();
    private PlayManager playManager = new PlayManager();
    private DropChallengeManager dropChallengeManager = new DropChallengeManager();

    @RequestMapping(value="/tictactoe")
    public SlackResponse tictactoe(SlackRequest request){
        //check to make sure token is from registered team
        if( request.getToken() == null || false == request.getToken().equals(SLACK_TOKEN) ){
            return new SlackResponse()
                    .changeResponseTypeToEphemeral()
                    .setText("Your slack team is not supported!");
        }

        if( request.getChannel_id() == null ){
            return new SlackResponse().setText("invalid channel id");
        }

        if( request.getText() == null ){
            return new SlackResponse()
                    .changeResponseTypeToEphemeral()
                    .setText("make sure you specify a command");
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
                    slackResponse = challengeManager.startChallenge(request, token[1], gameRoomList);
                    break;
                case "accept":
                    slackResponse = acceptManager.accept(request, gameRoomList);
                    break;
                case "play":
                    slackResponse = playManager.startPlay(request, token[1], token[2], gameRoomList);
                    break;
                case "drop":
                    slackResponse = dropChallengeManager.start(request, gameRoomList);
                    break;
                case "help":
                    slackResponse = new SlackResponse().includeAvailableCommands();
                    break;
                default:
                    throw new Exception("Invalid Command");
            }
        }
        catch (Exception e){
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
