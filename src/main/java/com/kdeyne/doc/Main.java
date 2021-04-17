package com.kdeyne.doc;

import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.api.Git;
import spoon.Launcher;
import spoon.legacy.NameFilter;
import spoon.reflect.CtModel;
import spoon.reflect.declaration.*;
import spoon.reflect.visitor.filter.TypeFilter;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

public class Main {

    //Github setup: GitHub github = new GitHubBuilder().withOAuthToken("my_personal_token").build()

    //sample: https://github.com/vmudigal/microservices-sample

    private static final File TEMP_DIR = new File("D:\\workspace\\temp");
    private static final String SAMPLE_REPO = "https://github.com/vmudigal/microservices-sample.git";
    private static final boolean CLEAN = false;

    public static void main(String[] args) throws Exception {
        if(CLEAN) {
            FileUtils.cleanDirectory(TEMP_DIR);

            Git.cloneRepository()
                    .setURI(SAMPLE_REPO)
                    .setDirectory(TEMP_DIR)
                    .call()
                    .close();
        }

        traverseFiles(TEMP_DIR);
    }

    private static void traverseFiles(File dir) throws IOException {
        for(File file : Objects.requireNonNull(dir.listFiles())) {
            if(file.isDirectory()) {
                traverseFiles(file);
            }

            if(isSupportedFile(file)) {
                interpretClass(file);
            }
        }
    }

    private static void interpretClass(File file) {
        List<CtType> ctTypes = getCtTypes(file);
        if(!ctTypes.isEmpty()) {
            System.out.println(">>" + file.getAbsolutePath().replace(TEMP_DIR.getAbsolutePath(), ""));
            for(CtType ctType : ctTypes) {
                System.out.println("  - " + ctType.getQualifiedName());

                List<CtNamedElement> elements = ctType.getElements(new NameFilter<>("rabbitTemplate"));
                if(!elements.isEmpty()) {
                    System.out.println("  - " + elements);
                }
            }
        }
    }

    static List<CtType> getCtTypes(File file) {
        Launcher launcher = new Launcher();
        launcher.addInputResource(file.getAbsolutePath());
        launcher.buildModel();
        CtModel model = launcher.getModel();
        return model.getElements(new TypeFilter<>(CtType.class));
    }

    static boolean isSupportedFile(File file) {
        return file.getName().endsWith(".java");
    }

}
