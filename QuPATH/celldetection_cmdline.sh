for i in $(find /mnt/Validation_LNmets/analysis/HER2/ -path "*project.qpproj" -print);
do ./QuPath-0.2.3 script -p $i /home/jana/Github/TheSingleCellPathologyLandscapeofBreastCancer/QuPATH/Automate_ROI_and_detection_HER2.groovy;
done

