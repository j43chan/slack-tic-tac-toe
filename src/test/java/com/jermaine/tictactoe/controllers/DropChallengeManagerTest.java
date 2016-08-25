package com.jermaine.tictactoe.controllers;

import com.jermaine.tictactoe.exceptions.InvalidSlackRequest;
import com.jermaine.tictactoe.models.SlackRequest;
import com.jermaine.tictactoe.models.SlackResponse;
import com.jermaine.tictactoe.models.GameRoom;
import com.jermaine.tictactoe.services.DropChallengeManager;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class DropChallengeManagerTest {
    private DropChallengeManager subject;
    private SlackRequest fakeRequest;
    private Map<String, GameRoom> fakeGameList;
    private GameRoom fakeGameRoom;
    @Before
    public void setUp(){
        fakeRequest = mock(SlackRequest.class);
        when(fakeRequest.getChannel_id()).thenReturn("fake_channel_id");
        fakeGameRoom = mock(GameRoom.class);
        fakeGameList = mock(HashMap.class);
        subject = new DropChallengeManager();
    }

    @After
    public void tearDown(){

    }

    @Test
    public void start_Invalid_Slack_Request_Throws_Invalid_Object_Exception(){
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
    public void start_When_Game_Room_Does_Not_Exist_Returns_Error() throws InvalidSlackRequest{
        when(fakeGameList.get(anyString())).thenReturn(null);
        SlackResponse response = subject.startService(fakeRequest, fakeGameList);
        assertTrue(response.getResponse_type().equals("ephemeral"));
        assertTrue(response.getText().equals("the current channel has no game in progress"));
    }

    @Test
    public void start_When_Game_Room_Is_Not_In_Waiting_To_Be_Accepted_State_Returns_Error() throws InvalidSlackRequest{
        when(fakeGameList.get(anyString())).thenReturn(fakeGameRoom);
        when(fakeGameList.containsKey(anyString())).thenReturn(true);
        when(fakeGameRoom.isWaitingToBeAccepted()).thenReturn(false);
        SlackResponse response = subject.startService(fakeRequest, fakeGameList);
        assertTrue(response.getResponse_type().equals("ephemeral"));
        assertTrue(response.getText().equals("Cannot Drop a Game that is in progress"));
    }

    @Test
    public void start_When_Game_Room_Is_In_Waiting_State_And_Wrong_User_Drops_Returns_Error() throws  InvalidSlackRequest{
        when(fakeGameList.get(anyString())).thenReturn(fakeGameRoom);
        when(fakeGameList.containsKey(anyString())).thenReturn(true);
        when(fakeGameRoom.isWaitingToBeAccepted()).thenReturn(true);
        when(fakeGameRoom.getPlayer1Name()).thenReturn("fake_name");
        when(fakeRequest.getUser_name()).thenReturn("a_different_fake_name");
        SlackResponse response = subject.startService(fakeRequest, fakeGameList);
        assertTrue(response.getResponse_type().equals("ephemeral"));
        assertTrue(response.getText().equals("cannot drop a game that you didn't create"));
    }

    @Test
    public void start_When_Dropping_Game_You_Created_In_Waiting_State_Returns_Success() throws InvalidSlackRequest{
        when(fakeGameList.get(anyString())).thenReturn(fakeGameRoom);
        when(fakeGameList.containsKey(anyString())).thenReturn(true);
        when(fakeGameRoom.isWaitingToBeAccepted()).thenReturn(true);
        when(fakeGameRoom.getPlayer1Name()).thenReturn("same_fake_name");
        when(fakeRequest.getUser_name()).thenReturn("same_fake_name");
        SlackResponse response = subject.startService(fakeRequest, fakeGameList);
        verify(fakeGameList).remove(fakeRequest.getChannel_id());
        assertTrue(response.getResponse_type().equals("in_channel"));
        assertTrue(response.getText().equals("game has been dropped"));
    }
}

