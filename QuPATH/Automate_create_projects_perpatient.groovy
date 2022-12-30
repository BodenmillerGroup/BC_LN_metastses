import groovy.io.FileType
import java.awt.image.BufferedImage
import qupath.lib.images.servers.ImageServerProvider
import qupath.lib.gui.commands.ProjectCommands
import java.util.ArrayList

//Find all patient IDs in gata3 folder (most IDs)
fileDir = new File("/mnt/Validation_LNmets/IHC_scans/GATA3/")

// Build a list of files and extract patient IDs
//def files = []
def PIDs = []
fileDir.eachFileRecurse (FileType.FILES) { file ->
    if (file.getName().toLowerCase().endsWith(".czi"))
    {
        //files << file
        PIDs << file.toString().split("_")[3]
        //print(file.getCanonicalPath())      
    }
}
print(PIDs)

//Get all files of all subfolders
def files_all = []
def dir = new File("/mnt/Validation_LNmets/IHC_scans/")
dir.eachFileRecurse (FileType.FILES) { file ->
  files_all << file
}

//Output folder
selectedDir = new File("/mnt/Validation_LNmets/analysis_patient/")

//For each PID find all marker files
for (int i = 0; i < PIDs.size(); i++){
    def cur = PIDs[i]
    def pattern = ~('.*'+cur)
    def matches = []
    files_all.each{if(it ==~ pattern){matches << it}}
    
    //make project for each PID
    PID_name = cur.toString().split("\\.")[0]
    projectName = "QuPathProject_"+PID_name.toString()
    File directory = new File(selectedDir.toString() + File.separator + projectName)

    if (!directory.exists())
    {
        print("No project directory, creating one!")
        directory.mkdirs()
    }
    // Create project
    def project = Projects.createProject(directory , BufferedImage.class) 
    
    // Add files to project
    for (file in matches) {
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
    // Changes should now be reflected in the project directory
    project.syncChanges()
}
