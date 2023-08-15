package it.vitalegi.structurizr.md;

import it.vitalegi.structurizr.md.model.MdContext;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class GenerateMarkdownAppTests {

    @Test
    public void generateMarkdown_whenDeploymentViewWithStarTarget_thenImagesAreGenerated() {
        // setup
        final var ROOT = Path.of("target", "generateMarkdown_whenDeploymentViewWithStarTarget_thenWorks");
        var b = WorkspaceBuilder.init("deploymentViewWithStarTarget", "Deployment view with * target should work");
        var s1 = b.addSoftwareSystem("s1", 3, 5);
        var s2 = b.addSoftwareSystem("s2", 2, 5);
        b.connectComponents("s1", 0, 0, "s2", 1, 0);
        var node1 = b.addDeploymentNode("prod", "node1");
        b.addContainer(node1, b.findContainer("s1", 1));
        var node2 = b.addDeploymentNode("prod", "node2");
        b.addContainer(node2, b.findContainer("s2", 1));
        b.addDeploymentView("view1");

        // execute
        execute(b, ROOT, false);

        // check
        assertTrue(Files.exists(ROOT.resolve("images").resolve("view1.png")));
        assertTrue(Files.exists(ROOT.resolve("images").resolve("view1.svg")));
    }

    protected void execute(WorkspaceBuilder b, Path root, boolean generateViews) {
        var ctx = new MdContext(b.build(), root);
        var app = new GenerateMarkdownApp(ctx, generateViews);
        app.createImages();
        app.createMd();
    }

    private Path createTempDir(String prefix) {
        try {
            return Files.createTempDirectory(prefix);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
