Megastage v0.1
==============
<i>Random ECS experiments inspired by DCPU</i>


BACKGROUND

Few months ago I decided to start learning 3d programming. Progress in my learning project: Megastage, has been painfully slow and results are not that good, but it has been a wonderful learning experience and my plan is to continue the journey.

In December 2013 I released 0.1 version of Megastage. It is only a technology demo, not ment to impress anyone, but to pace my own work and give me the feeling that I have accomplished something. This version is only about Proof of Concept type things - it has no game in it. Following instructions contain a lot of "in the future"-references that should enlighten you about my plans. 

INSTRUCTIONS

Download distribution package megastage.zip by following the link below. Distribution should work in Windows and Linux if you have recent enough java installed. (My 'java -version' reports 'Java(TM) SE Runtime Environment (build 1.7.0_25-b15)')

https://github.com/orlof/megastage/releases

Unzip megastage.zip:

    unzip -d megastage megastage.zip
    (or use something similar in Windows)

Go to the installation directory:

    cd megastage

Start server by going to installation directory and typing:

    ./server.sh (linux)
    server.bat (windows)

Next start client by typing

    ./client.sh (linux)
    client.bat (windows)
 
In the future there will be multiplayer support but currently server supports only one client connection per server start.

After starting the client, a first person view should appear on your screen. Imagine yourself standing on a starship bridge and don't mind that the whole starship is represented by a wooden "floor" :-)

Yes - all ships are a independent block-worlds that can be modified in the future versions.

All ships have artificial gravity. It makes the first person view more playable as your coordinate system stays aligned with gravity (y-axis) and your view is always anchored to a ship (or to a planet in the future).

...yes - very far in my wish list there is also the possibility that artificial gravity could fail and your first person view would start rotating along all axis...

In front of you, you should see a Sun like star - the main source of energy in future versions of the game. By looking around with mouse you should find a planet orbiting the star and moon orbiting the planet. All celestial movement is like 100x faster in this demo than it will be in the future. Sorry to say, but I broke the planet graphics and now there is a major flickering ruining the immersion at long distances. It will be fixed in the future. Planets and moons show more and more details the closer you get. Landing and building on planets should be possible in the future.

Currently there is NO physics implemented - you can walk outside the ship floor, you can fly through stars and planets. That will be fixed in the future.

You can move your character with arrow keys. To pilot the ship, you can use following key controls:

    Q / Z: Up / Down
    W / S: Forward / Backward
    A / D: Left / Right
    I / K: Pitch Up / Down
    J / L: Roll CCW / CW
    U / O: Yaw Left / Right

(hmmm. I just realized that I may have messed some of the controls inversed - never mind)

Flight model will be implemented as I get the velocity vectors, engines, thrust, gyroscopes etc... functioning.

One more thing you can do: Behind you there is a big live DCPU LEM screen. If you press the left mouse button, the game will change to use-item-mode. As you can see, there is only one item on board: the DCPU. DCPU currently boots up with fixed boot disk that happens to contain Admiral 

https://github.com/orlof/dcpu-admiral

In the future the DCPU boot disk can be freely changed and DCPU can control all ship systems including engines, gyroscopes, positioning systems, weapons, radars...

