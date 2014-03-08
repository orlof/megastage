echo Fanfare for Space Kevin MacLeod (incompetech.com)
echo All This Kevin MacLeod (incompetech.com)

set JAVA_HOME=%~dp0jre7
start "Megastage Server" "%JAVA_HOME%\bin\javaw.exe" -classpath Megastage.jar org.megastage.server.Main world.xml

timeout 5 /nobreak
start "Megastage Client" "%JAVA_HOME%\bin\javaw.exe" -jar Megastage.jar
