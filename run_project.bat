@echo off

cd /d C:\meera\CampusFindAI

start cmd /k "cd /d python_ai && py ocr_api.py"

timeout /t 5

javac -cp ".;lib/*" src\*.java

java -cp ".;src;lib/*" Main

pause