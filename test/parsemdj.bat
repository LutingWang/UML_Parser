@echo off
cd test\
set command=%1
set filename=%2
set modelname=%3
set modeltype=%4
if "%command%" == "" (
    echo No command specified.
    goto :eof
)
if "%filename%" == "" (
    set filename=%command%
    set command=list
)
if "%command%" == "dump" (
    if "%modelname%" == "" (
        echo Please specify model to dump.
        goto :eof
    )
    if "%modeltype%" == "" (
    	echo Please specify type to dump.
    	goto :eof
    )
    java -jar ..\dependencies\uml-homework-2.1.0-raw-jar-with-dependencies.jar dump -s "%filename%.mdj" -n %modelname% -t %modeltype% > %filename%.txt
    echo END_OF_MODEL >> %filename%.txt
    python gen.py %filename%.txt %modeltype%
) else if "%command%" == "list" (
    if "%modelname%" NEQ "" (
        echo Too many arguments.
        goto :eof
    )
    java -jar ..\dependencies\uml-homework-2.1.0-raw-jar-with-dependencies.jar list -s "%filename%.mdj"
) else (
    echo Unspecified command.
)

:eof
cd ..\
