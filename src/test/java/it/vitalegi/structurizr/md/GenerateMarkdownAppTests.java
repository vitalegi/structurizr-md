package it.vitalegi.structurizr.md;

import java.nio.file.Path;

public class GenerateMarkdownAppTests {

    //@Test
    void test_generateMarkdown() {
        var app = new GenerateMarkdownApp(Path.of("sample.dsl"), Path.of("sample"), true);
        app.createImages();
        app.createMd();
    }
}
