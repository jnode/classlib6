package org.jnode.ant.taskdefs;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.TreeSet;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.objectweb.asm.Attribute;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.attrs.Annotation;
import org.objectweb.asm.attrs.RuntimeVisibleAnnotations;
import org.objectweb.asm.util.TraceClassVisitor;
import org.objectweb.asm.util.attrs.ASMRuntimeVisibleAnnotations;

import static org.junit.Assert.assertArrayEquals;

@RunWith(Theories.class)
public class AnnotatorTest {
    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();

    @DataPoints
    public static final String[] ANNOTATIONS = new String[]{"SharedStatics", "MagicPermission"};

    private Annotator annotator;
    private InputStream inputStreamClass;
    private File outputClassFile;

    @Before
    public void setUp() throws IOException {
        annotator = new Annotator();
        outputClassFile = tempFolder.newFile("output.class");
    }

    @Test
    public void testAddAnnotation_singleAnnotationWithNoise() throws Exception {
        Collection<String> annotations = Collections.singletonList(ANNOTATIONS[0]);
        inputStreamClass = getInputStream(ClassToAnnotateWithNoise.class);

        annotator.addAnnotations(inputStreamClass, outputClassFile, annotations);

        assertClassHasAnnotations(outputClassFile, annotations);
    }

    @Theory
    public void testAddAnnotation_singleAnnotation(String annotation) throws Exception {
        Collection<String> annotations = Collections.singletonList(annotation);
        inputStreamClass = getInputStream(ClassToAnnotate.class);

        annotator.addAnnotations(inputStreamClass, outputClassFile, annotations);

        assertClassHasAnnotations(outputClassFile, annotations);
    }

    @Test
    public void testAddAnnotation_multipleAnnotations() throws Exception {
        Collection<String> annotations = Arrays.asList(ANNOTATIONS);
        inputStreamClass = getInputStream(ClassToAnnotate.class);

        annotator.addAnnotations(inputStreamClass, outputClassFile, annotations);

        assertClassHasAnnotations(outputClassFile, annotations);
    }

    private static InputStream getInputStream(Class<?> classToAnnotate) throws java.io.IOException {
        final String resourceName = "/" + classToAnnotate.getName().replace(".", "/") + ".class";
        return classToAnnotate.getResource(resourceName).openStream();
    }

    private static void assertClassHasAnnotations(File outputClassFile, Collection<String> expectedAnnotations)
        throws Exception {
        final List<String> actualAnnotations = new ArrayList<String>();
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(outputClassFile);

            ClassReader cr = new ClassReader(fis);
            ClassVisitor adapter = new TraceClassVisitor(null, new PrintWriter(new StringWriter())) {
                public void visitAttribute(Attribute var1) {
                    if (var1 instanceof RuntimeVisibleAnnotations) {
                        for (Object annotation : ((RuntimeVisibleAnnotations) var1).annotations) {
                            String className = ((Annotation) annotation).type;
                            actualAnnotations.add(className.substring(1, className.length() - 1));
                        }
                    }
                }
            };
            cr.accept(adapter, new Attribute[]{new ASMRuntimeVisibleAnnotations()}, true);
        } finally {
            if (fis != null) {
                fis.close();
            }
        }

        Collections.sort(actualAnnotations);

        TreeSet<String> expectAnnotations = new TreeSet<String>();
        for (String simpleName : expectedAnnotations) {
            expectAnnotations.add("org/jnode/annotation/" + simpleName);
        }
        assertArrayEquals(expectAnnotations.toArray(), actualAnnotations.toArray());
    }

    public static @interface NoiseAnnotation {

    }

    public static class ClassToAnnotate {

    }

    @NoiseAnnotation
    public static class ClassToAnnotateWithNoise {

    }
}