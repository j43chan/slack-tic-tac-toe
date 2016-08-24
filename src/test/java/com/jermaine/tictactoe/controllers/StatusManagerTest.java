package com.jermaine.tictactoe.controllers;

import com.jermaine.tictactoe.exceptions.InvalidSlackRequest;
import com.jermaine.tictactoe.models.SlackRequest;
import com.jermaine.tictactoe.models.SlackResponse;
import com.jermaine.tictactoe.models.GameRoom;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class StatusManagerTest {
    private StatusManager subject;
    private SlackRequest fakeRequest;
    private Map<String, GameRoom> fakeGameList;
    private GameRoom fakeGameRoom;
    @Before
    public void setUp(){
        fakeRequest = mock(SlackRequest.class);
        when(fakeRequest.getChannel_id()).thenReturn("fake_channel_id");
        fakeGameRoom = mock(GameRoom.class);
        fakeGameList = mock(HashMap.class);
        subject = new StatusManager();
    }

    @After
    public void tearDown(){

    }

    @Test
    public void startService_Invalid_Slack_Request_Throws_Invalid_Object_Exception(){
        try {
            subject.startService(null, fakeGameList);
            fail("exception not thrown");
        }catch (InvalidSlackRequest expectedException){
            assertTrue(expectedException.getMessage().equals("missing channel id"));
        }

        try{
            when(fakeRequest.getChannel_id()).thenReturn(null);
            subject.startService(fakeRequest, fakeGameList);
            fail("exception not thrown");
        }catch (InvalidSlackRequest expectedException){
            assertTrue(expectedException.getMessage().equals("missing channel id"));
        }
    }

    @Test
    public void startService_Game_Room_Does_Not_Exist() throws InvalidSlackRequest{
        when(fakeGameList.get(any())).thenReturn(null);
        SlackResponse response = subject.startService(fakeRequest, fakeGameList);
        assertTrue(response.getResponse_type().equals("ephemeral"));
        assertTrue(response.getText().equals("There is no game associated with this channel; please challenge someone to start a game."));
    }

    @Test
    public void startService_Game_Has_Not_Started_Returns_Error() throws InvalidSlackRequest{
        when(fakeGameList.get(any())).thenReturn(fakeGameRoom);
        when(fakeGameRoom.hasGameStarted()).thenReturn(false);
        SlackResponse response = subject.startService(fakeRequest, fakeGameList);
        assertTrue(response.getResponse_type().equals("ephemeral"));
        assertTrue(response.getText().equals("Game has not been accepted yet."));
    }

    @Test
    public void startService_Game_Has_Started_Returns_Board_And_Turn_Information() throws InvalidSlackRequest{
        when(fakeGameList.get(any())).thenReturn(fakeGameRoom);
        when(fakeGameRoom.hasGameStarted()).thenReturn(true);
        when(fakeGameRoom.getSlackRepresentationOfBoard()).thenReturn("a_representation_of_board");
        when(fakeGameRoom.getTurnInfo()).thenReturn("turn_information");
        SlackResponse response = subject.startService(fakeRequest, fakeGameList);
        assertTrue(response.getResponse_type().equals("in_channel"));
        assertTrue(response.getText().equals("a_representation_of_board"));
        assertTrue(response.getAttachments().size() == 1 );
        assertTrue(response.getAttachments().get(0).getText().equals("turn_information"));
    }


}

