package com.fitplanpro.repository;

import com.fitplanpro.entity.AIPromptTemplate;
import com.fitplanpro.enums.PromptTemplateType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AIPromptTemplateRepository extends JpaRepository<AIPromptTemplate, Long> {

    /**
     * Find active templates by type
     *
     * @param templateType the template type to filter by
     * @return a list of active templates
     */
    List<AIPromptTemplate> findByTemplateTypeAndIsActiveTrue(PromptTemplateType templateType);

    /**
     * Find the latest active template by type
     *
     * @param templateType the template type to filter by
     * @return an Optional containing the latest template if found
     */
    Optional<AIPromptTemplate> findTopByTemplateTypeAndIsActiveTrueOrderByVersionDesc(PromptTemplateType templateType);

    /**
     * Find template by name
     *
     * @param templateName the template name to search for
     * @return an Optional containing the template if found
     */
    Optional<AIPromptTemplate> findByTemplateName(String templateName);

    /**
     * Find all versions of a template by name
     *
     * @param templateName the template name to search for
     * @return a list of template versions
     */
    List<AIPromptTemplate> findByTemplateNameOrderByVersionDesc(String templateName);

    /**
     * Find templates by type and version
     *
     * @param templateType the template type to filter by
     * @param version the version to filter by
     * @return a list of matching templates
     */
    List<AIPromptTemplate> findByTemplateTypeAndVersion(PromptTemplateType templateType, String version);

    /**
     * Get template version history
     *
     * @return a list of template names and their versions
     */
    @Query("SELECT DISTINCT new map(t.templateName as name, t.version as version) " +
            "FROM AIPromptTemplate t ORDER BY t.templateName, t.version DESC")
    List<Object> getTemplateVersionHistory();

    /**
     * Count active templates by type
     *
     * @param templateType the template type to count
     * @return the count of templates
     */
    long countByTemplateTypeAndIsActiveTrue(PromptTemplateType templateType);
}