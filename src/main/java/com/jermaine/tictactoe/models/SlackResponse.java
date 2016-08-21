package com.jermaine.tictactoe.models;

import java.util.Vector;

public class SlackResponse {
    String text;
    String response_type = "ephemeral";
    Vector<SlackAttachment> attachments = new Vector<>();
    boolean hasIncludedAvailableCommand = false;
    final static String availableCommand =  "challenge [user_name] - issues a ttt challege towards [user_name] \n" +
                                            "accept - starts a ttt game after being challeged \n" +
                                            "play [row] [col] - puts your token down at [row] [col], note numbers need to be between 1 - 3 \n" +
                                            "surrender - you surrender the game, and the other player wins \n";


    public SlackResponse changeResponseTypeToEphemeral(){
        response_type = "ephemeral";
        return this;
    }

    public SlackResponse changeResponseTypeToInChannel(){
        response_type = "in_channel";
        return this;
    }

    public SlackResponse includeAvailableCommands(){
        if( hasIncludedAvailableCommand )
            return this;
        return addAttachmentText( availableCommand );
    }

    public SlackResponse addAttachmentText(final String attachmentText){
        SlackAttachment newAttachment = new SlackAttachment();
        newAttachment.setText(attachmentText);
        attachments.add(newAttachment);
        return this;
    }

    public SlackResponse setText(final String replyText){
        text = replyText;
        return this;
    }

    public String getText() {
        return text;
    }

    public String getResponse_type() {
        return response_type;
    }

    public void setResponse_type(String response_type) {
        this.response_type = response_type;
    }

    public Vector<SlackAttachment> getAttachments() {
        return attachments;
    }

    public void setAttachments(Vector<SlackAttachment> attachments) {
        this.attachments = attachments;
    }

    public boolean isHasIncludedAvailableCommand() {
        return hasIncludedAvailableCommand;
    }

    public void setHasIncludedAvailableCommand(boolean hasIncludedAvailableCommand) {
        this.hasIncludedAvailableCommand = hasIncludedAvailableCommand;
    }

    public static String getAvailableCommand() {
        return availableCommand;
    }
}
