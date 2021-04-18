package com.kdeyne.doc;

import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.api.Git;
import spoon.Launcher;
import spoon.reflect.CtModel;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.declaration.CtType;
import spoon.reflect.visitor.filter.TypeFilter;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class Main {

    //Github setup: GitHub github = new GitHubBuilder().withOAuthToken("my_personal_token").build()
    //sample: https://github.com/vmudigal/microservices-sample

    private static final File TEMP_DIR = new File("D:\\workspace\\temp");
    private static final String SAMPLE_REPO = "https://github.com/vmudigal/microservices-sample.git";
    private static final boolean CLEAN = false;

    private static final Map<String, File> fileModel = new HashMap<>();

    public static void main(String[] args) throws Exception {
        if(CLEAN) {
            FileUtils.cleanDirectory(TEMP_DIR);

            Git.cloneRepository()
                    .setURI(SAMPLE_REPO)
                    .setDirectory(TEMP_DIR)
                    .call()
                    .close();
        }

        buildGlobalFileModelForLookups(TEMP_DIR);

        for(File file : fileModel.values()) {
            List<CtType> ctTypes = getCtTypes(file);
            for(CtType ctType : ctTypes) {
                interpretClass(fileModel, ctType);
            }
        }
    }

    private static void buildGlobalFileModelForLookups(File dir) throws IOException {
        for(File file : Objects.requireNonNull(dir.listFiles())) {
            if(file.isDirectory()) {
                buildGlobalFileModelForLookups(file);
            }

            //load full model
            if(isSupportedFile(file)) {
                Map<String, File> classModel = loadClassNameFromFile(file);
                if(!classModel.isEmpty()) {
                    fileModel.putAll(classModel);
                }
            }
        }
    }

    static Map<String, File> loadClassNameFromFile(File file) {
        final String fullFileName = file.getAbsolutePath();
        int startIndex = fullFileName.indexOf("src\\main") + 9; //9 = src/main
        String className = fullFileName
                .substring(startIndex, fullFileName.length() - 5) //5 = .java
                .replaceAll("\\\\", ".");

        return Collections.singletonMap(className, file);
    }

    private static void interpretClass(Map<String, File> fullModel, CtType ctType) {
        for(CtInvocation invocation : ctType.getElements(new TypeFilter<>(CtInvocation.class))) {
            if(RabbitMQInvocationMatcher.match(invocation)) {
                System.out.println(ctType.getQualifiedName());
                System.out.println("- Exchange name: " + RabbitMQInvocationMatcher.parseValue(fullModel, invocation));
                System.out.println();
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
