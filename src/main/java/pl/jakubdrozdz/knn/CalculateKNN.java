package pl.jakubdrozdz.knn;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvException;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;

@Slf4j
public class CalculateKNN {
    public static void main(String[] args) throws IOException, CsvException {
        List<Double> mainVector = List.of(1d,0d,4d);
        Map<String, List<List<Double>>> classToVector;
        try (CSVReader csvReader = new CSVReaderBuilder(new BufferedReader(new InputStreamReader(Objects.requireNonNull(CalculateKNN.class.getClassLoader().getResourceAsStream("vectors.csv")), StandardCharsets.UTF_8)))
                .build()) {
            classToVector = new HashMap<>();
            for (String[] row : csvReader.readAll()) {
                List<Double> vector = new LinkedList<>();
                for (int i = 0; i < row.length - 1; i++) {
                    vector.add(Double.parseDouble(row[i]));
                }
                classToVector.computeIfAbsent(row[row.length - 1], k -> new ArrayList<>()).add(vector);
            }
        }
        Map<Double,List<String>> nearestNeighbours = new HashMap<>();
        classToVector.forEach((k,v) -> v.forEach(vector->{
            double sum = 0;
            for (int i = 0; i < vector.size(); i++) {
                sum += Math.pow(mainVector.get(i) - vector.get(i),2);
            }
            nearestNeighbours.computeIfAbsent(Math.sqrt(sum), e -> new ArrayList<>()).add(k);
        }));
        SortedSet<Double> keys = new TreeSet<>(Double::compareTo);
        keys.addAll(nearestNeighbours.keySet());
        List<List<String>> collect = keys.stream().limit(3).map(nearestNeighbours::get).collect(Collectors.toList());
        log.info("Associated class: {}",
                collect.stream().flatMap(List::stream).collect(Collectors.toMap(k->k, v -> 1, Integer::sum)).entrySet().stream().max(Map.Entry.comparingByValue()).orElseGet(() -> Map.entry("ERROR", 0)).getKey());
    }
}
