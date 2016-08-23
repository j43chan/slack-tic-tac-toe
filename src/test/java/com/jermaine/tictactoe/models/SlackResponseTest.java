package com.jermaine.tictactoe.models;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class SlackResponseTest {
    SlackResponse subject;
    @Before
    public void setUp(){
        subject = new SlackResponse();
    }

    @Test
    public void changeResponseTypeToEphemeral(){
        subject.changeResponseTypeToEphemeral();
        assertTrue(subject.response_type.equals("ephemeral"));
    }

    @Test
    public void changeResponseTypeToInChannel(){
        subject.changeResponseTypeToInChannel();
        assertTrue(subject.response_type.equals("in_channel"));
    }

    @Test
    public void includePlayCommand_When_Has_Been_Set_Text_Is_Not_Repeated(){
        subject.includePlayCommand();
        subject.includePlayCommand();
        assertTrue(subject.getAttachments().size() == 1);
        assertTrue(subject.getAttachments().get(0).getText().equals(SlackResponse.playCommand));
    }

    @Test
    public void includePlayCommand(){
        subject.includePlayCommand();
        assertTrue(subject.getAttachments().size() == 1);
        assertTrue(subject.getAttachments().get(0).getText().equals(SlackResponse.playCommand));
    }

    @Test
    public void includeAvailableCommands_When_Has_Been_Set_Text_Is_Not_Repeated(){
        subject.includeAvailableCommands();
        subject.includeAvailableCommands();
        assertTrue(subject.getAttachments().size() == 1);
        assertTrue(subject.getAttachments().get(0).getText().equals(SlackResponse.availableCommand));
    }

    @Test
    public void includeAvailableCommands(){
        subject.includeAvailableCommands();
        assertTrue(subject.getAttachments().size() == 1);
        assertTrue(subject.getAttachments().get(0).getText().equals(SlackResponse.availableCommand));
    }

    @Test
    public void addAttachmentText(){
        subject.addAttachmentText("pretext","text");
        assertTrue(subject.getAttachments().size() == 1);
        assertTrue(subject.getAttachments().get(0).getPretext().equals("pretext"));
        assertTrue(subject.getAttachments().get(0).getText().equals("text"));
    }

}
