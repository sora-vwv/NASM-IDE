@echo off
color 03
"./NASM/nasm.exe" --gprefix _ -f win32 "%1" -o "%1.o" -i "./include/"
"./MinGW/bin/gcc.exe" "%1.o" "./NASM/macro.o" -g -o "%1.exe" -m32
"%1.exe"
echo.Program is completed
pause >nul
exit 0