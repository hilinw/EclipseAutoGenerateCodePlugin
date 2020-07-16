package autogenerate.code.plugin.tools;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.*;

public class CopyTask {
    private static final Log LOG = LogFactory.getLog(CopyTask.class);
    private File file;
    private File targetDirectory;

    public CopyTask() {
    }

    public CopyTask(File file, File targetDirectory) {
        this.file = file;
        this.targetDirectory = targetDirectory;
    }

    public File getFile() {
        return this.file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public File getTargetDirectory() {
        return this.targetDirectory;
    }

    public void setTargetDirectory(File targetDirectory) {
        this.targetDirectory = targetDirectory;
    }

    public void execute() throws IOException {
        this.targetDirectory.mkdirs();
        File destFile = new File(this.targetDirectory, this.file.getName());
        if (LOG.isInfoEnabled()) {
            LOG.info("copy to '" + destFile.getPath() + "'");
        }
        IOUtils.copy(this.file, destFile);
        System.out.println("copy to '" + destFile.getPath() + "'");
    }

}