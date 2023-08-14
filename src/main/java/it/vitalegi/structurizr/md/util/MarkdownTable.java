package it.vitalegi.structurizr.md.util;

import java.util.ArrayList;
import java.util.List;

public class MarkdownTable {
    MarkdownUtil md;
    private final String[] headers;
    private final List<Object[]> values;

    public MarkdownTable(MarkdownUtil md, String... headers) {
        this.md = md;
        this.headers = headers;
        values = new ArrayList<>();
    }

    public MarkdownTable addRow(Object... row) {
        if (row.length != headers.length) {
            throw new IllegalArgumentException("Expected " + headers.length + " elements, got " + row.length);
        }
        values.add(row);
        return this;
    }

    public MarkdownUtil build() {
        th(headers);
        values.forEach(this::td);
        return md;
    }

    private void th(String[] headers) {
        md.print("|");
        for (var header : headers) {
            md.print(" " + header + " |");
        }
        md.println();
        md.print("|");
        for (var i = 0; i < headers.length; i++) {
            md.print(" --- |");
        }
        md.println();
    }

    private void td(Object... values) {
        md.print("|");
        for (var value : values) {
            String printable = "";
            if (value != null) {
                printable = value.toString();
            }
            md.print(" " + printable + " |");
        }
        md.println();
    }
}
