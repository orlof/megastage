package org.megastage.protocol;

public class LoginAction implements Action {
    public String name;

    public void receive(PlayerConnection conn) {
        conn.nick = name;
    }


}

