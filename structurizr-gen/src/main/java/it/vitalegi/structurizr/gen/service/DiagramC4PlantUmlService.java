package it.vitalegi.structurizr.gen.service;

import com.structurizr.Workspace;
import com.structurizr.export.Diagram;
import com.structurizr.export.plantuml.C4PlantUMLExporter;
import it.vitalegi.structurizr.gen.util.FileUtil;
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

public class DiagramC4PlantUmlService {

    Logger log = LoggerFactory.getLogger(getClass());

    public Collection<Diagram> exportDiagramsC4Plant(Workspace workspace) {
        C4PlantUMLExporter exporter = new C4PlantUMLExporter();
        workspace.getViews().getViews()
                 .forEach(v -> v.addProperty(C4PlantUMLExporter.C4PLANTUML_STANDARD_LIBRARY_PROPERTY, "true"));
        return exporter.export(workspace);
    }

    public File saveAsPng(Path dir, Diagram diagram) {
        return saveAs(dir, diagram.getKey() + ".png", diagram.getDefinition(), FileFormat.PNG);
    }

    public File saveAsSvg(Path dir, Diagram diagram) {
        return saveAs(dir, diagram.getKey() + ".svg", diagram.getDefinition(), FileFormat.SVG);
    }

    protected File saveAs(Path dir, String fileName, String definition, FileFormat format) {
        SourceStringReader reader = new SourceStringReader(definition);
        FileUtil.createDirs(dir);
        File out = new File(dir.toFile(), fileName);
        log.info("Save diagram as " + out);
        try (FileOutputStream fos = new FileOutputStream(out)) {
            reader.outputImage(fos, new FileFormatOption(format, false));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return out;
    }
}
