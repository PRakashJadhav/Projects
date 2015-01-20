Cops and Robbers README
@Zach Janice

// As the largest independent project of mine, this project
// is still in development.

This Unity project is a game I am currently working on, called
"Cops and Robbers". The game is a direct adaptation of a
game my friends and I would play in private lobbies in
Call of Duty, and the game itself is a variation of the
childhood game of the same title. The overall premise
of the game is that the cops are trying to capture roaming
robbers and escort them to jail. The robbers' goal is to
kill the cops and avoid being captured, since becoming
captured and jailed will make winning the round much harder
for the robbers. In the event robbers are confronted by cops,
they can try to run away or even kill the cop, though doing
so makes them "wanted" and cops, as a result, have 
jurisdiction to then kill the robbers. In summary, both sides
are trying to eliminate the other, and, whereas the robbers
are able to kill the cops, the cops can kill wanted robbers
but otherwise essentially eliminate robbers by jailing them.

The game itself is a multiplayer first-person shooter. Players
play continuous rounds, where a round starts with the players
(re)spawning and ends with the elimination of one or both
teams. Different sets of equipment are available to the cops
and robbers to help them achieve their goals. The game is
controlled by an abstract class that is able to be implemented
to form varying different game modes if more need to be added
later. The multiplayer capability is achieved using Photon
Unity Network, a free asset. Players interact and relay
information to each other using remote procedure calls (RPC).

This project started as an experiment in improving my comfort 
with Unity and tackling multiplayer aspects, but after making
good progress I intend to continue working on it.

<TODO>
As it stands, the core ideas of the game and its management are
constructed and implemented. The next steps are to finish equipment
functionality and work on player representations across clients
(in other words, to work on being able to see where the other
player is looking, what equipment they currently wield, in what
state that equipment is, etc.). I recently gained the help of two
friends, one who will help me program the before-mentioned
improvements and another who is working on 3D assets to give the
game more visual life. With this help, substantial additions can
be expected in the future.