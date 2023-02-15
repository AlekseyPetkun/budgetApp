package com.example.budgetapp.services;

import java.io.File;
import java.nio.file.Path;

public interface FileService {
    boolean saveToFile(String json);

    String readFromFile();

    File getDataFile();

    Path createTempFile(String suffix);

    boolean cleanDataFile();
}
