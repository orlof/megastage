echo Fanfare for Space Kevin MacLeod (incompetech.com)
echo All This Kevin MacLeod (incompetech.com)
set JAVA_HOME="%~dp0jre7"
%JAVA_HOME%\bin\javaw.exe -cp Megastage.jar org.megastage.server.Main --config world.xml
