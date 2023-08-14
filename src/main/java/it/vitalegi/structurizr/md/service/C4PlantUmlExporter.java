package it.vitalegi.structurizr.md.service;

import com.structurizr.Workspace;
import com.structurizr.export.Diagram;
import com.structurizr.export.plantuml.C4PlantUMLExporter;
import it.vitalegi.structurizr.md.util.FileUtil;
import net.sourceforge.plantuml.FileFormat;
import net.sourceforge.plantuml.FileFormatOption;
import net.sourceforge.plantuml.SourceStringReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Collection;

public class C4PlantUmlExporter {

    Logger log = LoggerFactory.getLogger(getClass());

    public void exportDiagramsC4Plant(Workspace workspace, Path out) {
        var diagrams = createC4PlantUmlDiagrams(workspace);
        diagrams.parallelStream().forEach(d -> exportDiagram(d, out));
    }

    protected Collection<Diagram> createC4PlantUmlDiagrams(Workspace ws) {
        C4PlantUMLExporter exporter = new C4PlantUMLExporter();
        ws.getViews().getViews()
          .forEach(v -> v.addProperty(C4PlantUMLExporter.C4PLANTUML_STANDARD_LIBRARY_PROPERTY, "true"));
        return exporter.export(ws);
    }

    protected void exportDiagram(Diagram diagram, Path outDir) {
        log.info("Export diagram " + diagram.getKey());
        saveAsSvg(outDir, diagram);
        saveAsPng(outDir, diagram);
    }

    protected File saveAsSvg(Path outDir, Diagram diagram) {
        return saveAs(outDir, diagram.getKey() + ".svg", diagram.getDefinition(), FileFormat.SVG);
    }

    protected File saveAsPng(Path outDir, Diagram diagram) {
        return saveAs(outDir, diagram.getKey() + ".png", diagram.getDefinition(), FileFormat.PNG);
    }

    protected File saveAs(Path outDir, String fileName, String definition, FileFormat format) {
        SourceStringReader reader = new SourceStringReader(definition);
        FileUtil.createDirs(outDir);
        File out = new File(outDir.toFile(), fileName);
        log.info("Save diagram as " + out);
        try (FileOutputStream fos = new FileOutputStream(out)) {
            reader.outputImage(fos, new FileFormatOption(format, false));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return out;
    }
}
