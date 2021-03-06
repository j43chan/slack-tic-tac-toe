package com.jermaine.tictactoe.models;

import java.util.Vector;

public class SlackResponse {
    protected String text;
    protected String response_type = "ephemeral";
    protected Vector<SlackAttachment> attachments = new Vector<>();
    protected boolean hasIncludedAvailableCommand = false;
    protected boolean hasIncludedPlayCommand = false;
    public final static String statusCommand = "/ttt status - shows board, and indicates turn information \n";
    public final static String playCommand = "/ttt play [slot_number] - numbers between (1 - 9) \n";
    public final static String availableCommand =  "```/ttt challenge [user_name] - issues a ttt challege \n" +
                                            "/ttt accept - accepts a ttt challeged \n" +
                                            playCommand +
                                            statusCommand +
                                            "/ttt drop - drops a game waiting to be accepted \n```";

    public SlackResponse changeResponseTypeToEphemeral(){
        response_type = "ephemeral";
        return this;
    }

    public SlackResponse changeResponseTypeToInChannel(){
        response_type = "in_channel";
        return this;
    }

    public SlackResponse includePlayCommand(){
        if(hasIncludedPlayCommand)
            return this;
        hasIncludedPlayCommand = true;
        return addAttachmentText(playCommand);
    }

    public SlackResponse includeAvailableCommands(){
        if( hasIncludedAvailableCommand )
            return this;
        hasIncludedAvailableCommand = true;
        return addAttachmentText( availableCommand );
    }

    public SlackResponse addAttachment(final String preText, final String attachmentText, final String imageUrl){
        SlackAttachment newAttachment = new SlackAttachment();
        newAttachment.setPretext(preText);
        newAttachment.setText(attachmentText);
        newAttachment.setImage_url(imageUrl);
        attachments.add(newAttachment);
        return this;
    }

    public SlackResponse addAttachmentText(final String attachmentText){
        return addAttachment(null, attachmentText, null);
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

    public void setHasIncludedAvailableCommand(boolean hasIncludedAvailableCommand) {
        this.hasIncludedAvailableCommand = hasIncludedAvailableCommand;
    }

    public static String getAvailableCommand() {
        return availableCommand;
    }
}
