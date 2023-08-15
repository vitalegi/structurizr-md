package it.vitalegi.structurizr.md;

import it.vitalegi.structurizr.md.model.MdContext;
import it.vitalegi.structurizr.md.util.FileUtil;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class GenerateMarkdownAppTests {

    @Test
    public void generateMarkdown_whenContainersExist_thenEachContainerHasDedicatedPage() {
        // setup
        final var ROOT = Path.of("target", "generateMarkdown_whenContainersExist_thenEachContainerHasDedicatedPage");
        var b = WorkspaceBuilder.init("containersFolders", "");
        var s1 = b.addSoftwareSystem("s1", 2, 2);
        var s2 = b.addSoftwareSystem("s2", 3, 1);
        b.connectComponents("s1", 0, 0, "s2", 1, 0);

        b.addComponentView("s1", 0, "view_s1_0_A", "First view of S1_0");
        b.addComponentView("s1", 0, "view_s1_0_B", "Second view of S1_0");
        b.addComponentView("s1", 1, "view_s1_1_A", "First view of S1_1");
        // execute
        execute(b, ROOT, false);

        // check
        assertExists(ROOT, "software-systems", "s1", b.getContainerName("s1", 0), "README.md");
        assertExists(ROOT, "software-systems", "s1", b.getContainerName("s1", 1), "README.md");
        assertExists(ROOT, "software-systems", "s2", b.getContainerName("s2", 0), "README.md");
        assertExists(ROOT, "software-systems", "s2", b.getContainerName("s2", 1), "README.md");
        assertExists(ROOT, "software-systems", "s2", b.getContainerName("s2", 2), "README.md");
    }

    protected void execute(WorkspaceBuilder b, Path root, boolean generateViews) {
        FileUtil.createDirs(root);
        var ctx = new MdContext(b.build(), root);
        var app = new GenerateMarkdownApp(ctx, generateViews);
        app.createImages();
        app.createMd();
    }

    private void assertExists(Path root, String... path) {
        var target = resolve(root, path);
        assertTrue(Files.exists(target), "File doesn't exist. " + target);
    }

    private Path resolve(Path from, String... path) {
        for (var p : path) {
            from = from.resolve(p);
        }
        return from;
    }

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
        assertExists(ROOT.resolve("images").resolve("view1.png"));
        assertExists(ROOT.resolve("images").resolve("view1.svg"));
    }

    private Path createTempDir(String prefix) {
        try {
            return Files.createTempDirectory(prefix);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean exists(Path from, String... path) {
        return Files.exists(resolve(from, path));
    }

}
