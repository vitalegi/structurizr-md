package it.vitalegi.structurizr.gen.model;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class AssetContext {

    private List<Asset> assets = new ArrayList<>();

    public void addImage(String key, String format, Path path) {
        var asset = new Asset(key, format, path);
        assets.add(asset);
    }

    public List<String> getFormats(String key) {
        return assets.stream().filter(a -> Objects.equals(a.getKey(), key))
                     .map(Asset::getFormat).collect(Collectors.toList());

    }

    public Path getPath(String key, String format) {
        return assets.stream().filter(a -> Objects.equals(a.getKey(), key))
                     .filter(a -> Objects.equals(a.getFormat(), format)).map(Asset::getPath).findFirst().orElse(null);
    }

    private static class Asset {
        String key;
        String format;
        Path path;

        public Asset(String key, String format, Path path) {
            this.key = key;
            this.format = format;
            this.path = path;
        }

        public String getFormat() {
            return format;
        }

        public String getKey() {
            return key;
        }

        public Path getPath() {
            return path;
        }
    }
}
