package com.efimchick.ifmo.io.filetree;

import java.io.File;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class FileTreeImpl implements FileTree {
    private static final String INDENT_NULL = "";
    private static final String INDENT_SMALL = " ";
    private static final String INDENT_LARGE = "  ";
    private static final String BOX_DRAWINGS_LIGHT_VERTICAL_SYM = "│";
    private static final String BOX_DRAWINGS_LIGHT_UP_AND_RIGHT_SYM = "└";
    private static final String BOX_DRAWINGS_LIGHT_HORIZONTAL_SYM = "─ ";
    private static final String BOX_DRAWINGS_LIGHT_VERTICAL_AND_RIGHT_SYM = "├";
    private static final String BYTES = "bytes";

    @Override
    public Optional<String> tree(Path path) {
        if (path == null || !path.toFile().exists()) {
            return Optional.empty();
        }

        File file = path.toFile();
        if (file.isFile()) {
            return Optional.of(getFileName(file));
        }

        return Optional.of(getTree(file, INDENT_NULL).tree);
    }

    private String getFileName(File file) {
        String fileName = file.getName();
        long fileSize = file.length();
        return getName(fileName, fileSize);
    }

    private String getName(String name, long size) {
        return name + INDENT_SMALL + size + INDENT_SMALL + BYTES;
    }

    private TreeResult getTree(File directory, String indent) {
        List<File> files = Arrays.asList(Objects.requireNonNull(directory.listFiles()));

        files.sort((File a, File b) -> {
            int result = Boolean.compare(a.isFile(), b.isFile());

            if (result == 0) {
                result = a.getName().compareToIgnoreCase(b.getName());
            }
            return result;
        });

        long size = 0;
        StringBuilder treeStringBuilder = new StringBuilder();

        for (File f : files) {
            treeStringBuilder.append("\n");
            treeStringBuilder.append(indent);

            StringBuilder indentStringBuilder = new StringBuilder(indent);

            boolean isLast = f.equals(files.get(files.size() - 1));
            if (isLast) {
                indentStringBuilder.append(INDENT_SMALL);
            } else {
                indentStringBuilder.append(BOX_DRAWINGS_LIGHT_VERTICAL_SYM);
            }

            indentStringBuilder.append(INDENT_LARGE);

            if (isLast) {
                treeStringBuilder.append(BOX_DRAWINGS_LIGHT_UP_AND_RIGHT_SYM);
            } else {
                treeStringBuilder.append(BOX_DRAWINGS_LIGHT_VERTICAL_AND_RIGHT_SYM);
            }

            treeStringBuilder.append(BOX_DRAWINGS_LIGHT_HORIZONTAL_SYM);

            if (f.isFile()) {
                size += f.length();
                treeStringBuilder.append(getFileName(f));
            } else {
                TreeResult treeResult = getTree(f, indentStringBuilder.toString());
                size += treeResult.size;
                treeStringBuilder.append(treeResult.tree);
            }
        }

        String name = directory.getName();

        return new TreeResult(getName(name, size) + treeStringBuilder, size);
    }

    private static class TreeResult {
        private final String tree;
        private final long size;

        public TreeResult(String tree, long size) {
            this.tree = tree;
            this.size = size;
        }
    }
}
