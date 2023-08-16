package it.vitalegi.structurizr.md;

import it.vitalegi.structurizr.md.model.MdContext;
import it.vitalegi.structurizr.md.util.FileUtil;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class GenerateMarkdownAppTests {
    Logger log = LoggerFactory.getLogger(getClass());

    @Test
    public void generateMarkdown_linksAreNotDead() {
        // setup
        final var ROOT = Path.of("target", "generateMarkdown_linksAreNotDead");
        var b = WorkspaceBuilder.init("containersFolders", "");
        var s1 = b.addSoftwareSystem("s1", 2, 2);
        var s2 = b.addSoftwareSystem("s2", 3, 2);
        b.connectComponents("s1", 0, 0, "s2", 1, 0);
        b.connectComponents("s1", 0, 0, "s2", 1, 1);

        b.addComponentView("s1", 0, "view_s1_0_A", "First view of S1_0");
        b.addComponentView("s1", 0, "view_s1_0_B", "Second view of S1_0");
        b.addComponentView("s1", 1, "view_s1_1_A", "First view of S1_1");
        // execute
        execute(b, ROOT, true);

        // check
        assertLinkNotDead(ROOT, "software-systems", "s1", b.getContainerName("s1", 0), "README.md");
        assertLinkNotDead(ROOT, "software-systems", "s1", b.getContainerName("s1", 1), "README.md");
        assertLinkNotDead(ROOT, "software-systems", "s2", b.getContainerName("s2", 0), "README.md");
        assertLinkNotDead(ROOT, "software-systems", "s2", b.getContainerName("s2", 1), "README.md");
        assertLinkNotDead(ROOT, "software-systems", "s2", b.getContainerName("s2", 2), "README.md");
    }

    protected void execute(WorkspaceBuilder b, Path root, boolean generateViews) {
        FileUtil.createDirs(root);
        var ctx = new MdContext(b.build(), root);
        var app = new GenerateMarkdownApp(ctx, generateViews);
        app.createImages();
        app.createMd();
    }

    protected void assertLinkNotDead(Path root, String... path) {
        assertExists(root, path);
        var target = resolve(root, path);
        var targetDir = target.getParent();
        var content = readLines(target);
        log.info("Scan file {} ", target);
        for (var lineIndex = 0; lineIndex < content.size(); lineIndex++) {
            var col = 0;
            var line = content.get(lineIndex);
            while (col < line.length()) {
                var startLinkName = line.indexOf('[', col);
                if (startLinkName == -1) {
                    break;
                }
                var endLinkName = line.indexOf(']', startLinkName + 1);
                if (endLinkName == -1) {
                    break;
                }
                if (line.charAt(endLinkName + 1) != '(') {
                    col = endLinkName + 1;
                    continue;
                }
                var startLinkValue = endLinkName + 1;
                var endLinkValue = line.indexOf(')', startLinkValue + 1);
                if (endLinkValue == -1) {
                    break;
                }
                var name = line.substring(startLinkName + 1, endLinkName);
                var value = line.substring(startLinkValue + 1, endLinkValue);
                col = endLinkValue + 1;
                log.info("Line {}, found {}=>{}, full row {}", lineIndex, name, value, line);
                var relativePath = value.replace("%20", " ");
                assertTrue(Files.exists(targetDir.resolve(relativePath)),
                        "File " + target + " contains a dead link: [" + name + "](" + value + "), on line " + lineIndex);
            }
        }
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

    protected List<String> readLines(Path path) {
        try {
            return Files.readAllLines(path);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

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

    int max(int... values) {
        if (values.length == 0) {
            throw new IllegalArgumentException("Array is empty");
        }
        var max = values[0];
        for (var v : values) {
            max = Math.max(max, v);
        }
        return max;
    }

}
