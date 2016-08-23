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
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class PlayManagerTest {
    private PlayManager subject;
    private SlackRequest fakeRequest;
    private Map<String, GameRoom> fakeGameList;
    private GameRoom fakeGameRoom;
    @Before
    public void setUp(){
        fakeRequest = mock(SlackRequest.class);
        when(fakeRequest.getChannel_id()).thenReturn("fake_channel_id");
        fakeGameRoom = mock(GameRoom.class);
        fakeGameList = mock(HashMap.class);
        subject = new PlayManager();
    }

    @After
    public void tearDown(){

    }

    @Test
    public void startPlay_Invalid_Slack_Request_Throws_Invalid_Object_Exception(){
        try {
            subject.startPlay(null, "0", "0", fakeGameList);
            fail("exception not thrown");
        }catch (InvalidSlackRequest expectedException){
            assertTrue(expectedException.getMessage().equals("missing channel id"));
        }

        try{
            when(fakeRequest.getChannel_id()).thenReturn(null);
            subject.startPlay(fakeRequest, "0", "0", fakeGameList);
            fail("exception not thrown");
        }catch (InvalidSlackRequest expectedException){
            assertTrue(expectedException.getMessage().equals("missing channel id"));
        }
    }

    @Test
    public void startPlay_Game_Room_Does_Not_Exist() throws InvalidSlackRequest{
        when(fakeGameList.get(any())).thenReturn(null);
        SlackResponse response = subject.startPlay(fakeRequest, null, null, fakeGameList);
        assertTrue(response.getResponse_type().equals("ephemeral"));
        assertTrue(response.getText().equals("There is no game associated with this channel; please challenge someone to start a game."));
    }

    @Test
    public void startPlay_Game_Has_Not_Started_Returns_Error() throws InvalidSlackRequest{
        when(fakeGameList.get(any())).thenReturn(fakeGameRoom);
        when(fakeGameRoom.hasGameStarted()).thenReturn(false);
        SlackResponse response = subject.startPlay(fakeRequest, null, null, fakeGameList);
        assertTrue(response.getResponse_type().equals("ephemeral"));
        assertTrue(response.getText().equals("Game has not been accepted yet."));
    }

    @Test
    public void startPlay_Not_Your_Turn_Returns_Error() throws InvalidSlackRequest{
        when(fakeGameList.get(any())).thenReturn(fakeGameRoom);
        when(fakeGameRoom.hasGameStarted()).thenReturn(true);
        when(fakeGameRoom.getCurrentUserId()).thenReturn("current_user_id");
        when(fakeRequest.getUser_id()).thenReturn("another_user_id");

        SlackResponse response = subject.startPlay(fakeRequest, null, null, fakeGameList);
        assertTrue(response.getResponse_type().equals("ephemeral"));
        assertTrue(response.getText().equals("It is not your turn!"));
    }

    @Test
    public void startPlay_Invalid_Row_Col_Returns_Error() throws InvalidSlackRequest{
        when(fakeGameList.get(any())).thenReturn(fakeGameRoom);
        when(fakeGameRoom.hasGameStarted()).thenReturn(true);
        when(fakeGameRoom.getCurrentUserId()).thenReturn("same_user_id");
        when(fakeRequest.getUser_id()).thenReturn("same_user_id");

        SlackResponse response = subject.startPlay(fakeRequest, null, null, fakeGameList);
        assertTrue(response.getResponse_type().equals("ephemeral"));
        assertTrue(response.getText().equals("you must specify a valid row col for play command"));

        response = subject.startPlay(fakeRequest, "wrong type", "1", fakeGameList);
        assertTrue(response.getResponse_type().equals("ephemeral"));
        assertTrue(response.getText().equals("you must specify a valid row col for play command"));

        response = subject.startPlay(fakeRequest, "1", "wrong type", fakeGameList);
        assertTrue(response.getResponse_type().equals("ephemeral"));
        assertTrue(response.getText().equals("you must specify a valid row col for play command"));

        response = subject.startPlay(fakeRequest, "4", "2", fakeGameList);
        assertTrue(response.getResponse_type().equals("ephemeral"));
        assertTrue(response.getText().equals("you must specify a valid row col for play command"));

        response = subject.startPlay(fakeRequest, "0", "2", fakeGameList);
        assertTrue(response.getResponse_type().equals("ephemeral"));
        assertTrue(response.getText().equals("you must specify a valid row col for play command"));
    }

    @Test
    public void startPlay_Row_Col_On_A_Area_With_Existing_Piece() throws InvalidSlackRequest{
        when(fakeGameList.get(any())).thenReturn(fakeGameRoom);
        when(fakeGameRoom.hasGameStarted()).thenReturn(true);
        when(fakeGameRoom.getCurrentUserId()).thenReturn("same_user_id");
        when(fakeGameRoom.playTurn(anyInt(),anyInt())).thenReturn(false);
        when(fakeRequest.getUser_id()).thenReturn("same_user_id");

        SlackResponse response = subject.startPlay(fakeRequest, "1", "1", fakeGameList);
        assertTrue(response.getResponse_type().equals("ephemeral"));
        assertTrue(response.getText().equals("there is already a piece in that spot"));
    }

    @Test
    public void startPlay_Row_Col_Valid_Turn_Game_Not_Ended_Includes_Play_Command_List() throws InvalidSlackRequest{
        when(fakeGameList.get(any())).thenReturn(fakeGameRoom);
        when(fakeGameRoom.hasGameStarted()).thenReturn(true);
        when(fakeGameRoom.getCurrentUserId()).thenReturn("same_user_id");
        when(fakeGameRoom.playTurn(anyInt(),anyInt())).thenReturn(true);
        when(fakeGameRoom.getSlackRepresentationOfBoard()).thenReturn("representation_of_board");
        when(fakeGameRoom.hasGameEnded()).thenReturn(false);
        when(fakeGameRoom.getTurnInfo()).thenReturn("currentTurnInformation");
        when(fakeRequest.getUser_id()).thenReturn("same_user_id");

        SlackResponse response = subject.startPlay(fakeRequest, "1", "1", fakeGameList);
        assertTrue(response.getResponse_type().equals("in_channel"));
        assertTrue(response.getText().equals("representation_of_board"));
        assertTrue(response.getAttachments().get(0).getText().equals("currentTurnInformation"));
        assertTrue(response.getAttachments().get(1).getText().equals("/ttt play [row] [col] - numbers between (1 - 3) \n"));
    }

    @Test
    public void startPlay_Row_Col_Valid_Turn_Game_Ended_Remove_Game_Room() throws InvalidSlackRequest{
        when(fakeGameList.get(any())).thenReturn(fakeGameRoom);
        when(fakeGameRoom.hasGameStarted()).thenReturn(true);
        when(fakeGameRoom.getCurrentUserId()).thenReturn("same_user_id");
        when(fakeGameRoom.playTurn(anyInt(),anyInt())).thenReturn(true);
        when(fakeGameRoom.getSlackRepresentationOfBoard()).thenReturn("representation_of_board");
        when(fakeGameRoom.hasGameEnded()).thenReturn(true);
        when(fakeGameRoom.getTurnInfo()).thenReturn("currentTurnInformation");
        when(fakeRequest.getUser_id()).thenReturn("same_user_id");

        SlackResponse response = subject.startPlay(fakeRequest, "1", "1", fakeGameList);
        verify(fakeGameList).remove(fakeRequest.getChannel_id());
        assertTrue(response.getResponse_type().equals("in_channel"));
        assertTrue(response.getText().equals("representation_of_board"));
        assertTrue(response.getAttachments().get(0).getText().equals("currentTurnInformation"));
    }

}

