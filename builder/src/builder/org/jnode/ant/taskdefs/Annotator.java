package org.jnode.ant.taskdefs;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.jnode.annotation.MagicPermission;
import org.jnode.annotation.SharedStatics;
import org.objectweb.asm.Attribute;
import org.objectweb.asm.ClassAdapter;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Type;
import org.objectweb.asm.attrs.Annotation;
import org.objectweb.asm.attrs.Attributes;
import org.objectweb.asm.attrs.RuntimeVisibleAnnotations;

public class Annotator {
    private static final String SHAREDSTATICS_TYPE_DESC = Type.getDescriptor(SharedStatics.class);
    private static final String MAGICPERMISSION_TYPE_DESC = Type.getDescriptor(MagicPermission.class);

    public boolean addAnnotations(InputStream inputClass, File outputClassFile, Collection<String> annotations)
        throws Exception {
        boolean classIsModified = false;
        FileOutputStream outputClass = null;

        ClassWriter cw = new ClassWriter(false);
        try {
            ClassReader cr = new ClassReader(inputClass);

            List<String> annotationTypeDescs = new ArrayList<String>(2);
            if (annotations.contains("SharedStatics")) {
                annotationTypeDescs.add(SHAREDSTATICS_TYPE_DESC);
            }
            if (annotations.contains("MagicPermission")) {
                annotationTypeDescs.add(MAGICPERMISSION_TYPE_DESC);
            }

            MarkerClassVisitor mcv = new MarkerClassVisitor(cw, annotationTypeDescs);
            cr.accept(mcv, Attributes.getDefaultAttributes(), true);

            if (mcv.classIsModified()) {
                System.out.println("adding annotations " + annotations + " to file " + outputClassFile.getName());
                classIsModified = true;

                outputClass = new FileOutputStream(outputClassFile);

                byte[] b = cw.toByteArray();
                outputClass.write(b);
            }
        } finally {
            if (outputClass != null) {
                try {
                    outputClass.close();
                } catch (IOException e) {
                    System.err.println("Can't close stream for file " + outputClassFile.getName());
                }
            }
        }

        return classIsModified;

//        return false;
    }

    /**
     * Visitor for a class file that actually do the job of adding annotations in the class.
     *
     * @author fabien
     */
    private static class MarkerClassVisitor extends ClassAdapter {
        private final List<String> annotationTypeDescs;

        private boolean classIsModified = false;

        public MarkerClassVisitor(ClassVisitor cv, List<String> annotationTypeDescs) {
            super(cv);

            this.annotationTypeDescs = annotationTypeDescs;
        }

        @Override
        public void visit(int version, int access, String name,
                          String superName, String[] interfaces, String sourceFile) {
            super.visit(org.objectweb.asm.Constants.V1_5, access,
                name, superName, interfaces, sourceFile);
        }

        @Override
        public void visitAttribute(Attribute attr) {
            if (attr instanceof RuntimeVisibleAnnotations) {
                RuntimeVisibleAnnotations rva = (RuntimeVisibleAnnotations) attr;
                for (Object annotation : rva.annotations) {
                    if (annotation instanceof Annotation) {
                        Annotation ann = (Annotation) annotation;
                        for (String annTypeDesc : annotationTypeDescs) {
                            if (ann.type.equals(annTypeDesc)) {
                                // we have found one of the annotations -> we won't need to add it again !
                                annotationTypeDescs.remove(annTypeDesc);
                                break;
                            }
                        }
                    }
                }
            }

            super.visitAttribute(attr);
        }

        @SuppressWarnings("unchecked")
        public void visitEnd() {
            if (!annotationTypeDescs.isEmpty()) {
                // we have not found the annotation -> we will add it and so modify the class
                classIsModified = true;
                RuntimeVisibleAnnotations attr = new RuntimeVisibleAnnotations();

                for (String annTypeDesc : annotationTypeDescs) {

                    Annotation ann = new Annotation(annTypeDesc);
                    ann.add("name", "");

                    attr.annotations.add(ann);
                }

                cv.visitAttribute(attr);
            }

            super.visitEnd();
        }

        public boolean classIsModified() {
            return classIsModified;
        }
    }
}
