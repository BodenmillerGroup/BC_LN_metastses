//Create a QuPath classifier by scripting, rather than the 'standard' way with annotations.
//by @Pete Bankhead
//but adapted to train classifier on one image and then loop through all images and apply it.

import qupath.lib.classifiers.Normalization
import qupath.lib.objects.PathObject
import qupath.lib.objects.classes.PathClassFactory
import qupath.lib.gui.scripting.QPEx

//Loop through to find the image with intermediate positive stain for thresholding
def avgs = []
def names = []
def imagelists = []
getProject().getImageList().each{
        //selectAnnotations()
        def imageData = it.readImageData()
        def objs = it.readHierarchy().getCellObjects()
        def intensities = objs.collect{cell -> measurement(cell,'Cell: DAB OD mean')}as double[]
        avgs << intensities.average()
        names << it.getImageName()
        imagelists << it
}
print(avgs)
print(names)

def middleVal = avgs.sort()[5]
def highestImagelist = imagelists.get(avgs.indexOf(middleVal))
print(highestImagelist)

def highestImageData = highestImagelist.readImageData()
def highestHierarchy = highestImagelist.readHierarchy()

// Optionally check what will be used for training -
// setting the training classification for each cell & not actually building the classifier
// (i.e. just do a sanity check)
boolean checkTraining = false

// Get all cells
def cells = highestHierarchy.getCellObjects()

// Split by some kind of DAB measurement
def isTumor = {PathObject cell -> return (cell.getMeasurementList().getMeasurementValue('Nucleus: Area') > 28 && cell.getMeasurementList().getMeasurementValue('Nucleus: Circularity') > 0.4) || cell.getMeasurementList().getMeasurementValue('Cell: DAB OD mean') > 0.8}
def tumorCells = cells.findAll {isTumor(it)}
def nonTumorCells = cells.findAll {!isTumor(it)}
print 'Number of tumor cells: ' + tumorCells.size()
print 'Number of non-tumor cells: ' + nonTumorCells.size()

// Create a relevant map for training
def map = [:]
map.put(PathClassFactory.getPathClass('Tumor'), tumorCells)
map.put(PathClassFactory.getPathClass('Stroma'), nonTumorCells)

// Check training... if necessary
if (checkTraining) {
    print 'Showing training classifications (not building a classifier!)'
    map.each {classification, list ->
        list.each {it.setPathClass(classification)}
    }
    fireHierarchyUpdate(highestHierarchy)
    return
}

// Get features & filter out the ones that shouldn't be used (here, any connected to intensities)
def features = PathClassifierTools.getAvailableFeatures(highestHierarchy.getDetectionObjects())
//features = features.findAll {!it.toLowerCase().contains(': dab') && !it.toLowerCase().contains(': hematoxylin')}// && !it.toLowerCase().contains('Cell:')}
features = features.findAll {!it.toLowerCase().contains(': hematoxylin')}


// Print the features
print Integer.toString(features.size()) + ' features: \n\t' + String.join('\n\t', features)

// Create a new random trees classifier with default settings & no normalization
print 'Training classifier...'
// This would show available parameters
// print classifier.getParameterList().getParameters().keySet()
def classifier = new qupath.opencv.classify.RTreesClassifier()
classifier.updateClassifier(map, features as List, Normalization.NONE)


//Loop through to apply same classifier to all images in project (and not train individually in each project)
getProject().getImageList().each{
        //selectAnnotations()
        def imageData = it.readImageData()
        def objs = it.readHierarchy().getCellObjects()
        def hierarchy = imageData.getHierarchy()
        hierarchy.removeObjects(hierarchy.getDetectionObjects(), false)
        classifier.classifyPathObjects(objs)
        hierarchy.addPathObjects(objs)
        hierarchy.resolveHierarchy()
        fireHierarchyUpdate(hierarchy)
        it.saveImageData(imageData)
}
