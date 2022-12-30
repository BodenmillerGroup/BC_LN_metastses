for i in $(find /mnt/Validation_LNmets/analysis/HER2/ -path "*project.qpproj" -print);
do ./QuPath-0.2.3 script -p $i /home/jana/Github/TheSingleCellPathologyLandscapeofBreastCancer/QuPATH/Automate_object_classification.groovy;
done

