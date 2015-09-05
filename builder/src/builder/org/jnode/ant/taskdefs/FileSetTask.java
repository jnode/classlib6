/*
 * $Id$
 *
 * Copyright (C) 2003-2009 JNode.org
 *
 * This library is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published
 * by the Free Software Foundation; either version 2.1 of the License, or
 * (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but 
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public 
 * License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; If not, write to the Free Software Foundation, Inc., 
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */
 
package org.jnode.ant.taskdefs;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.FileSet;

/**
 * Abstract class for ant task that process one or more FileSet
 * The concrete classes only have to implement the <i>process</i> method
 * for doing the concrete work on a file.
 *
 * @author Fabien DUMINY (fduminy at jnode.org)
 */
public abstract class FileSetTask extends Task {
    protected boolean trace = false;
    protected boolean failOnError = true;

    private final ArrayList<FileSet> fileSets = new ArrayList<FileSet>();

    public final void setTrace(boolean trace) {
        this.trace = trace;
    }

    public final void setFailOnError(boolean failOnError) {
        this.failOnError = failOnError;
    }

    public void addFileSet(FileSet fs) {
        fileSets.add(fs);
    }

    public final void execute() throws BuildException {
        try {
            int nbModifiedFiles = doExecute();
            if (nbModifiedFiles == 0) {
                log("Files are already up to date");
            } else {
                log("%d files have been modified", nbModifiedFiles);
            }
        } catch (BuildException be) {
            if (failOnError) {
                throw be;
            } else {
                be.printStackTrace();
            }
        } catch (Throwable t) {
            if (failOnError) {
                throw new BuildException(t);
            } else {
                t.printStackTrace();
            }
        }
    }

    protected int doExecute() throws BuildException {
        // default implementation : simply iterate on all files
        return processFiles();
    }

    protected final int processFiles() throws BuildException {
        final Project project = getProject();
        int nbModifiedFiles = 0;
        try {
            for (FileSet fs : fileSets) {
                final String[] files = fs.getDirectoryScanner(project)
                    .getIncludedFiles();
                final File projectDir = fs.getDir(project);
                for (String fname : files) {
                    boolean modified = processFile(new File(projectDir, fname));
                    if (modified) {
                        nbModifiedFiles++;
                    }
                }
            }
        } catch (IOException e) {
            throw new BuildException(e);
        }
        return nbModifiedFiles;
    }

    /**
     *
     * @param file
     * @return true is the file has been modified, false if it was already up to date.
     * @throws IOException
     */
    protected abstract boolean processFile(File file) throws IOException;
}
