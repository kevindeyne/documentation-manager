package com.kdeyne.doc;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.Map;

class ClassToFileNameParserTest {

    @Test
    void testName() {
        String fullFileName = "D:\\workspace\\temp\\service-one\\src\\main\\com\\mudigal\\one\\service\\impl\\ServiceOneRabbitMessageProducer.java";
        Map<String, File> classMap = Main.loadClassNameFromFile(new File(fullFileName));
        Assertions.assertEquals("com.mudigal.one.service.impl.ServiceOneRabbitMessageProducer", classMap.keySet().stream().findFirst().get());
    }

}
