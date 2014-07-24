package org.megastage.util;

public class CmdLineParser {
    private final String[] args;
    
    public CmdLineParser(String[] args) {
        this.args = args;
    }
    
    public boolean isDefined(String name) {
        for(String arg: args) {
            if(arg.equalsIgnoreCase(name)) {
                return true;
            }
        }
        
        return false;
    }

    public String getString(String name, String defaultValue) {
        boolean found = false;
        for(String arg: args) {
            if(found) {
                return arg;
            }
            if(arg.equalsIgnoreCase(name)) {
                found = true;
            }
        }
        
        return defaultValue;
    }
    
    public int getInteger(String name, int defaultValue) {
        String value = getString(name, String.valueOf(defaultValue));
        return Integer.parseInt(value);
    }
}
