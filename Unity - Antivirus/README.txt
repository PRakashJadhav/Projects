Antivirus README
@Zach Janice

This project is the final project my group and I
completed for our class "Introduction to Game Design".
The game is called "Antivirus", a tower defense game
with the theme of defending your computer in cyberspace
from malware and DDOS attacks. Using different methods /
resources (towers) at your disposal, your goal is to
prevent the central control structure from becoming
infected.

As this was a group project, we all gave ideas on and 
helped with each piece of the project, but otherwise
divided tasks amongst ourselves to work on in majority.
My task was to develop the towers and their different
types. I wrote two classes, one that defined the shared
procedures of the towers (such as how towers find an
individual target or multiple targets) and another that
defined the specific behaviours of different towers,
behaviours that made them distinct from others.

For the tower's targeting AI, the towers search for the 
most imminent threat. As I defined it, this most imminent
threat is the enemy or enemies that are closest to the
beacon(s) on the map. For the final product, we only use
one beacon for each map, and this beacon is the object
that the player pivots on and tries to defend. This method
of target detection seemed to work effectively in play
testing, especially on the spiral-like level. Unless
otherwise specified (like the Process Moderator and
Process Terminator towers), towers target based on
proximity to the beacon and carry out actions form there.

<TODO>
While the project is completed, if I could change
aspects of the project and improve them, I would
turn my attention to the display of information.
We experimented with a HUD detached form the camera
and, instead, displayed along the exterior of the
map. While this idea was interesting in concept, I
personally believe it fell short in execution. As
I play through the game, I grow frustrated that I
must turn my camera (in most cases, away from the
focal point of my defense) to check pieces of
information.