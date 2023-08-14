package it.vitalegi.structurizr.md;

import com.structurizr.Workspace;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class GenerateMarkdownAppTests {

    //@Test
    void test_generateMarkdown() {
        var app = new GenerateMarkdownApp(Path.of("sample.dsl"), Path.of("sample"), true);
        app.createMd();
    }

    @Test
    void test_generateMarkdown_100() {
        Workspace ws = new Workspace("name", "description");
        for (var s = 0; s < 30; s++) {
            var softwareSystem = ws.getModel().addSoftwareSystem("ss_" + s);
            for (var c = 0; c < 15; c++) {
                var container = softwareSystem.addContainer("ss_" + s + "_c_" + c);
                for (var c2 = 0; c2 < 10; c2++) {
                    var component = container.addComponent("ss_" + s + "_c_" + c + "_cc_" + c2);
                    container.getComponents().stream().filter(c3 -> !c3.equals(component))
                             .forEach(c3 -> c3.uses(component, "Relation " + ((int) (Math.random() * 1000))));

                    if (s > 0) {
                        var ss = ws.getModel().getSoftwareSystems().stream().findFirst().orElse(null);
                        var container2 = ss.getContainers().stream().findFirst().orElse(null);
                        var component2 = container2.getComponents().stream().findFirst().orElse(null);
                        component2.uses(component, "cross");
                    }
                }
            }
        }

        var app = new GenerateMarkdownApp(ws, tmpDir("sample10"));
        app = new GenerateMarkdownApp(ws, Path.of("sample10"));
        app.createMd();
    }

    Path tmpDir(String name) {
        try {
            var dir = Files.createTempDirectory(name);
            dir.toFile().deleteOnExit();
            return dir;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
