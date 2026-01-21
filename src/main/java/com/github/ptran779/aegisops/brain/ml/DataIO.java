package com.github.ptran779.aegisops.brain.ml;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

//todo: add gameID just in case
public class DataIO {

  // Save all training data to a CSV readable by Excel
  public static void saveToCSV(String filepath, List<List<DataManager.itemUnit>> data) {
    if (data.isEmpty()) return;

    try (PrintWriter writer = new PrintWriter(new FileWriter(filepath))) {
      // 1. Write Header
      int inputSize = data.get(0).get(0).input.length;
      StringBuilder header = new StringBuilder("GameID,");
      for (int i = 0; i < inputSize; i++) header.append("In_").append(i).append(",");
      header.append("Action,Score");
      writer.println(header);

      // 2. Write Data
      for (int gameIdx = 0; gameIdx < data.size(); gameIdx++) {
        List<DataManager.itemUnit> game = data.get(gameIdx);
        for (DataManager.itemUnit unit : game) {
          StringBuilder line = new StringBuilder();
          line.append(gameIdx).append(","); // GameID

          // Inputs
          for (float val : unit.input) {
            line.append(val).append(",");
          }

          // Action & Score
          line.append(unit.action).append(",");
          line.append(unit.score);

          writer.println(line);
        }
      }
      System.out.println("Saved " + data.size() + " games to " + filepath);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  // Load CSV back into the jagged 3D structure
  public static List<List<DataManager.itemUnit>> loadFromCSV(String filepath) {
    List<List<DataManager.itemUnit>> allGames = new ArrayList<>();

    try (BufferedReader br = new BufferedReader(new FileReader(filepath))) {
      String line;
      br.readLine(); // Skip Header

      List<DataManager.itemUnit> currentGame = new ArrayList<>();
      int lastGameID = -1;

      while ((line = br.readLine()) != null) {
        String[] parts = line.split(",");
        if (parts.length < 3) continue;

        // Parse ID
        int gameID = Integer.parseInt(parts[0]);

        // Detect new game start
        if (gameID != lastGameID) {
          if (!currentGame.isEmpty()) {
            allGames.add(new ArrayList<>(currentGame));
            currentGame.clear();
          }
          lastGameID = gameID;
        }

        // Parse Unit
        DataManager.itemUnit unit = new DataManager.itemUnit();

        // Calculate input size: Total parts - (GameID + Action + Score)
        int inputLen = parts.length - 3;
        unit.input = new float[inputLen];

        for (int i = 0; i < inputLen; i++) {
          unit.input[i] = Float.parseFloat(parts[i+1]);
        }
        unit.action = Integer.parseInt(parts[parts.length - 2]);
        unit.score = Float.parseFloat(parts[parts.length - 1]);

        currentGame.add(unit);
      }

      // Add final game
      if (!currentGame.isEmpty()) {
        allGames.add(currentGame);
      }

    } catch (IOException e) {
      e.printStackTrace();
    }

    System.out.println("Loaded " + allGames.size() + " games.");
    return allGames;
  }
}