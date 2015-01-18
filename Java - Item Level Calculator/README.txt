Item Level Calculator README
@Zach Janice

This application is one I designed for use with World of
Warcraft. Ever since item level has become one of several
measurements of character progression and quality, I (and
many other players, I imagine) would constantly reach for
a nearby calculator and ask "So if I got this piece of
gear with this item level, what would my overall level be
afterwards? How many pieces of gear do I need to replace,
and with what level of gear exactly, to reach this specific
level desired?"

Repeating these calculations quickly grew annoying, so I
developed this application. The application allows you to
create a file for each of your characters. With each file,
you can enter in your character's item levels for each piece
of gear and see the overall statistics of your gear, including 
the all-important "final" item level displayed in your character
pane. The application includes two modes: The Current Mode,
where you can see what your gear currently is, and the Edit
Mode, where you can make tentative changes to your gear
and see how it would affect your item level. A test file is 
included as an example of how the application works.

The application also demonstrates a set of classes I wrote
that allows for easier Java application creation. Using
a set of abstract classes, a developer can create windows,
displays with buttons and fields, mouse and keybaord listeners, 
and file writers/loaders. As someone who likes using Java
to make applications, someone who particularly hates using
predefined objects like JButtons, and as someone who wants
an easier way to create/use windows and displays where needed,
I wrote predefined, yet still flexible and implementable,
classes of this sort. The main portion of these classes
are the buttons and fields, which are interactable items
in the display that are (in my opinion) much more easily
implementable than the Java ones. These classes are
in the "Application" package.

<TODO>
In the future, I would perhaps like to add a list of in-game
item levels of gear and use this list to add the ability
for the application to give recommendations. Such a
recommendation would be, for example, "To get to 640 from 638,
you can replace Item 1 and Item 2 with items of level 650,
or you can replace Item 1 with a 660." Such an addition
would allow the user to look at possibile solutions for
reaching a targeted level and decide how to proceed.