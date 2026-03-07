cd /d "%~dp0src"
if not exist bin mkdir bin
javac -cp "lib/*" -d bin *.java
java -cp "lib/*;bin" Main
