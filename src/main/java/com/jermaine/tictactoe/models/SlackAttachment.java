package com.jermaine.tictactoe.models;

import java.util.Vector;

public class SlackAttachment {
    String text = null;

    Vector<SlackMsgButton> actions = new Vector<>();

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setButtons(final SlackMsgButton button){
        actions.add(button);
    }

    public Vector<SlackMsgButton> getActions() {
        return actions;
    }
}
