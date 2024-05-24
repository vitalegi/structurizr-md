package it.vitalegi.structurizr.md.service;

import com.structurizr.Workspace;
import com.structurizr.export.Diagram;
import com.structurizr.export.IndentingWriter;
import com.structurizr.export.plantuml.C4PlantUMLExporter;
import com.structurizr.util.StringUtils;
import com.structurizr.view.ModelView;
import it.vitalegi.structurizr.md.util.FileUtil;
import net.sourceforge.plantuml.FileFormat;
import net.sourceforge.plantuml.FileFormatOption;
import net.sourceforge.plantuml.SourceStringReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.util.Collection;

public class C4PlantUmlExporter {

    public static final String PLANTUML_DEFINES_PROPERTY = "plantuml.defines";

    Logger log = LoggerFactory.getLogger(getClass());

    public void exportDiagramsC4Plant(Workspace workspace, Path out) {
        var diagrams = createC4PlantUmlDiagrams(workspace);
        diagrams.parallelStream().forEach(d -> exportDiagram(d, out));
    }

    protected Collection<Diagram> createC4PlantUmlDiagrams(Workspace ws) {
        C4PlantUMLExporter exporter = new C4PlantUMLExporter() {
            @Override
            protected void writeIncludes(ModelView view, IndentingWriter writer) {
                String[] defines = getViewOrViewSetProperty(view, PLANTUML_DEFINES_PROPERTY, "").split(",");
                for (String define : defines) {
                    if (!StringUtils.isNullOrEmpty(define)) {
                        define = define.trim();
                        writer.writeLine("!define " + define);
                    }
                }
                super.writeIncludes(view, writer);
            }
        };
        ws.getViews().getViews()
                .forEach(v -> v.addProperty(C4PlantUMLExporter.C4PLANTUML_STANDARD_LIBRARY_PROPERTY, "true"));
        return exporter.export(ws);
    }

    protected void exportDiagram(Diagram diagram, Path outDir) {
        log.info("Export diagram " + diagram.getKey());
        logDiagram(diagram);
        saveDiagram(outDir, diagram);
        saveAsSvg(outDir, diagram);
        saveAsPng(outDir, diagram);
    }

    protected void logDiagram(Diagram diagram) {
        log.debug("PlantUML diagram:\n{}", diagram.getDefinition());
    }

    protected void saveDiagram(Path outDir, Diagram diagram) {
        var filename = diagram.getKey() + ".plantuml";
        var out = outDir.resolve(filename).toFile();
        log.info("Save diagram as " + out);
        FileUtil.createDirs(outDir);
        try (var pw = new PrintWriter(out)) {
            pw.println(diagram.getDefinition());
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
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
