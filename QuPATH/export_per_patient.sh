for i in $(find /mnt/Validation_LNmets/analysis_patient/ -path "*project.qpproj" -print);
do ./QuPath-0.2.3 script -p $i /home/jana/Github/TheSingleCellPathologyLandscapeofBreastCancer/QuPATH/Automate_measurement_export_patient.groovy;
done

