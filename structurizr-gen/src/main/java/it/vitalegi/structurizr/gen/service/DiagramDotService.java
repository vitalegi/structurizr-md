package it.vitalegi.structurizr.gen.service;

import com.structurizr.Workspace;
import com.structurizr.export.Diagram;
import com.structurizr.export.dot.DOTExporter;
import it.vitalegi.structurizr.gen.constant.DotFormat;
import it.vitalegi.structurizr.gen.util.FileUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.util.Collection;

public class DiagramDotService {

    final static Path DOT_PATH = Paths.get("C:/a/software/graphviz-8.1.0-win32/Graphviz/bin/dot");

    Logger log = LoggerFactory.getLogger(getClass());

    public Collection<Diagram> exportDiagrams(Workspace workspace) {
        var exporter = new DOTExporter();
        return exporter.export(workspace);
    }

    public Path saveAsPng(Path dir, Diagram diagram) {
        return save(dir, diagram, DotFormat.PNG);
    }

    public Path saveAsSvg(Path dir, Diagram diagram) {
        return save(dir, diagram, DotFormat.SVG);
    }

    protected Path save(Path dir, Diagram diagram, DotFormat format) {
        var tmpDir = Path.of("tmp");
        FileUtil.createDirs(tmpDir);
        FileUtil.createDirs(dir);
        var name = diagram.getKey();
        var tmp = tmpDir.resolve(name + ".dot");
        log.info("Temporary file: {}", tmp.toAbsolutePath());
        log.info("Final file:     {}", name);
        FileUtil.writeFile(tmp, diagram.getDefinition());
        Path out;
        try {
            out = graphviz(tmpDir, dir, name, format);
        } catch (InterruptedException | IOException e) {
            throw new RuntimeException(e);
        }
        return out;
    }

    /**
     * Run:
     * <p>
     * dot -Tpng filename.dot -o filename.png
     */
    private Path graphviz(Path inDir, Path outDir, String name, DotFormat format) throws InterruptedException,
            IOException {
        Path in = inDir.resolve(name + ".dot");
        Path out = outDir.resolve(name + "." + format.getFileExtension());
        final String command = MessageFormat.format("{0} -T{1} {2} -o {3}", DOT_PATH, format.getCmd(), in,
                out.toString());
        log.info("Execute: {}", command);
        final Process p = Runtime.getRuntime().exec(command);
        final BufferedReader reader = new BufferedReader(new InputStreamReader(p.getErrorStream()));

        final String newLine = String.format("%n");
        final StringBuilder errorMessage = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            errorMessage.append(newLine);
            errorMessage.append(line);
        }
        int result = p.waitFor();
        if (result != 0) {
            throw new RuntimeException("Errors running Graphviz on " + command + ": " + errorMessage);
        }
        return out;
    }
}
