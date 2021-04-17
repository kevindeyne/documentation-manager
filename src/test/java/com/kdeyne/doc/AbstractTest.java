package com.kdeyne.doc;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

class AbstractTest {

    protected File getFile(String path) {
        return new File(getClass().getClassLoader().getResource(path).getFile());
    }

    protected String readFile(String path) {
        try {
            File file = new File(getClass().getClassLoader().getResource(path).getFile());
            return Files.readString(file.toPath());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
