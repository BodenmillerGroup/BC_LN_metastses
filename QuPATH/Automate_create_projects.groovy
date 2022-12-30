import groovy.io.FileType
import java.awt.image.BufferedImage
import qupath.lib.images.servers.ImageServerProvider
import qupath.lib.gui.commands.ProjectCommands
import java.util.ArrayList

//Select folder with images to loop through and put 10 into each project
fileDir = new File("/mnt/Validation_LNmets/IHC_scans/round2/p53/")

// Build a list of files
def files = []
fileDir.eachFileRecurse (FileType.FILES) { file ->
    if (file.getName().toLowerCase().endsWith(".czi"))
    {
        files << file
        print(file.getCanonicalPath())      
    }
}

//calculate number of projects required
def nprojects = Math.ceil(files.size()/10)
def extraLast = files.size()%10

if (extraLast == 0){extraLast = 10}

//selectedDir = Dialogs.promptForDirectory(null)
selectedDir = new File("/mnt/Validation_LNmets/analysis2/p53/")

if (selectedDir == null)
    return

def count = 0

//Loop through and create a project for each 10 files
for (int i = 0; i < nprojects; i++) {
    //Check if we already have a QuPath Project directory in there...
    projectName = "QuPathProject"+i.toString()
    File directory = new File(selectedDir.toString() + File.separator + projectName)

    if (!directory.exists())
    {
        print("No project directory, creating one!")
        directory.mkdirs()
    }
    
    // Create project
    def project = Projects.createProject(directory , BufferedImage.class) 
    
    def curfiles = []
    if (i < nprojects -1){
        //Get current 10 files to add to current project       
        for (int j = count;j < count+10;j++){
            curfiles << files.get(j)
        }
        print(curfiles)}else{
        //Get current 10 files plus the remaining last to add to current project
        for (int j = count;j < count+extraLast;j++){
            curfiles << files.get(j)
        }
        print(curfiles)}

    // Add files to project
    for (file in curfiles) {
        def imagePath = file.getCanonicalPath()
        
        // Get serverBuilder
        def support = ImageServerProvider.getPreferredUriImageSupport(BufferedImage.class, imagePath, "")
        def builder = support.builders.get(0)
    
        // Make sure we don't have null 
        if (builder == null) {
           print "Image not supported: " + imagePath
           continue
        }
        
        // Add the image as entry to the project
        print "Adding: " + imagePath
        entry = project.addImage(builder)
        
        // Set a particular image type
        def imageData = entry.readImageData()
        imageData.setImageType(ImageData.ImageType.BRIGHTFIELD_H_DAB)
        entry.saveImageData(imageData)
        
        // Write a thumbnail if we can
        var img = ProjectCommands.getThumbnailRGB(imageData.getServer());
        entry.setThumbnail(img)
        
        // Add an entry name (the filename)
        entry.setImageName(file.getName())
    }
    count = count + 10
    
    // Changes should now be reflected in the project directory
    project.syncChanges()
}
