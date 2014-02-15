package com.cubes.network;

import java.io.IOException;

public abstract interface BitSerializable
{
  public abstract void write(BitOutputStream paramBitOutputStream);

  public abstract void read(BitInputStream paramBitInputStream)
    throws IOException;
}

/* Location:           /home/teppo/Dropbox/Computer/0x10c/megastage/lib/cubes/Cubes.jar
 * Qualified Name:     com.cubes.network.BitSerializable
 * JD-Core Version:    0.6.2
 */