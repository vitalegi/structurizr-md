package it.vitalegi.structurizr.gen.service;

import com.structurizr.Workspace;
import com.structurizr.export.Diagram;
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

public class DiagramService {

    Logger log = LoggerFactory.getLogger(getClass());

    public Collection<Diagram> exportDiagrams(Workspace workspace) {
        C4PlantUMLExporter exporter = new C4PlantUMLExporter();
        workspace.getViews().getViews()
                 .forEach(v -> v.addProperty(C4PlantUMLExporter.C4PLANTUML_STANDARD_LIBRARY_PROPERTY, "true"));
        return exporter.export(workspace);
    }

    public void saveAsPng(String dir, Diagram diagram) {
        SourceStringReader reader = new SourceStringReader(diagram.getDefinition());

        File pngFile = new File(mkdir(dir), diagram.getKey() + ".png");
        log.info("Save diagram in " + pngFile);
        try (FileOutputStream fos = new FileOutputStream(pngFile)) {
            reader.outputImage(fos, new FileFormatOption(FileFormat.PNG, true));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void saveAsSvg(String dir, Diagram diagram) {
        SourceStringReader reader = new SourceStringReader(diagram.getDefinition());
        File pngFile = new File(mkdir(dir), diagram.getKey() + ".svg");
        log.info("Save diagram in " + pngFile);
        try (FileOutputStream fos = new FileOutputStream(pngFile)) {
            reader.outputImage(fos, new FileFormatOption(FileFormat.SVG, false));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    protected File mkdir(String dir) {
        var file = new File(dir);
        file.mkdirs();
        return file;
    }
}
