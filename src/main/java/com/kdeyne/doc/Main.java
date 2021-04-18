package com.kdeyne.doc;

import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.api.Git;
import spoon.Launcher;
import spoon.reflect.CtModel;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.CtTypeInformation;
import spoon.reflect.visitor.filter.TypeFilter;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class Main {

    //Github setup: GitHub github = new GitHubBuilder().withOAuthToken("my_personal_token").build()

    //sample: https://github.com/vmudigal/microservices-sample

    private static final File TEMP_DIR = new File("D:\\workspace\\temp");
    private static final String SAMPLE_REPO = "https://github.com/vmudigal/microservices-sample.git";
    private static final boolean CLEAN = false;

    private static final Map<String, CtType> fullModel = new HashMap<>();

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

        for(CtType ctType : fullModel.values()) {
            interpretClass(fullModel, ctType);
        }
    }

    private static void traverseFiles(File dir) throws IOException {
        for(File file : Objects.requireNonNull(dir.listFiles())) {
            if(file.isDirectory()) {
                traverseFiles(file);
            }

            //load full model
            if(isSupportedFile(file)) {
                Map<String, CtType> classModel = loadClass(file);
                if(classModel != null && !classModel.isEmpty()) {
                    fullModel.putAll(classModel);
                }
            }
        }
    }

    private static Map<String, CtType> loadClass(File file) {
        List<CtType> ctTypes = getCtTypes(file);
        if(!ctTypes.isEmpty()) {
            return buildLookupModel(ctTypes);
        }
        return null;
    }

    private static void interpretClass(Map<String, CtType> fullModel, CtType ctType) {
        for(CtInvocation invocation : ctType.getElements(new TypeFilter<>(CtInvocation.class))) {
            if(RabbitMQInvocationMatcher.match(invocation)) {
                System.out.println(ctType.getQualifiedName());
                System.out.println("- Exchange name: " + RabbitMQInvocationMatcher.parseValue(fullModel, invocation));
                System.out.println();
            }
        }
    }

    static Map<String, CtType> buildLookupModel(List<CtType> ctTypes) {
        return ctTypes.stream().collect(Collectors.toMap(CtTypeInformation::getQualifiedName, c -> c, (a, b) -> b));
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
