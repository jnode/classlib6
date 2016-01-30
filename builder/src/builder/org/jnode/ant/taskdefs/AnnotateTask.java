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
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.Properties;
import org.apache.tools.ant.BuildException;

/**
 * That ant task will add some annotations to some compiled classes
 * mentioned in a property file.
 * For now, it's only necessary to add annotations to some
 * openjdk classes to avoid modifying the original source code.
 *
 * @author Fabien DUMINY (fduminy at jnode dot org)
 */
public class AnnotateTask extends FileSetTask {
    private File annotationFile;
    private String[] classesFiles;

    private String buildStartTime = "";
    private String pattern = "";
    private long startTime = 0;
    private String baseDir;

    private Properties annotations = new Properties();
    private final Annotator annotator = new Annotator();

    protected int doExecute() throws BuildException {
        try {
            SimpleDateFormat format = new SimpleDateFormat(pattern);
            startTime = format.parse(buildStartTime).getTime();
        } catch (Exception e) {
            throw new BuildException("invalid buildStartTime or pattern", e);
        }

        int nbModifiedFiles = 0;
        try {
            if (readProperties()) {
                for (String file : classesFiles) {
                    File classFile = new File(baseDir, file);
                    boolean fileModified = processFile(classFile);
                    if (fileModified) {
                        nbModifiedFiles++;
                    }
                }
            }
        } catch (IOException ioe) {
            throw new BuildException(ioe);
        }
        return nbModifiedFiles;
    }

    /**
     * Defines the annotation property file where are specified annotations to add.
     *
     * @param annotationFile
     */
    public final void setAnnotationFile(File annotationFile) {
        this.annotationFile = annotationFile;
    }

    /**
     * Define the time at which build started.
     *
     * @param buildStartTime
     */
    public final void setBuildStartTime(String buildStartTime) {
        this.buildStartTime = buildStartTime;
    }

    /**
     * Define the pattern with which buildStartTime is defined.
     *
     * @param pattern
     */
    public final void setPattern(String pattern) {
        this.pattern = pattern;
    }

    public void setBaseDir(String baseDir) {
        this.baseDir = baseDir;
    }

    /**
     * Read the properties file. For now, it simply contains a list of
     * classes that need the SharedStatics annotation.
     *
     * @return
     * @throws BuildException
     */
    private boolean readProperties() throws BuildException {
        readProperties("annotationFile", annotationFile, annotations);
        if (annotations.isEmpty()) {
            System.err.println("WARNING: annotationFile is empty (or doesn't exist)");
            return false;
        }

        classesFiles = (String[]) annotations.keySet().toArray(new String[annotations.size()]);

        // we must sort the classes in reverse order so that
        // classes with longest package name will be used first
        // (that is only necessary for classes whose name is the same
        // but package is different ; typical such class name : "Constants")
        Arrays.sort(classesFiles, Collections.reverseOrder());

        return true;
    }

    /**
     * Generic method that read properties from a given file.
     *
     * @param name
     * @param file
     * @param properties
     * @throws BuildException
     */
    private void readProperties(String name, File file, Properties properties) throws BuildException {
        if (file == null) {
            throw new BuildException(name + " is mandatory");
        }

        if (!file.exists()) {
            return;
        }

        FileInputStream fis = null;
        try {
            fis = new FileInputStream(file);
            properties.load(fis);
        } catch (IOException e) {
            throw new BuildException(e);
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    throw new BuildException(e);
                }
            }
        }
    }

    /**
     * Get the list of annotations for the given class file.
     *
     * @param classFile list of annotations with ',' as separator. null if no annotation for that class.
     * @return
     */
    private String getAnnotations(File classFile) {
        String annotations = null;
        String classFilePath = classFile.getAbsolutePath();
        for (String f : classesFiles) {
            if (classFilePath.endsWith(f)) {
                annotations = this.annotations.getProperty(f);
                break;
            }
        }

        return annotations;
    }

    /**
     * Actually process a class file (called from parent class).
     */
    @Override
    protected boolean processFile(File classFile) throws IOException {
        if (classFile.lastModified() < startTime) {
            return false;
        }

        String annotations = getAnnotations(classFile);
        if (annotations == null) {
            return false;
        }

        File tmpFile = new File(classFile.getParentFile(), classFile.getName() + ".tmp");
        FileInputStream fis = null;
        boolean classIsModified = false;

        try {
            fis = new FileInputStream(classFile);
            classIsModified = addAnnotation(classFile, fis, tmpFile, annotations);
        } finally {
            if (fis != null) {
                fis.close();
            }
        }

        if (classIsModified) {
            if (!classFile.delete()) {
                throw new IOException("can't delete " + classFile.getAbsolutePath());
            }

            if (!tmpFile.renameTo(classFile)) {
                throw new IOException("can't rename " + tmpFile.getAbsolutePath());
            }
        }
        return classIsModified;
    }

    /**
     * Add an annotation to a class file.
     *
     * @param classFile
     * @param inputClass
     * @param tmpFile
     * @param annotations
     * @return
     * @throws BuildException
     */
    private boolean addAnnotation(File classFile, InputStream inputClass, File tmpFile, String annotations)
        throws BuildException {
        boolean classIsModified = false;

        try {
            classIsModified = annotator.addAnnotations(inputClass, tmpFile, Arrays.asList(annotations.split(",")));
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new BuildException("Unable to add annotations to file " + classFile.getName(), ex);
        } finally {
            if (classIsModified) {
                long timestamp = classFile.lastModified();
                tmpFile.setLastModified(timestamp);
            }
        }

        return classIsModified;
    }
}
