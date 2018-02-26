@echo off
echo Deleting auxilary files
attrib +r *.tex
attrib +r *.png
attrib +r *.bat
attrib +r *.pdf
del /q /a:-r *
attrib -r *.tex
attrib -r *.png
attrib -r *.bat
attrib -r *.pdf
pause