package it.vitalegi.structurizr.gen.util;

import com.structurizr.view.View;
import it.vitalegi.structurizr.gen.model.MdContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.file.Path;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MarkdownUtilTests {

    ByteArrayOutputStream os;
    MarkdownUtil md;

    @BeforeEach
    void init() {
        os = new ByteArrayOutputStream();
        md = new MarkdownUtil();
        md.os = new OutputStreamWriter(os);
    }

    @Test
    void test_addImage() throws IOException {
        md.addImage("a", Path.of("p1", "p2", "p3"));
        var out = output();
        assertEquals("![a](p1/p2/p3)\n\n", out);
    }

    String output() {
        try {
            md.os.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        var out = new String(os.toByteArray());
        return out;
    }

    @Test
    void test_addImage_spaces() throws IOException {
        md.addImage("A b c", Path.of("foo bar", "p2", "p3"));
        var out = output();
        assertEquals("![A b c](foo%20bar/p2/p3)\n\n", out);
    }

    @Test
    void test_addViewImages_withPng() throws IOException {

        var view = mock(View.class);
        when(view.getKey()).thenReturn("a");

        var ctx = mock(MdContext.class);
        when(ctx.getImageFormats("a")).thenReturn(Arrays.asList("png", "svg"));
        when(ctx.getImagePath("a", "png")).thenReturn(Path.of("a/b/foo bar.png"));
        when(ctx.getImagePath("a", "svg")).thenReturn(Path.of("a/foo bar/c.svg"));

        md.addViewImages(view, ctx, Path.of("..", "images"));
        var out = output();
        assertEquals("![a](../images/a/b/foo%20bar.png)\n\n[png](../images/a/b/foo%20bar.png) | [svg](." + "./images" +
                "/a/foo%20bar/c.svg)\n", out);
    }


    @Test
    void test_h1() {
        md.h1("Title 123");
        var out = output();
        assertEquals("# Title 123\n\n", out);
    }

    @Test
    void test_h2() {
        md.h2("Title 123");
        var out = output();
        assertEquals("## Title 123\n\n", out);
    }

    @Test
    void test_h3() {
        md.h3("Title 123");
        var out = output();
        assertEquals("### Title 123\n\n", out);
    }

    @Test
    void test_mdLink() {
        md.mdLink("a", Path.of("p1", "p2", "p3"));
        var out = output();
        assertEquals("[a](p1/p2/p3)", out);
    }
}
