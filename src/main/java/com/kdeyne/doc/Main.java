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

    /**
     * Loads in a repo, then tries to find a few matchers
     * Proof of concept
     * @param args None
     * @throws Exception None
     */
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

    /**
     * This runs through all files recursively in the repo and builds a hashmap of it
     * It only contains references to what class is in what file
     * So we can do easy dynamic lookup later in the matchers
     * @param dir Base directory where all the classes will be
     * @throws IOException Should generally not happen unless you have permission issues
     */
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

    /**
     * Instead of using a Spoon Launcher here, we're doing it the faster way:
     * transforming an absolute path into what we assume the qualified name is
     * It's only for lookups so it doesn't need to be 100% accurate, it just needs to be fast
     * This is a long-lived map
     * @param file File to interpret the absolute path from
     * @return Map entry with classname + file reference
     */
    static Map<String, File> loadClassNameFromFile(File file) {
        final String fullFileName = file.getAbsolutePath();
        int startIndex = fullFileName.indexOf("src\\main") + 9; //9 = src/main
        String className = fullFileName
                .substring(startIndex, fullFileName.length() - 5) //5 = .java
                .replaceAll("\\\\", ".");

        return Collections.singletonMap(className, file);
    }

    /**
     * Runs the matchers on code
     * @param fileMap Map for lookups, gets passed to all matchers in case lookup is required
     * @param ctType logical model for the matcher to use
     */
    private static void interpretClass(Map<String, File> fileMap, CtType ctType) {
        for(CtInvocation invocation : ctType.getElements(new TypeFilter<>(CtInvocation.class))) {
            if(RabbitMQInvocationMatcher.match(invocation)) {
                System.out.println(ctType.getQualifiedName());
                System.out.println("- Exchange name: " + RabbitMQInvocationMatcher.parseValue(fileMap, invocation));
                System.out.println();
            }
        }
    }

    /**
     * Reads the file as a Spoon CtType logical model
     * This will allow us to easily parse through the classes
     * @param file File to read in
     * @return CtType logical model of the class. Usually only one class per file. But in case of inner classes, can be a list
     */
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
