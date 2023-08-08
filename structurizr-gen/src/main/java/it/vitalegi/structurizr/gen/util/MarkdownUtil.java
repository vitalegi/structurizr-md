package it.vitalegi.structurizr.gen.util;

import java.io.Closeable;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

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

    public MarkdownUtil h2(String text) {
        write("## " + text + "\n\n");
        return this;
    }

    public MarkdownUtil h3(String text) {
        write("### " + text + "\n\n");
        return this;
    }

    public MarkdownUtil image(String name, String url) {
        write("![" + name + "](" + url + ")");
        return this;
    }

    public MarkdownUtil link(String url) {
        write("<" + url + ">");
        write("<a href=\""+url+"\">test</a>");
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

    protected void write(String text) {
        try {
            os.write(text);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
