/*
 * Provides a simple way to manipulate files
 */
package com.creapar.creativestone.util;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Celso
 */
public class FileStatement {

    BufferedReader reader;

    public FileStatement(String fileDescription) {
        try {
            this.reader = new BufferedReader(new FileReader(fileDescription));
        } catch (FileNotFoundException ex) {
            Logger.getLogger(FileStatement.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public String readLine() {
        String readed = null;
        try {
            readed = reader.readLine();
        } catch (IOException ex) {
            Logger.getLogger(FileStatement.class.getName()).log(Level.SEVERE, null, ex);
        }
        return readed;
    }

    public String[] readLine(String delimiter) {
        String[] splited = null;
        try {
            splited = reader.readLine().split(delimiter);
        } catch (IOException ex) {
            Logger.getLogger(FileStatement.class.getName()).log(Level.SEVERE, null, ex);
        }
        return splited;

    }

    public List<Double> readLineAsDouble(String delimiter) {
        List<Double> instanceData = null;
        String readedLine;
        String[] splited;
        try {
            readedLine = reader.readLine();
            if (readedLine != null) {
                instanceData = new ArrayList<>();
                splited = readedLine.split(delimiter);
                for (String data : splited) {
                    instanceData.add(Double.parseDouble(data));
                }
            }
        } catch (IOException | NullPointerException | NumberFormatException ex) {
            Logger.getLogger(FileStatement.class.getName()).log(Level.SEVERE, null, ex);
        }
        return instanceData;

    }

    public void close() {
        try {
            reader.close();
        } catch (IOException ex) {
            Logger.getLogger(FileStatement.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
