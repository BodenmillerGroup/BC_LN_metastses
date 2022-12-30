import qupath.lib.gui.tools.MeasurementExporter
import qupath.lib.objects.PathCellObject

// Get the list of all images in the current project
def project = getProject()
def imagesToExport = project.getImageList()

// Separate each measurement value in the output file with a tab ("\t")
def separator = "\t"

// Choose the columns that will be included in the export
// Note: if 'columnsToInclude' is empty, all columns will be included
def columnsToInclude = new String[]{"Image", "Centroid X µm","Centroid Y µm","Nucleus: Area","Nucleus: Hematoxylin OD mean","Nucleus: DAB OD mean","Cell: Area","Cell: Hematoxylin OD mean","Cell: DAB OD mean"}

// Choose the type of objects that the export will process
// Other possibilities include:
//    1. PathAnnotationObject
//    2. PathDetectionObject
//    3. PathRootObject
// Note: import statements should then be modified accordingly
def exportType = PathCellObject.class

//Get project name
def projectname = project.getName().split("\\/")[0]

//Get marker name
def markername = imagesToExport[0].toString().split("_")[0]

// Choose your *full* output path
def outputPath = "/mnt/Validation_LNmets/singlecelldat/" + markername.toString() + "/" + projectname.toString() + "_measurements.tsv" 
def outputFile = new File(outputPath)

// Create the measurementExporter and start the export
def exporter  = new MeasurementExporter()
                  .imageList(imagesToExport)            // Images from which measurements will be exported
                  .separator(separator)                 // Character that separates values
                  .includeOnlyColumns(columnsToInclude) // Columns are case-sensitive
                  .exportType(exportType)               // Type of objects to export
                  .exportMeasurements(outputFile)        // Start the export process

print "Done!"