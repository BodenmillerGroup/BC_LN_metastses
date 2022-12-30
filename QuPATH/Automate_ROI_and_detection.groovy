import qupath.lib.objects.PathObjects
import qupath.lib.roi.ROIs
import qupath.lib.regions.ImagePlane

removeObjects(getDetectionObjects(),false)

int z = 0
int t = 0
def plane = ImagePlane.getPlane(z, t)
def server = getCurrentServer()
def roi = ROIs.createRectangleROI(0, 0, server.getWidth(), server.getHeight(), plane)
def annotation = PathObjects.createAnnotationObject(roi)
addObject(annotation)

selectAnnotations()
runPlugin('qupath.imagej.detect.cells.WatershedCellDetection', '{"detectionImageBrightfield": "Optical density sum",  "requestedPixelSizeMicrons": 0.5,  "backgroundRadiusMicrons": 8.0,  "medianRadiusMicrons": 0.0,  "sigmaMicrons": 1.5,  "minAreaMicrons": 10.0,  "maxAreaMicrons": 500.0,  "threshold": 0.1,  "maxBackground": 2.0,  "watershedPostProcess": true,  "excludeDAB": false,  "cellExpansionMicrons": 5.0,  "includeNuclei": true,  "smoothBoundaries": true,  "makeMeasurements": true}');
getProjectEntry().saveImageData(getCurrentImageData())