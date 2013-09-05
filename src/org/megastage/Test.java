package org.megastage;

import org.megastage.server.Main;

/**
 * Created with IntelliJ IDEA.
 * User: contko3
 * Date: 8/21/13
 * Time: 2:37 PM
 * To change this template use File | Settings | File Templates.
 */
public class Test {
    public static void main(String[] args) throws Exception {
        org.megastage.client.Main.main(new String[0]);
        Main.main(new String[]{"world.xml"});
    }
}
