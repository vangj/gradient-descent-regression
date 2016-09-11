package com.github.vangj.gdr.util;

import com.opencsv.CSVReader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FileUtil {
  private FileUtil() {

  }

  /**
   * Reads a CSV file and parses it as a double[][].
   * @param filePath Path to CSV file.
   * @return double[][].
   * @throws IOException Thrown if there is any IO problem.
   */
  public static double[][] read(String filePath) throws IOException {
    CSVReader reader = null;
    try {
      List<List<Double>> data = new ArrayList<List<Double>>();
      reader = new CSVReader(new FileReader(new File(filePath)));
      String[] line = null;
      int counter = 0;
      while(null != (line = reader.readNext())) {
        if(0 == counter) { //skip the headers
          counter++;
          continue;
        }

        List<Double> row = new ArrayList<Double>();
        for(String token : line) {
          row.add(Double.parseDouble(token.trim()));
        }
        data.add(row);
      }

      final int rows = data.size();
      final int cols = data.get(0).size();
      double[][] output = new double[rows][cols];
      for(int r=0; r < rows; r++) {
        for(int c=0; c < cols; c++) {
          output[r][c] = data.get(r).get(c);
        }
      }

      return output;
    } catch (IOException e) {
      throw e;
    } finally {
      if(null != reader) {
        try {
          reader.close();
          reader = null;
        } catch (Exception e) {

        }
      }
    }
  }
}
