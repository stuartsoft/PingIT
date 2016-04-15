#!/bin/bash

#all connected devices should have the lock screen DISABLED for this to work

serialnumlist=( $(adb devices | sed -n '1!p' | cut -d$'\t' -f1) )
for i in "${serialnumlist[@]}"
do
	echo $i
	adb -s $i shell input keyevent 82 #press menu button/ unlock
	adb -s $i shell input keyevent 3  #press home button just to be sure
	adb -s $i shell pm uninstall edu.gcc.whiletrue.pingit
done
