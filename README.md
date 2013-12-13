Megastage 0.1
=========
<i>Random ECS experiments inspired by DCPU</i>


Few months ago I decided to start learning 3d programming. I have never done anything like that before - so my progress has been very slow and results are not that good. Anyway, this has been a wonderful learning experience so far and my plan is to continue my exploration.

December 2013 my software can demonstrate some early proof of concept implementations. Following instructions contain a lot of "future version" references that should give some light to the direction of my development efforts. 

INSTRUCTIONS

Download and install the distribution package. It should work in Windows and Linux.

Start server by going to installation directory and typing

  bin\server.bat (in Windows) or
  bin/server.sh (in Linux)

Next start client by typing

  bin\client.bat (in Windows) or
  bin/client.sh (in Linux)
 
In the future there should be multiplayer support but its not there yet.

A first person view should open on your screen. You are standing on a starship represented by a "floor" made from wooden :-) blocks.
- Yes, ship is a independent block world that can be modified in the future versions.

In front of you, should be a Sun like star - the main source of energy in future versions of the game. By looking around with mouse you should see a planet and moon in orbits and behind you there is a big DCPU screen. Sorry to say, but I broke the planet graphics and now there is a major flickering ruining the immersion. It will be fixed in the future. Planets and moons show more and more in closer distances and landing will be possible in the future.

Currently there are no physics involved - you can walk outside the ship floor, you can fly through stars and planets etc. That will be fixed in the future.

You can move your character with arrow keys. To pilot the ship, you can use following key controls:

  Q / Z: Up / Down
  W / S: Forward / Backward
  A / D: Left / Right
  I / K: Pitch Up / Down
  J / L: Roll CCW / CW
  U / O: Yaw Left / Right

This flight model will ofcourse change radically as soon as I get velocity vectors, engines, thrust, gyroscopes etc... implemented in the future.

One more thing you can do: If you press the left mouse button, the game will change to use-item-mode. As you can see, there is only one item on board: the DCPU. DCPU currently boots up with fixed boot disk that happens to contain Admiral https://github.com/orlof/dcpu-admiral

In the future the DCPU boot disk can be freely changed and DCPU will have control to all ship systems including engines, gyroscopes, positioning systems, weapons, radars...

