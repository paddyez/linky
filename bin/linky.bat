rem run linky

echo off
cd "%~dp0%"
cd ..
java -cp lib\pircbot-bin.jar;lib\xstream-1.4.7.jar;lib\lib-util.jar;build\classes net.psammead.linky.Linky
