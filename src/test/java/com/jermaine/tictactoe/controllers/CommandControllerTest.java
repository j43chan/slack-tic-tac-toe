package com.jermaine.tictactoe.controllers;

import com.jermaine.tictactoe.models.SlackRequest;
import com.jermaine.tictactoe.models.SlackResponse;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CommandControllerTest {
    @Mock
    ChallengeManager fakeChallengeManager;
    @Mock
    AcceptManager fakeAcceptManager;
    @Mock
    PlayManager fakePlayManager;
    @Mock
    DropChallengeManager fakeDropManager;

    @InjectMocks
    CommandController subject;
    SlackRequest fakeRequest;
    SlackResponse fakeResponse;



    @Before
    public void setUp(){
        fakeRequest = mock(SlackRequest.class);
        fakeResponse = mock(SlackResponse.class);
    }

    @Test
    public void tictactoe_Null_Slack_Token_Returns_Error() throws Exception{
        when(fakeRequest.getToken()).thenReturn(null);
        SlackResponse response = subject.tictactoe(fakeRequest);
        response.getText().equals("Your slack team is not supported! Please check your token!");
    }

    @Test
    public void tictactoe_Wrong_Slack_Token_Returns_Error() throws Exception{
        when(fakeRequest.getToken()).thenReturn("wrong_slack_token");
        SlackResponse response = subject.tictactoe(fakeRequest);
        response.getText().equals("Your slack team is not supported! Please check your token!");
    }

    @Test
    public void tictactoe_Null_Channel_Id_Returns_Error() throws Exception{
        when(fakeRequest.getToken()).thenReturn("KmL99uWHnNnIj8TwAkhFqc6B");
        when(fakeRequest.getChannel_id()).thenReturn(null);
        SlackResponse response = subject.tictactoe(fakeRequest);
        response.getText().equals("invalid channel id");
    }

    @Test
    public void tictactoe_Null_Command_Returns_Error() throws Exception{
        when(fakeRequest.getToken()).thenReturn("KmL99uWHnNnIj8TwAkhFqc6B");
        when(fakeRequest.getChannel_id()).thenReturn("fake_channel_id");
        when(fakeRequest.getText()).thenReturn(null);
        SlackResponse response = subject.tictactoe(fakeRequest);
        response.getText().equals("make sure you specify a command");
    }

    @Test
    public void tictactoe_Challenge_Command_Invokes_Challenge_Manager_And_Returns_Response() throws Exception{
        when(fakeRequest.getToken()).thenReturn("KmL99uWHnNnIj8TwAkhFqc6B");
        when(fakeRequest.getChannel_id()).thenReturn("fake_channel_id");
        when(fakeRequest.getText()).thenReturn("challenge fake_user");
        when(fakeChallengeManager.startService(any(),anyString(),any())).thenReturn(fakeResponse);
        SlackResponse response = subject.tictactoe(fakeRequest);
        verify(fakeChallengeManager).startService(any(), anyString(), any());
        assertSame(fakeResponse, response);
    }

    @Test
    public void tictactoe_Challenge_Command_Missing_User_Name_Returns_Error(){
        when(fakeRequest.getToken()).thenReturn("KmL99uWHnNnIj8TwAkhFqc6B");
        when(fakeRequest.getChannel_id()).thenReturn("fake_channel_id");
        when(fakeRequest.getText()).thenReturn("challenge");
        SlackResponse response = subject.tictactoe(fakeRequest);
        assertTrue(response.getText().equals("invalid command/parameters"));
        assertTrue(response.getAttachments().size() == 1);
        assertTrue(response.getAttachments().get(0).getText().equals(SlackResponse.availableCommand));
    }

    @Test
    public void tictactoe_Accept_Command_Invokes_Accept_Manager_And_Returns_Response() throws Exception{
        when(fakeRequest.getToken()).thenReturn("KmL99uWHnNnIj8TwAkhFqc6B");
        when(fakeRequest.getChannel_id()).thenReturn("fake_channel_id");
        when(fakeRequest.getText()).thenReturn("accept");
        when(fakeAcceptManager.startService(any(),any())).thenReturn(fakeResponse);
        SlackResponse response = subject.tictactoe(fakeRequest);
        verify(fakeAcceptManager).startService(any(), any());
        assertSame(fakeResponse, response);
    }

    @Test
    public void tictactoe_Play_Command_Invokes_Play_Manager_And_Returns_Response() throws Exception{
        when(fakeRequest.getToken()).thenReturn("KmL99uWHnNnIj8TwAkhFqc6B");
        when(fakeRequest.getChannel_id()).thenReturn("fake_channel_id");
        when(fakeRequest.getText()).thenReturn("play 1");
        when(fakePlayManager.startService(any(), anyString(),any())).thenReturn(fakeResponse);
        SlackResponse response = subject.tictactoe(fakeRequest);
        verify(fakePlayManager).startService(any(), anyString(),any());
        assertSame(fakeResponse, response);
    }

    @Test
    public void tictactoe_Drop_Command_Invokes_Drop_Manager_And_Returns_Response() throws Exception{
        when(fakeRequest.getToken()).thenReturn("KmL99uWHnNnIj8TwAkhFqc6B");
        when(fakeRequest.getChannel_id()).thenReturn("fake_channel_id");
        when(fakeRequest.getText()).thenReturn("drop");
        when(fakeDropManager.startService(any(),any())).thenReturn(fakeResponse);
        SlackResponse response = subject.tictactoe(fakeRequest);
        verify(fakeDropManager).startService(any(),any());
        assertSame(fakeResponse, response);
    }

    @Test
    public void tictactoe_Help_Command_Returns_Help_Commands() throws Exception{
        when(fakeRequest.getToken()).thenReturn("KmL99uWHnNnIj8TwAkhFqc6B");
        when(fakeRequest.getChannel_id()).thenReturn("fake_channel_id");
        when(fakeRequest.getText()).thenReturn("help");
        SlackResponse response = subject.tictactoe(fakeRequest);
        assertTrue(response.getText() == null);
        assertTrue(response.getAttachments().size() == 1);
        assertTrue(response.getAttachments().get(0).getText().equals(SlackResponse.availableCommand));
    }

    @Test
    public void tictactoe_Invalid_Command_Returns_Available_Commands() throws Exception{
        when(fakeRequest.getToken()).thenReturn("KmL99uWHnNnIj8TwAkhFqc6B");
        when(fakeRequest.getChannel_id()).thenReturn("fake_channel_id");
        when(fakeRequest.getText()).thenReturn("invalid_command");
        SlackResponse response = subject.tictactoe(fakeRequest);
        assertTrue(response.getText().equals("invalid command/parameters"));
        assertTrue(response.getAttachments().size() == 1);
        assertTrue(response.getAttachments().get(0).getText().equals(SlackResponse.availableCommand));
    }
}


