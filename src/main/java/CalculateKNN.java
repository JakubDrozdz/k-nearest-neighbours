import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;

public class CalculateKNN {
    public static void main(String[] args) throws IOException, CsvException {
        List<Integer> mainVector = List.of(1,0,4);
        CSVReader csvReader = new CSVReaderBuilder(new BufferedReader(new InputStreamReader(CalculateKNN.class.getClassLoader().getResourceAsStream("vectors.csv"), StandardCharsets.UTF_8)))
                .build();
        Map<String, List<List<Integer>>> classToVector = new HashMap<>();
        for (String[] row : csvReader.readAll()) {
            List<Integer> vector = new LinkedList<>();
            for (int i = 0; i < row.length - 1; i++) {
                vector.add(Integer.parseInt(row[i]));
            }
            classToVector.computeIfAbsent(row[row.length-1], k -> new ArrayList<>()).add(vector);
        }
        Map<Double,List<String>> nearestNeighbours = new HashMap<>();
        for (Map.Entry<String, List<List<Integer>>> entry: classToVector.entrySet()) {
            List<List<Integer>> value = entry.getValue();
            for (List<Integer> vector : value) {
                double sum = 0;
                for (int i = 0; i < vector.size(); i++) {
                    sum += Math.pow(mainVector.get(i) - vector.get(i),2);
                }
                nearestNeighbours.computeIfAbsent(Math.sqrt(sum), k -> new ArrayList<>()).add(entry.getKey());
            }
        }
        SortedSet<Double> keys = new TreeSet<>(Double::compareTo);
        keys.addAll(nearestNeighbours.keySet());
        List<List<String>> collect = keys.stream().limit(3).map(nearestNeighbours::get).collect(Collectors.toList());
        System.out.println(collect.stream().flatMap(List::stream).collect(Collectors.toMap(k->k, v -> 1, Integer::sum)).entrySet().stream().max(Map.Entry.comparingByValue()).orElseGet(null).getKey());
    }
}
