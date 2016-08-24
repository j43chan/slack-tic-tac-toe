# slack-tic-tac-toe

commands
```
/ttt challenge [user_name] - issues a ttt challege
/ttt accept - accepts a ttt challeged
/ttt play [slot_number] - numbers between (1 - 9)
/ttt drop - drops a game waiting to be accepted
/ttt status - shows board, and indicates turn information
```

#How To Play

1) Issue a challenge to any user in the channel e.g `/ttt challenge jermaine`
2) The person being challenged, in this case jermaine, will need to accept the challenge by typing in `/ttt accept`
3) Board will now be displayed along with turn information

```
| 1 | 2 | 3 |
|---+---+---|
| 4 | 5 | 6 |
|---+---+---|
| 7 | 8 | 9 |
```

4) The board is mapped into 9 different sections, the user whose turn it is can now play by specifying
  which zone they want to put their piece in.  For example to put a piece down on the most top left corner of the
  board, use `/ttt play 1`

5) The game continues until the game is a draw or someone wins.

#Note
```
you can accidentally challenge a non existent user, or the other player simply doesn't want to accept the challenge.
in this case you can use `/ttt drop` to end the challenge.

only games waiting for challenge accept can be dropped.  once the game starts, you need to either win or draw to end the game
```

```
Each channel can at most support 1 game at a time
```


