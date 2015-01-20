Random Map Generator README
@Zach Janice

This Java project is an experiment of concept I created
for possible implementation of random map generation in
my "Cops and Robbers" project.

The map generator uses randomized recursion to branch out 
from a starting root until all attempts to continue 
branching have failed due to already-drawn paths. The
process results in a maze-like construction. Taking
this result, any existing branch endings and "loops" are
found and marked to, in implementation, perhaps be the
locations of different types of rooms or obstacles that
will exist in the random map. After finding these
elements, walls are placed and empty nodes are filled in
to create a random map, ready to have special map elements
added in.

Two different forms of the project exist. One is the
generator set to recursively draw the map with a single
start node. This root node can be toggled to either stay 
anchored in the middle of the map or to be randomly 
located on the map. The recursive process of drawing this
map operates similarly to depth-first graph traversal.
The other form is the "multiroot" generator that can draw 
the map with a varying number of roots, determined by the 
user. For this version, the recursive branching process is
done with a queue instead of recursive function calls, and 
operates similarly to breadth-first graph traversal. The 
dimensions of the map generated can be set with the "Set X" 
and "Set Y" buttons on the interface.

In understanding the overall result, the bright green node
is the root node that the map recursively branched out
from. If no bright green node exists, a gray node exists 
instead, signifying a root node that has been "breached"
by the end of a branch (blue node) that leads into another
branch (this removal of the wall, or "breach", was
implemented to make generated maps less maze-like and more
open). Since the breach may affect any map elements there,
the root node is marked accordingly. Branch endings that
occur inside a loop are nodes of a light red color, while
loops themselves are a lighter shade of green than regular
branch paths. Beyond the root node, non-green nodes
signal a possible position of special rooms or map elements
while loops create areas without walls, helping to make the 
map more open. The white dots near the edges of nodes show
in what direction (if any) the path branched off from that
node.

<TODO>
Adjustments to the multiroot generator still need to
be made; maps generated with three or more roots have
a chance to be "closed", where different sections of
the map are walled off and cannot be accessed by each
other. Due to a small hole in the logic behind my
method of checking for a closed map product, these
maps are presented as a final product for three or
more roots whereas they are otherwise discarded
correctly for one or two roots. Also, I need to
combine the two setups - regular and multiroot - into
single classes rather than multiples of some.