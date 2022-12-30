for i in $(find /mnt/Validation_LNmets/analysis_patient/ -path "*project.qpproj" -print);
do 
	./QuPath-0.2.3 script -s -p $i /home/jana/Github/TheSingleCellPathologyLandscapeofBreastCancer/QuPATH/Automate_detection_manual_ROIs.groovy;
	#./QuPath-0.2.3 script -p $i /home/jana/Github/TheSingleCellPathologyLandscapeofBreastCancer/QuPATH/Automate_measurement_export_patient.groovey;
	echo "finished with $i"
done
