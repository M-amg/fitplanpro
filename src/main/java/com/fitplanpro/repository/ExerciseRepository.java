package com.fitplanpro.repository;

import com.fitplanpro.entity.Exercise;
import com.fitplanpro.enums.ExerciseDifficulty;
import com.fitplanpro.enums.MuscleGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExerciseRepository extends JpaRepository<Exercise, Long> {

    /**
     * Find exercises by name (case-insensitive partial match)
     *
     * @param name the name to search for
     * @return a list of matching exercises
     */
    List<Exercise> findByNameContainingIgnoreCase(String name);

    /**
     * Find exercises by muscle group
     *
     * @param muscleGroup the muscle group to filter by
     * @return a list of matching exercises
     */
    List<Exercise> findByMuscleGroup(MuscleGroup muscleGroup);

    /**
     * Find exercises by difficulty
     *
     * @param difficulty the difficulty level to filter by
     * @return a list of matching exercises
     */
    List<Exercise> findByDifficulty(ExerciseDifficulty difficulty);

    /**
     * Find exercises by muscle group and difficulty
     *
     * @param muscleGroup the muscle group to filter by
     * @param difficulty the difficulty level to filter by
     * @return a list of matching exercises
     */
    List<Exercise> findByMuscleGroupAndDifficulty(MuscleGroup muscleGroup, ExerciseDifficulty difficulty);

    /**
     * Find exercises by equipment required (partial match)
     *
     * @param equipment the equipment to search for
     * @return a list of matching exercises
     */
    List<Exercise> findByEquipmentRequiredContainingIgnoreCase(String equipment);

    /**
     * Find exercises requiring no equipment
     *
     * @return a list of bodyweight exercises
     */
    @Query("SELECT e FROM Exercise e WHERE " +
            "LOWER(e.equipmentRequired) LIKE '%bodyweight%' OR " +
            "LOWER(e.equipmentRequired) LIKE '%none%' OR " +
            "LOWER(e.equipmentRequired) LIKE '%no equipment%'")
    List<Exercise> findBodyweightExercises();

    /**
     * Find exercises for specific muscle groups that match equipment availability
     *
     * @param muscleGroups the muscle groups to include
     * @param difficulties the maximum difficulty level
     * @return a list of suitable exercises
     */
    @Query("""
            SELECT e FROM Exercise e WHERE
            e.muscleGroup IN :muscleGroups AND
            e.difficulty IN :difficulties AND
            (LOWER(e.equipmentRequired) LIKE '%none%' OR
            LOWER(e.equipmentRequired) LIKE '%bodyweight%' OR
            LOWER(e.equipmentRequired) LIKE '%no equipment%')
            """)
    List<Exercise> findExercisesByMuscleGroupsAndNoEquipment(
            @Param("muscleGroups") List<MuscleGroup> muscleGroups,
            @Param("difficulties") List<ExerciseDifficulty> difficulties);

    /**
     * Find exercises for specific muscle groups that match equipment availability
     *
     * @param muscleGroups the muscle groups to include
     * @param equipmentList the available equipment items
     * @param difficulties the difficulty levels to include
     * @return a list of suitable exercises
     */
    @Query(value = """
            SELECT * FROM exercise_database e WHERE
            e.muscle_group IN :muscleGroups AND
            e.difficulty IN :difficulties AND
            (:equipmentList IS NULL OR
            e.equipment_required LIKE '%none%' OR
            e.equipment_required LIKE '%bodyweight%' OR
            e.equipment_required LIKE '%no equipment%' OR
            e.equipment_required SIMILAR TO CONCAT('%', REPLACE(:equipmentList, ',', '|'), '%'))
            """,
            nativeQuery = true)
    List<Exercise> findExercisesByMuscleGroupsAndEquipment(
            @Param("muscleGroups") List<String> muscleGroups,
            @Param("equipmentList") String equipmentList,
            @Param("difficulties") List<String> difficulties);

    /**
     * Count exercises by muscle group
     *
     * @param muscleGroup the muscle group to count
     * @return the count of exercises
     */
    long countByMuscleGroup(MuscleGroup muscleGroup);
}