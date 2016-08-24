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

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ChallengeManagerTest {
    private ChallengeManager subject;
    private SlackRequest fakeRequest;
    private Map<String, GameRoom> fakeGameList;

    @Before
    public void setUp(){
        fakeRequest = mock(SlackRequest.class);
        when(fakeRequest.getChannel_id()).thenReturn("fake_channel_id");
        fakeGameList = spy(HashMap.class);
        subject = new ChallengeManager();
    }

    @After
    public void tearDown(){

    }

    @Test
    public void startChallenge_Invalid_Slack_Request_Throws_Invalid_Object_Exception(){
        try {
            subject.startService(null, "fake_user", fakeGameList);
            fail("exception not thrown");
        }catch (InvalidSlackRequest expectedException){
            assertTrue(expectedException.getMessage().equals("missing channel id"));
        }

        try{
            when(fakeRequest.getChannel_id()).thenReturn(null);
            subject.startService(fakeRequest, "fake_user", fakeGameList);
            fail("exception not thrown");
        }catch (InvalidSlackRequest expectedException){
            assertTrue(expectedException.getMessage().equals("missing channel id"));
        }
    }

    @Test
    public void startChallenge_When_Channel_Already_Has_Game_Room_Returns_Already_Exists() throws InvalidSlackRequest{
        when(fakeGameList.containsKey(anyString())).thenReturn(true);
        SlackResponse response = subject.startService(fakeRequest, "fake_user", fakeGameList);
        assertTrue(response.getResponse_type().equals("ephemeral"));
        assertTrue(response.getText().equals("Challenged already issued, or a game is in progress; please wait for the game to startService, or finish game before reissuing another challenge."));
    }

    @Test
    public void startChallenge_When_Channel_Does_Not_Exist_Issue_New_Challenge() throws InvalidSlackRequest{
        when(fakeGameList.containsKey(anyString())).thenReturn(false);
        when(fakeRequest.getUser_name()).thenReturn("user_1");
        when(fakeRequest.getUser_id()).thenReturn("user_1_id");
        SlackResponse response = subject.startService(fakeRequest, "challenged_user", fakeGameList);
        assertTrue(fakeGameList.size() == 1);
        assertTrue(fakeGameList.get(fakeRequest.getChannel_id()).getPlayer1Name().equals("user_1"));
        assertTrue(fakeGameList.get(fakeRequest.getChannel_id()).getPlayer1UserId().equals("user_1_id"));
        assertTrue(fakeGameList.get(fakeRequest.getChannel_id()).getPlayer2Name().equals("challenged_user"));
        assertTrue(response.getResponse_type().equals("in_channel"));
        assertTrue(response.getText().equals("user_1 has issued a ttt challenge to challenged_user\n<@challenged_user> type ```/ttt startService``` to startService the game!"));
    }

    @Test
    public void startChallenge_2_Request_Different_Channel_Id_Creates_2_Game_Rooms(){

    }
}


