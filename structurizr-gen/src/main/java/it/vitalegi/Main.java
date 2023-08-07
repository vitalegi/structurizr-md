package it.vitalegi;

import com.structurizr.Workspace;
import com.structurizr.dsl.StructurizrDslParser;
import com.structurizr.export.Diagram;
import com.structurizr.export.plantuml.C4PlantUMLExporter;
import net.sourceforge.plantuml.FileFormat;
import net.sourceforge.plantuml.FileFormatOption;
import net.sourceforge.plantuml.SourceStringReader;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collection;

public class Main {

    static File EXPORT_IMG = new File("export");

    public static void main(String[] args) throws Exception {
        EXPORT_IMG.mkdirs();
        StructurizrDslParser parser = new StructurizrDslParser();
        parser.parse(new File("sample.dsl"));
        Workspace workspace = parser.getWorkspace();
        Collection<Diagram> diagrams = exportDiagrams(workspace);
        diagrams.forEach(Main::process);
    }

    static Collection<Diagram> exportDiagrams(Workspace workspace) {
        C4PlantUMLExporter exporter = new C4PlantUMLExporter();
        workspace.getViews().getViews().forEach(v -> v.addProperty(C4PlantUMLExporter.C4PLANTUML_STANDARD_LIBRARY_PROPERTY, "true"));
        return exporter.export(workspace);
    }

    static void process(Diagram diagram) {
        System.out.println("Export diagram " + diagram.getKey());
        saveAsSvg(diagram);
        saveAsPng(diagram);
    }

    static void saveAsPng(Diagram diagram) {
        //String definition = _replaceInclude(diagram);
        SourceStringReader reader = new SourceStringReader(diagram.getDefinition());
        File pngFile = new File(EXPORT_IMG, diagram.getKey() + ".png");
        System.out.println("Save diagram in " + pngFile);
        try (FileOutputStream fos = new FileOutputStream(pngFile)) {
            reader.outputImage(fos);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    static void saveAsSvg(Diagram diagram) {
        SourceStringReader reader = new SourceStringReader(diagram.getDefinition());
        File pngFile = new File(EXPORT_IMG, diagram.getKey() + ".svg");
        System.out.println("Save diagram in " + pngFile);
        try (FileOutputStream fos = new FileOutputStream(pngFile)) {
            reader.outputImage(fos, new FileFormatOption(FileFormat.SVG, false));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}