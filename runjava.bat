@echo off
setlocal

cd target\dataspy\web-inf\lib
set CP=..\..\..\classes;

for %%f in (*.jar) do call ..\..\..\..\cpappend %%f

rem java -Djava.compiler=NONE -Xmx769m -classpath %CP% %*
java -classpath %CP% %*
