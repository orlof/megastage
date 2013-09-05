package org.megastage.components.dcpu;

public class VirtualClock extends DCPUHardware
{
  private int interval;
  private int intCount;
  private char ticks;
  private char interruptMessage;
  
	private String id;

  public VirtualClock(String id)
  {
    super(315667458, 32776, 515079825);
    this.id = id;
  }

  public VirtualClock() {
  	this("Generic Clock");
	}

	public void interrupt() {
    int a = this.dcpu.registers[0];
    if (a == 0)
      this.interval = this.dcpu.registers[1];
    else if (a == 1)
      this.dcpu.registers[2] = this.ticks;
    else if (a == 2)
      this.interruptMessage = this.dcpu.registers[1];
  }

  public void tick60hz()
  {
    if (this.interval == 0) return;
    if (++this.intCount >= this.interval) {
      if (this.interruptMessage != 0) this.dcpu.interrupt(this.interruptMessage);
      this.intCount = 0;
      this.ticks++;
    }
  }

	public String getID() {
		return id;
	}

	public void setID(String id) {
		this.id = id;
	}

	@Override
	public void powerOff() {
		this.intCount = 0;
		this.interruptMessage = 0;
		this.interval = 0;
		this.ticks = 0;
	}
}