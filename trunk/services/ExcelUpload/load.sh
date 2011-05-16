#!/bin/bash
if [ $# -eq 0 ] 
then
	echo "Usage: $0 FILENAME [DBHOST] [DBNAME] [imagepath] [debug]"
	exit

fi

java -Dfile.encoding=Cp1252 -cp excelupload.jar:/usr/share/tomcat6/lib/mysql-connector-java.jar net.morphbank.loadexcel.LoadData $1 $2 $3 $4 $5


