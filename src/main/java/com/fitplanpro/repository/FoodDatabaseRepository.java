package com.fitplanpro.repository;

import com.fitplanpro.entity.FoodDatabase;
import com.fitplanpro.enums.FoodGroup;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FoodDatabaseRepository extends JpaRepository<FoodDatabase, Long> {

    /**
     * Find foods by name (case-insensitive partial match)
     *
     * @param name the name to search for
     * @return a list of matching foods
     */
    List<FoodDatabase> findByNameContainingIgnoreCase(String name);

    /**
     * Find foods by culture/region
     *
     * @param cultureRegion the culture/region to search for
     * @return a list of matching foods
     */
    List<FoodDatabase> findByCultureRegion(String cultureRegion);

    /**
     * Find foods by culture/region and food group
     *
     * @param cultureRegion the culture/region to search for
     * @param foodGroup the food group to filter by
     * @return a list of matching foods
     */
    List<FoodDatabase> findByCultureRegionAndFoodGroup(String cultureRegion, FoodGroup foodGroup);

    /**
     * Find traditional foods by culture/region
     *
     * @param cultureRegion the culture/region to search for
     * @return a list of traditional foods
     */
    List<FoodDatabase> findByCultureRegionAndIsTraditionalTrue(String cultureRegion);

    /**
     * Search foods by name and culture/region
     *
     * @param name the name to search for
     * @param cultureRegion the culture/region to search for
     * @param pageable pagination information
     * @return a page of matching foods
     */
    Page<FoodDatabase> findByNameContainingIgnoreCaseAndCultureRegion(
            String name, String cultureRegion, Pageable pageable);

    /**
     * Find foods within a calorie range
     *
     * @param minCalories the minimum calories per 100g
     * @param maxCalories the maximum calories per 100g
     * @return a list of matching foods
     */
    List<FoodDatabase> findByCaloriesPer100gBetween(float minCalories, float maxCalories);

    /**
     * Find high protein foods in a specific culture/region
     *
     * @param cultureRegion the culture/region to search for
     * @param minProtein the minimum protein content per 100g
     * @return a list of high protein foods
     */
    @Query("SELECT f FROM FoodDatabase f WHERE f.cultureRegion = :cultureRegion " +
            "AND f.proteinPer100g >= :minProtein " +
            "ORDER BY f.proteinPer100g DESC")
    List<FoodDatabase> findHighProteinFoodsByCulture(
            @Param("cultureRegion") String cultureRegion,
            @Param("minProtein") float minProtein);

    /**
     * Find foods suitable for a specific diet type
     *
     * @param dietType the diet type (e.g., "KETO", "VEGAN")
     * @param cultureRegion the culture/region to search for
     * @return a list of suitable foods
     */
    @Query(value = "SELECT * FROM food_database " +
            "WHERE culture_region = :cultureRegion " +
            "AND CASE " +
            "  WHEN :dietType = 'KETO' THEN carbs_per_100g <= 5 AND fat_per_100g >= 15 " +
            "  WHEN :dietType = 'VEGAN' THEN food_group NOT IN ('DAIRY', 'PROTEIN') OR name NOT LIKE '%meat%' " +
            "  WHEN :dietType = 'VEGETARIAN' THEN food_group != 'PROTEIN' OR name NOT LIKE '%meat%' " +
            "  ELSE TRUE " +
            "END", nativeQuery = true)
    List<FoodDatabase> findFoodsSuitableForDiet(
            @Param("dietType") String dietType,
            @Param("cultureRegion") String cultureRegion);

    /**
     * Count foods by culture/region
     *
     * @param cultureRegion the culture/region to count
     * @return the count of foods
     */
    long countByCultureRegion(String cultureRegion);
}