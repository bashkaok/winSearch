package org.mikesoft.winsearch;

import java.nio.file.Path;
import java.util.List;

public interface SearchEngine {
    public List<Path> getFiles(String searchStr, boolean exactMatch);
}
