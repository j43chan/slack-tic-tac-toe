package com.jermaine.tictactoe.models;

public class SlackAttachment {
    String text = null;
    String pretext = null;
    String[] mrkdwn_in = {"text", "pretext"};

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getPretext() {
        return pretext;
    }

    public void setPretext(String pretext) {
        this.pretext = pretext;
    }

    public String[] getMrkdwn_in() {
        return mrkdwn_in;
    }
}
