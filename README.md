Megastage v0.1
==============
<i>Random ECS experiments inspired by DCPU</i>


Few months ago I start to learn 3d programming. My progress has been painfully slow and results are not that good, but it has been a wonderful learning experience and my plan is to continue my exploration.

In December 2013 I released 0.1 version of my software. Not to impress anyone else, but to pace my work have to feeling to accomplish something. This version is only a proof of concept type things - it has no game in it. Following instructions contain a lot of "future version" references that should give some light to the direction of my development efforts. 

INSTRUCTIONS

Download distribution package megastage.zip behind the following link. It should work in Windows and Linux if you have recent enough (?) java installed.

https://github.com/orlof/megastage/releases

Start server by going to installation directory and typing:

    ./server.sh (linux)
    server.bat (windows)

Next start client by typing

    ./client.sh (linux)
    client.bat (windows)
 
In the future there will be multiplayer support but its not there yet.

After starting the client, a first person view should open on your screen. Imagine yourself standing on a starship bridge and don't mind that the whole starship is represented by a wooden "floor" :-)

- Yes - all ships are a independent block-worlds that can be modified in the future versions.

Your first person view is always anchored to some ship (or to planet in the future) that will fix your coordinate system (Ship floor is always in the bottom of your screen). Ships have artificial gravity and that should make the 1st person perspective more playable.

...yes - very far in my wish list there is also the possibility that artificial gravity could fail and your first person view would rotate around any axis...

In front of you, you should see a Sun like star - the main source of energy in future versions of the game. By looking around with mouse you should be able to find a planet orbiting the star and moon orbiting the planet. All celestial movement is like 100x faster in this demo than it should be. Sorry to say, but I broke the planet graphics and now there is a major flickering ruining the immersion. It will be fixed in the future. Planets and moons show more and more details the closer you get. Landing and planetal buildings should be possible in the future.

Currently there are no physics involved - you can walk outside the ship floor, you can fly through stars and planets. That will be fixed in the future.

You can move your character with arrow keys. To pilot the ship, you can use following key controls:

    Q / Z: Up / Down
    W / S: Forward / Backward
    A / D: Left / Right
    I / K: Pitch Up / Down
    J / L: Roll CCW / CW
    U / O: Yaw Left / Right

(hmmm. I just realized that I may have messed some of the controls inversed - never mind)

Flight model will be radically different as soon as I get the velocity vectors, engines, thrust, gyroscopes etc... implemented.

One more thing you can do: Behind you there is a big live DCPU LEM screen. If you press the left mouse button, the game will change to use-item-mode. As you can see, there is only one item on board: the DCPU. DCPU currently boots up with fixed boot disk that happens to contain Admiral 

https://github.com/orlof/dcpu-admiral

In the future the DCPU boot disk can be freely changed and DCPU will have control to all ship systems including engines, gyroscopes, positioning systems, weapons, radars...

