package com.efimchick.ifmo;

import com.efimchick.ifmo.util.CourseResult;
import com.efimchick.ifmo.util.Person;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;


public class Collecting {
    public static final int DIVISOR = 2;
    public static final String SCORE_A = "A";
    public static final String SCORE_B = "B";
    public static final String SCORE_C = "C";
    public static final String SCORE_D = "D";
    public static final String SCORE_E = "E";
    public static final String SCORE_F = "F";
    public static final int MIN_FOR_A = 90;
    public static final int MIN_FOR_B = 83;
    public static final int MIN_FOR_C = 75;
    public static final int MIN_FOR_D = 68;
    public static final int MIN_FOR_E = 60;
    public static final double EPSILON = 0.00001;

    public int sum(IntStream intStream) {
        return intStream.reduce(0, Integer::sum);
    }

    public int production(IntStream intStream) {
        return intStream.reduce(1, (num1, num2) -> num1 * num2);
    }

    public int oddSum(IntStream intStream) {
        return intStream.filter(number -> number % DIVISOR != 0)
                .reduce(0, Integer::sum);
    }

    public Map<Integer, Integer> sumByRemainder(int divisor, IntStream intStream) {
        return intStream.boxed()
                .collect(Collectors.toMap(key -> key % divisor, value -> value, Integer::sum));
    }

    public Map<Person, Double> totalScores(Stream<CourseResult> courseResultStream) {

        List<CourseResult> courseResultList = courseResultStream.collect(Collectors.toList());

        long countTasks = getCountTasks(courseResultList);

        return courseResultList.stream()
                .collect(Collectors.toMap(CourseResult::getPerson, value -> value.getTaskResults()
                        .values().stream()
                        .mapToDouble(score -> score)
                        .reduce(0, Double::sum) / countTasks));
    }

    private long getCountTasks(List<CourseResult> courseResultList) {
        return courseResultList.stream()
                .flatMap(course -> course.getTaskResults().entrySet().stream())
                .map(Map.Entry::getKey)
                .distinct()
                .count();
    }

    public double averageTotalScore(Stream<CourseResult> courseResultStream) {
        List<CourseResult> courseResultList = courseResultStream.collect(Collectors.toList());

        long countTasks = getCountTasks(courseResultList);

        return courseResultList.stream()
                .collect(Collectors.averagingDouble(course -> course.getTaskResults()
                        .values()
                        .stream()
                        .mapToDouble(score -> score)
                        .reduce(0, Double::sum) / countTasks));
    }

    public Map<String, Double> averageScoresPerTask(Stream<CourseResult> courseResultStream) {
        List<CourseResult> courseResultList = courseResultStream.collect(Collectors.toList());

        long countStudents = courseResultList.stream()
                .map(CourseResult::getPerson)
                .count();

        Map<String, List<Integer>> taskMap = courseResultList
                .stream().flatMap(course -> course.getTaskResults().entrySet().stream())
                .collect(Collectors.groupingBy(Map.Entry::getKey,
                        Collectors.mapping(Map.Entry::getValue, Collectors.toList())));

        return taskMap.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey,
                scoreList -> scoreList.getValue().stream()
                        .mapToDouble(score -> score).sum() / countStudents));
    }

    public Map<Person, String> defineMarks(Stream<CourseResult> courseResultStream) {
        List<CourseResult> courseResultList = courseResultStream.collect(Collectors.toList());
        Map<Person, String> result = new LinkedHashMap<>();

        Map<Person, Double> numericalScore = totalScores(courseResultList.stream());

        Map<Person, String> minScore = numericalScore.entrySet().stream()
                .flatMap((Map.Entry<Person, Double> student) -> {
                    result.put(student.getKey(), SCORE_A);
                    return Stream.of(student);
                })
                .filter(student -> student.getValue() < MIN_FOR_A)
                .flatMap((Map.Entry<Person, Double> student) -> {
                    result.put(student.getKey(), SCORE_B);
                    return Stream.of(student);
                })
                .filter(student -> student.getValue() < MIN_FOR_B)
                .flatMap((Map.Entry<Person, Double> student) -> {
                    result.put(student.getKey(), SCORE_C);
                    return Stream.of(student);
                })
                .filter(student -> student.getValue() < MIN_FOR_C)
                .flatMap((Map.Entry<Person, Double> student) -> {
                    result.put(student.getKey(), SCORE_D);
                    return Stream.of(student);
                })
                .filter(student -> student.getValue() < MIN_FOR_D)
                .flatMap((Map.Entry<Person, Double> student) -> {
                    result.put(student.getKey(), SCORE_E);
                    return Stream.of(student);
                })
                .filter(student -> student.getValue() < MIN_FOR_E)
                .collect(Collectors.toMap(Map.Entry::getKey, y -> SCORE_F));

        result.putAll(minScore);
        return result;
    }

    public String easiestTask(Stream<CourseResult> students) {
        List<CourseResult> courseResultList = students.collect(Collectors.toList());

        Map<String, Double> taskMap = averageScoresPerTask(courseResultList.stream());

        double maxTask = taskMap.values().stream()
                .mapToDouble(score -> score)
                .max().getAsDouble();

        return taskMap.entrySet().stream()
                .filter(task -> Math.abs(task.getValue() - maxTask) <= EPSILON)
                .map(Map.Entry::getKey)
                .collect(Collectors.joining());
    }
}
