package it.vitalegi.structurizr.gen.service;

import com.structurizr.Workspace;
import com.structurizr.export.Diagram;
import com.structurizr.export.dot.DOTExporter;
import com.structurizr.export.plantuml.C4PlantUMLExporter;
import net.sourceforge.plantuml.FileFormat;
import net.sourceforge.plantuml.FileFormatOption;
import net.sourceforge.plantuml.SourceStringReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collection;

public class DiagramC4PlantUmlService {

    Logger log = LoggerFactory.getLogger(getClass());

    public Collection<Diagram> exportDiagramsC4Plant(Workspace workspace) {
        C4PlantUMLExporter exporter = new C4PlantUMLExporter();
        workspace.getViews().getViews()
                 .forEach(v -> v.addProperty(C4PlantUMLExporter.C4PLANTUML_STANDARD_LIBRARY_PROPERTY, "true"));
        return exporter.export(workspace);
    }

    public Collection<Diagram> exportDiagramsDot(Workspace workspace) {
        var exporter = new DOTExporter();
        return exporter.export(workspace);
    }

    public File saveAsPng(String dir, Diagram diagram) {
        SourceStringReader reader = new SourceStringReader(diagram.getDefinition());

        File out = new File(mkdir(dir), diagram.getKey() + ".png");
        log.info("Save diagram as " + out);
        try (FileOutputStream fos = new FileOutputStream(out)) {
            reader.outputImage(fos, new FileFormatOption(FileFormat.PNG, true));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return out;
    }

    public File saveAsSvg(String dir, Diagram diagram) {
        SourceStringReader reader = new SourceStringReader(diagram.getDefinition());
        File out = new File(mkdir(dir), diagram.getKey() + ".svg");
        log.info("Save diagram as " + out);
        try (FileOutputStream fos = new FileOutputStream(out)) {
            reader.outputImage(fos, new FileFormatOption(FileFormat.SVG, false));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return out;
    }

    protected File mkdir(String dir) {
        var file = new File(dir);
        file.mkdirs();
        return file;
    }
}
