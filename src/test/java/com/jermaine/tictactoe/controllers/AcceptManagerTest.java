package com.jermaine.tictactoe.controllers;

import com.jermaine.tictactoe.models.SlackRequest;
import com.jermaine.tictactoe.models.SlackResponse;
import com.jermaine.tictactoe.models.TicTacToe;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AcceptManagerTest {
    private AcceptManager subject;
    private SlackRequest fakeRequest;
    private Map<String, TicTacToe> fakeGameList;
    private TicTacToe fakeGameRoom;
    @Before
    public void setUp(){
        fakeRequest = mock(SlackRequest.class);
        when(fakeRequest.getChannel_id()).thenReturn("fake_channel_id");
        fakeGameRoom = mock(TicTacToe.class);
        fakeGameList = mock(HashMap.class);
        subject = new AcceptManager();
    }

    @After
    public void tearDown(){

    }

    @Test
    public void accept_Game_Room_Does_Not_Exist_Returns_Error(){
        when(fakeGameList.get(anyString())).thenReturn(null);
        SlackResponse response = subject.accept( fakeRequest, fakeGameList );
        assertTrue(response.getResponse_type().equals("ephemeral"));
        assertTrue(response.getText().equals("you must be challenged before accepting a request"));
    }

    @Test
    public void accept_Game_Room_Already_Started_Returns_Error(){
        when(fakeGameList.get(anyString())).thenReturn(fakeGameRoom);
        when(fakeGameRoom.hasGameStarted()).thenReturn(true);
        SlackResponse response = subject.accept(fakeRequest, fakeGameList);
        assertTrue(response.getResponse_type().equals("ephemeral"));
        assertTrue(response.getText().equals("Game is already in progress"));
    }

    @Test
    public void accept_Challenge_With_Wrong_User_Returns_Error(){
        when(fakeGameList.get(anyString())).thenReturn(fakeGameRoom);
        when(fakeGameRoom.hasGameStarted()).thenReturn(false);
        when(fakeGameRoom.getPlayer2Name()).thenReturn("another_user_name");
        when(fakeRequest.getUser_name()).thenReturn("yet_another_user_name");

        SlackResponse response = subject.accept(fakeRequest, fakeGameList);
        assertTrue(response.getResponse_type().equals("ephemeral"));
        assertTrue(response.getText().equals("You are not the one being challenged"));
    }

    @Test
    public void accept_Challenge_With_Correct_User_Returns_Calls_Start_Game(){
        when(fakeGameList.get(anyString())).thenReturn(fakeGameRoom);
        when(fakeGameRoom.hasGameStarted()).thenReturn(false);
        when(fakeGameRoom.getPlayer2Name()).thenReturn("correct_user");
        when(fakeGameRoom.getSlackRepresentationOfBoard()).thenReturn("a_representation_of_board");
        when(fakeGameRoom.currentTurnInformation()).thenReturn("current_turn_information");
        when(fakeRequest.getUser_name()).thenReturn("correct_user");
        when(fakeRequest.getUser_id()).thenReturn("fake_user_id");

        SlackResponse response = subject.accept(fakeRequest, fakeGameList);
        verify(fakeGameRoom).setPlayer2UserId(fakeRequest.getUser_id());
        verify(fakeGameRoom).startGame();
        assertTrue(response.getResponse_type().equals("in_channel"));
        assertTrue(response.getText().equals("a_representation_of_board"));
        assertTrue(response.getAttachments().size() == 2 );
        assertTrue(response.getAttachments().get(0).getText().equals("current_turn_information"));
        assertTrue(response.getAttachments().get(1).getText().equals("/ttt play [row] [col] - numbers between (1 - 3) \n"));

    }
}

