package it.vitalegi.structurizr.md.util;

import com.structurizr.view.View;
import it.vitalegi.structurizr.md.model.MdContext;

import java.io.Closeable;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Arrays;

public class MarkdownUtil implements Closeable {
    protected OutputStreamWriter os;

    public static MarkdownUtil init(Path filePath) {
        var markdownUtil = new MarkdownUtil();
        markdownUtil.os = outputStream(filePath);
        return markdownUtil;
    }

    protected static OutputStreamWriter outputStream(Path filePath) {
        try {
            return new OutputStreamWriter(new FileOutputStream(filePath.toFile()), StandardCharsets.UTF_8);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public MarkdownUtil addImage(String key, Path path) {
        image(key, path);
        println();
        println();
        return this;
    }

    public MarkdownUtil addViewImages(View view, MdContext ctx, Path relativePath) {
        String key = view.getKey();
        var formats = ctx.getImageFormats(key);
        if (formats.isEmpty()) {
            throw new RuntimeException("no image for view " + key + " found");
        }

        var pngPath = ctx.getImagePath(key, "png");
        if (pngPath == null) {
            throw new RuntimeException("png image for " + key + " view not found");
        }
        addImage(key, relativePath.resolve(pngPath));
        for (var i = 0; i < formats.size(); i++) {
            var format = formats.get(i);
            if (i > 0) {
                print(" | ");
            }
            mdLink(formats.get(i), relativePath.resolve(ctx.getImagePath(key, format)));
        }
        println();
        println();
        return this;
    }

    @Override
    public void close() throws IOException {
        if (os != null) {
            os.close();
        }
    }

    public MarkdownUtil h1(String text) {
        write("# " + text + "\n\n");
        return this;
    }

    protected void write(String text) {
        try {
            os.write(text);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public MarkdownUtil h2(String text) {
        write("## " + text + "\n\n");
        return this;
    }

    public MarkdownUtil h3(String text) {
        write("### " + text + "\n\n");
        return this;
    }

    public MarkdownUtil image(String name, Path path) {
        name = sanitizeUrlName(name);
        var url = sanitizeUrlPath(path);
        write("![" + name + "](" + url + ")");
        return this;
    }

    public MarkdownUtil mdLink(String name, Path path) {
        name = sanitizeUrlName(name);
        var url = sanitizeUrlPath(path);
        write("[" + name + "](" + url + ")");
        return this;
    }

    public MarkdownUtil print(String text) {
        write(text);
        return this;
    }

    public MarkdownUtil println(String text) {
        write(text + "\n");
        return this;
    }

    public MarkdownUtil println() {
        write("\n");
        return this;
    }

    public MarkdownTable table(String... headers) {
        return new MarkdownTable(this, headers);
    }

    private String sanitizeUrlName(String name) {
        var forbidden = Arrays.asList("(", ")", "[", "]", "$");
        for (var i = 0; i < forbidden.size(); i++) {
            name = name.replace(forbidden.get(i), " ");
        }
        return name;
    }

    private String sanitizeUrlPath(Path path) {
        return path.toString().replace("\\", "/").replace(" ", "%20");
    }

}