package ru.javawebinar.topjava.util;

import ru.javawebinar.topjava.model.Test;
import ru.javawebinar.topjava.model.UserMeal;
import ru.javawebinar.topjava.model.UserMealWithExcess;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class UserMealsUtil {
    public static void main(String[] args) {
        List<UserMeal> meals = Arrays.asList(
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 30, 10, 0), "Завтрак", 500),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 30, 13, 0), "Обед", 1000),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 30, 20, 0), "Ужин", 500),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 0, 0), "Еда на граничное значение", 100),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 10, 0), "Завтрак", 1000),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 13, 0), "Обед", 500),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 20, 0), "Ужин", 410)
        );

        List<UserMealWithExcess> mealsTo = filteredByStreams(meals, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000);
        mealsTo.forEach(System.out::println);

//        System.out.println(filteredByStreams(meals, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000));
    }

    public static List<UserMealWithExcess> filteredByCycles(List<UserMeal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        Map<LocalDate, Integer> map1 = new HashMap<>();
        for (UserMeal meal : meals) {
            if (map1.containsKey(meal.getDateTime().toLocalDate())) {
                map1.put(meal.getDateTime().toLocalDate(), map1.get(meal.getDateTime().toLocalDate()) + meal.getCalories());
            } else {
                map1.put(meal.getDateTime().toLocalDate(), meal.getCalories());
            }
        }

        Map<UserMeal, Integer> map2 = new HashMap<>();
        for (UserMeal meal : meals) {
            map2.put(meal, map1.get(meal.getDateTime().toLocalDate()));
        }

        List<UserMealWithExcess> list = map2.entrySet().stream().filter(p -> TimeUtil.isBetweenHalfOpen(p.getKey().getDateTime().toLocalTime(), startTime, endTime)).
                map(p -> new UserMealWithExcess(p.getKey().getDateTime(),
                        p.getKey().getDescription(),
                        p.getKey().getCalories(),
                        p.getValue() > caloriesPerDay)).
                collect(Collectors.toList());

        return list;
    }

    public static List<UserMealWithExcess> filteredByStreams(List<UserMeal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        Map<LocalDate, Integer> map1 = new HashMap<>();
        for (UserMeal meal : meals) {
            map1.put(meal.getDateTime().toLocalDate(), map1.getOrDefault(meal.getDateTime().toLocalDate(), 0) + meal.getCalories());
        }

        List<UserMealWithExcess> list = meals.stream().
                filter(p -> TimeUtil.isBetweenHalfOpen(p.getDateTime().toLocalTime(), startTime, endTime)).
                map(p -> new UserMealWithExcess(p.getDateTime(),
                        p.getDescription(),
                        p.getCalories(),
                        map1.get(p.getDateTime().toLocalDate()) > caloriesPerDay)).
                collect(Collectors.toList());

        return list;
    }
}
