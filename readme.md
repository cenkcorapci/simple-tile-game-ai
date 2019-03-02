[![Build Status](https://travis-ci.org/cenkcorapci/simple-tile-game-ai.svg?branch=master)](https://travis-ci.org/cenkcorapci/simple-tile-game-ai)
[![Coverage Status](https://coveralls.io/repos/github/cenkcorapci/simple-tile-game-ai/badge.svg)](https://coveralls.io/github/cenkcorapci/simple-tile-game-ai)

simple-tile-game-ai
===================
Play a tile game against a minimax algorithm with alpha-beta pruning.

## The game
- There is a 7x7 board(Can be set to a custom size), a parametric turn limit and a parametrical number of pieces for each player. Initial
position of these pieces must be random.On each turn current player makes a single move:Moving his piece to one of its four neighbours.
which are either horizontal or vertical.

```
---Move left:2 ----
(35,21)
$  | 1  | 2  | 3  | 4  | 5  | 6  | 7
------------------------------------
a  |    | O  | O  |    |    |    |  
------------------------------------
b  |    | X  |    |    | X  |    |  
------------------------------------
c  |    |    | O  |    |    |    |  
------------------------------------
d  |    |    |    |    |    |    |  
------------------------------------
e  |    | X  |    |    |    | X  |  
------------------------------------
f  | O  |    |    | X  |    |    | O
------------------------------------
g  |    |    |    |    |    |    |  
()

---Move left:1 ----
(35,22)
$  | 1  | 2  | 3  | 4  | 5  | 6  | 7
------------------------------------
a  |    | O  |    |    |    |    |  
------------------------------------
b  |    | X  | O  |    | X  |    |  
------------------------------------
c  |    |    | O  |    |    |    |  
------------------------------------
d  |    |    |    |    |    |    |  
------------------------------------
e  |    | X  |    |    |    | X  |  
------------------------------------
f  | O  |    |    | X  |    |    | O
------------------------------------
g  |    |    |    |    |    |    |  
()

---Move left:0 ----
(35,22)
$  | 1  | 2  | 3  | 4  | 5  | 6  | 7
------------------------------------
a  |    | O  |    |    |    |    |  
------------------------------------
b  |    | X  | O  |    | X  |    |  
------------------------------------
c  |    |    | O  |    |    |    |  
------------------------------------
d  |    |    |    |    |    |    |  
------------------------------------
e  |    | X  |    | X  |    | X  |  
------------------------------------
f  | O  |    |    |    |    |    | O
------------------------------------
g  |    |    |    |    |    |    |  
()

```
### Winning
- The game ends when one of the players can not make a valid move or the turn limit has been reached.
- When one of the players can not make a valid move, the game ends and the other player will be the
  winner.
- When the turn limit is reached, the game ends and the player with more valid moveable space will be the
  winner.
## Usage
- Use `sbt run` to start.
```
> sbt run 
 _______ __        ______                   
 /_  __(_) /__     / ____/___ _____ ___  ___ 
  / / / / / _ \   / / __/ __ `/ __ `__ \/ _ \
 / / / / /  __/  / /_/ / /_/ / / / / / /  __/
/_/ /_/_/\___/   \____/\__,_/_/ /_/ /_/\___/ 
                                             

- Type help for commands
- Type quit for quitting the application

```
- Other than that, this project has unit tests that covers a random 5 round play between a dummy player and ai.
Use `sbt clean coverage test coverageReport` to run tests with coverage.
- use `sbt assembly` to assemble a fat jar.
