package com.jermaine.tictactoe.models;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class GameRoomTest {
    GameRoom subject;
    int boardSetupWithX[][] = {{1,1,1},{1,1,1},{1,1,1}};
    int boardSetupWithO[][] = {{-1,-1,-1},{-1,-1,-1},{-1,-1,-1}};
    int boardSetupMix[][] = {{1,-1,4},{1,-1,7},{1,-1,10}};
    @Before
    public void setUp(){
        subject = spy(new GameRoom());
        subject.player1Name = "player_1";
        subject.player2Name = "player_2";
    }

    @Test
    public void getSlackRepresentationOfBoard(){
        assertTrue(subject.getSlackRepresentationOfBoard().equals("```| 1 | 2 | 3 |\n|---+---+---|\n| 4 | 5 | 6 |\n|---+---+---|\n| 7 | 8 | 9 |```"));


        subject.board = boardSetupWithX;
        assertTrue(subject.getSlackRepresentationOfBoard().equals("```| X | X | X |\n|---+---+---|\n| X | X | X |\n|---+---+---|\n| X | X | X |```"));


        subject.board = boardSetupWithO;
        assertTrue(subject.getSlackRepresentationOfBoard().equals("```| O | O | O |\n|---+---+---|\n| O | O | O |\n|---+---+---|\n| O | O | O |```"));
    }

    @Test
    public void getTokenAsCharFromBoardPosition(){
        subject.board = boardSetupMix;
        assertTrue(subject.getTokenAsCharFromBoardPosition(0,0) == 'X');
        assertTrue(subject.getTokenAsCharFromBoardPosition(0,1) == 'O');
        assertTrue(subject.getTokenAsCharFromBoardPosition(0,2) == '3');
    }

    @Test
    public void getTurnInfo_When_Player1_Turn_And_Win_State_Return_Player1_Wins_Message(){
        subject.currentPlayerTurn = 1;
        subject.gameState = GameRoom.GAME_STATE.WIN;
        String reply = subject.getTurnInfo();
        assertTrue(reply.equals("Game has ended player_1 wins!"));
    }

    @Test
    public void getTurnInfo_When_Player1_Turn_And_Draw_State_Return_Draw_Game_Message(){
        subject.currentPlayerTurn = 1;
        subject.gameState = GameRoom.GAME_STATE.DRAW;
        String reply = subject.getTurnInfo();
        assertTrue(reply.equals("Game has ended in a draw"));
    }

    @Test
    public void getTurnInfo_When_Player1_Turn_And_Started_State_Return_Player_1_Turn_Message(){
        subject.currentPlayerTurn = 1;
        subject.gameState = GameRoom.GAME_STATE.STARTED;
        String reply = subject.getTurnInfo();
        assertTrue(reply.equals("<@player_1> it is your turn to play"));
    }

    @Test
    public void getTurnInfo_When_Player2_Turn_And_Win_State_Return_Player2_Wins_Message(){
        subject.currentPlayerTurn = 2;
        subject.gameState = GameRoom.GAME_STATE.WIN;
        String reply = subject.getTurnInfo();
        assertTrue(reply.equals("Game has ended player_2 wins!"));
    }

    @Test
    public void getTurnInfo_When_Player2_Turn_And_Draw_State_Return_Draw_Game_Message(){
        subject.currentPlayerTurn = 2;
        subject.gameState = GameRoom.GAME_STATE.DRAW;
        String reply = subject.getTurnInfo();
        assertTrue(reply.equals("Game has ended in a draw"));
    }

    @Test
    public void getTurnInfo_When_Player2_Turn_And_Started_State_Return_Player_2_Turn_Message(){
        subject.currentPlayerTurn = 2;
        subject.gameState = GameRoom.GAME_STATE.STARTED;
        String reply = subject.getTurnInfo();
        assertTrue(reply.equals("<@player_2> it is your turn to play"));
    }

    @Test
    public void getNextToken_When_Current_Token_Is_1_Return_Negative_1(){
        subject.currentToken = 1;
        assertTrue(subject.getNextToken() == -1 );
    }

    @Test
    public void getNextToken_When_Current_Token_Is_Negative_1_Return_1(){
        subject.currentToken = -1;
        assertTrue(subject.getNextToken() == 1 );
    }

    @Test
    public void playTurn_When_Piece_Already_Exist_Returns_False(){
        subject.board = boardSetupMix;
        assertTrue(subject.playTurn( 0, 0 ) == false);
    }

    @Test
    public void playTurn_When_Empty_Spot_Wins_Game_And_Returns_True(){
        subject.gameState = GameRoom.GAME_STATE.STARTED;
        when(subject.checkWin(anyInt(),anyInt(),anyInt())).thenReturn(true);
        assertTrue(subject.playTurn(0,0));
        assertTrue(subject.gameState == GameRoom.GAME_STATE.WIN);
    }

    @Test
    public void playTurn_When_Empty_Spot_Draws_Game_And_Returns_True(){
        subject.gameState = GameRoom.GAME_STATE.STARTED;
        when(subject.checkWin(anyInt(),anyInt(),anyInt())).thenReturn(false);
        when(subject.checkDraw()).thenReturn(true);
        assertTrue(subject.playTurn(0,0));
        assertTrue(subject.gameState == GameRoom.GAME_STATE.DRAW);
    }

    @Test
    public void playTurn_When_Empty_Spot_Calls_Change_Turn_And_Returns_True(){
        subject.gameState = GameRoom.GAME_STATE.STARTED;
        when(subject.checkWin(anyInt(),anyInt(),anyInt())).thenReturn(false);
        when(subject.checkDraw()).thenReturn(false);
        assertTrue(subject.playTurn(0,0));
        verify(subject).changeTurn();
        assertTrue(subject.gameState == GameRoom.GAME_STATE.STARTED);
    }

    @Test
    public void checkDraw_Turns_Remaining_Greater_Then_Zero_Returns_False(){
        subject.turnsRemaining = 1;
        assertFalse(subject.checkDraw());
    }

    @Test
    public void checkDraw_Turns_Remaining_Zero_Returns_True(){
        subject.turnsRemaining = 0;
        assertTrue(subject.checkDraw());
    }

    @Test
    public void checkWin_Vertical_Win_For_Col1_X(){
        assertFalse(subject.checkWin(0,0,1));
        assertFalse(subject.checkWin(1,0,1));
        assertTrue(subject.checkWin(2,0,1));
    }

    @Test
    public void checkWin_Vertical_Win_For_Col2_X(){
        assertFalse(subject.checkWin(0,1,1));
        assertFalse(subject.checkWin(1,1,1));
        assertTrue(subject.checkWin(2,1,1));
    }

    @Test
    public void checkWin_Vertical_Win_For_Col3_X(){
        assertFalse(subject.checkWin(0,2,1));
        assertFalse(subject.checkWin(1,2,1));
        assertTrue(subject.checkWin(2,2,1));
    }

    @Test
    public void checkWin_Vertical_Win_For_Col1_O(){
        assertFalse(subject.checkWin(0,0,-1));
        assertFalse(subject.checkWin(1,0,-1));
        assertTrue(subject.checkWin(2,0,-1));
    }

    @Test
    public void checkWin_Vertical_Win_For_Col2_O(){
        assertFalse(subject.checkWin(0,1,-1));
        assertFalse(subject.checkWin(1,1,-1));
        assertTrue(subject.checkWin(2,1,-1));
    }

    @Test
    public void checkWin_Vertical_Win_For_Col3_O(){
        assertFalse(subject.checkWin(0,2,-1));
        assertFalse(subject.checkWin(1,2,-1));
        assertTrue(subject.checkWin(2,2,-1));
    }

    @Test
    public void checkWin_Horizontal_Win_For_Row1_X(){
        assertFalse(subject.checkWin(0,0,1));
        assertFalse(subject.checkWin(0,1,1));
        assertTrue(subject.checkWin(0,2,1));
    }

    @Test
    public void checkWin_Horizontal_Win_For_Row2_X(){
        assertFalse(subject.checkWin(1,0,1));
        assertFalse(subject.checkWin(1,1,1));
        assertTrue(subject.checkWin(1,2,1));
    }

    @Test
    public void checkWin_Horizontal_Win_For_Row3_X(){
        assertFalse(subject.checkWin(2,0,1));
        assertFalse(subject.checkWin(2,1,1));
        assertTrue(subject.checkWin(2,2,1));
    }

    @Test
    public void checkWin_Horizontal_Win_For_Row1_O(){
        assertFalse(subject.checkWin(0,0,-1));
        assertFalse(subject.checkWin(0,1,-1));
        assertTrue(subject.checkWin(0,2,-1));
    }

    @Test
    public void checkWin_Horizontal_Win_For_Row2_O(){
        assertFalse(subject.checkWin(1,0,-1));
        assertFalse(subject.checkWin(1,1,-1));
        assertTrue(subject.checkWin(1,2,-1));
    }

    @Test
    public void checkWin_Horizontal_Win_For_Row3_O(){
        assertFalse(subject.checkWin(2,0,-1));
        assertFalse(subject.checkWin(2,1,-1));
        assertTrue(subject.checkWin(2,2,-1));
    }

    @Test
    public void checkWin_Diagonal_For_X(){
        assertFalse(subject.checkWin(0,0,1));
        assertFalse(subject.checkWin(1,1,1));
        assertTrue(subject.checkWin(2,2,1));
    }

    @Test
    public void checkWin_Diagonal_For_O(){
        assertFalse(subject.checkWin(0,0,-1));
        assertFalse(subject.checkWin(1,1,-1));
        assertTrue(subject.checkWin(2,2,-1));
    }

    @Test
    public void checkWin_Reverse_Diag_For_X(){
        assertFalse(subject.checkWin(0,2,1));
        assertFalse(subject.checkWin(1,1,1));
        assertTrue(subject.checkWin(2,0,1));
    }

    @Test
    public void checkWin_Reverse_Diag_For_O(){
        assertFalse(subject.checkWin(0,2,-1));
        assertFalse(subject.checkWin(1,1,-1));
        assertTrue(subject.checkWin(2,0,-1));
    }

    @Test
    public void checkWin_Empty_Board_Returns_False(){
        assertFalse(subject.checkWin(0,0,0));
    }

    @Test
    public void checkWin_Invalid_Input_Returns_False(){
        assertFalse(subject.checkWin(-1, 0, 1));
        assertFalse(subject.checkWin(0, -1, 1));
        assertFalse(subject.checkWin(1, 0, 2));
        assertFalse(subject.checkWin(3, 0, 1));
        assertFalse(subject.checkWin(0, 3, 1));
    }

    @Test
    public void changeTurn_When_Game_Not_Started_Current_Player_Turn_Negative_1(){
        subject.gameState = GameRoom.GAME_STATE.CREATED;
        subject.changeTurn();
        assertTrue(subject.currentPlayerTurn == -1);
    }

    @Test
    public void changeTurn_When_Game_Started_Swap_Player_Turns(){
        subject.gameState = GameRoom.GAME_STATE.STARTED;
        subject.currentPlayerTurn = 1;
        subject.changeTurn();
        assertTrue(subject.currentPlayerTurn == 2);
        subject.changeTurn();
        assertTrue(subject.currentPlayerTurn == 1);
    }

    @Test
    public void startGame_When_Current_State_Is_Created_Change_State_To_Started(){
        subject.gameState = GameRoom.GAME_STATE.CREATED;
        subject.startGame();
        assertTrue(subject.gameState == GameRoom.GAME_STATE.STARTED);
    }

    @Test
    public void startGame_When_Current_State_Is_End_Game_Does_A_No_Op(){
        subject.gameState = GameRoom.GAME_STATE.WIN;
        subject.startGame();
        assertTrue(subject.gameState == GameRoom.GAME_STATE.WIN);
        subject.gameState = GameRoom.GAME_STATE.DRAW;
        subject.startGame();
        assertTrue(subject.gameState == GameRoom.GAME_STATE.DRAW);
    }



}

