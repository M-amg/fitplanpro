package com.fitplanpro.mapper;

import com.fitplanpro.dto.aiservice.AIPromptTemplateCreateDto;
import com.fitplanpro.dto.aiservice.AIPromptTemplateDto;
import com.fitplanpro.entity.AIPromptTemplate;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AIPromptTemplateMapper {

    /**
     * Convert AIPromptTemplate entity to AIPromptTemplateDto
     *
     * @param template the entity to convert
     * @return the DTO
     */
    AIPromptTemplateDto toDto(AIPromptTemplate template);

    /**
     * Convert AIPromptTemplateCreateDto to AIPromptTemplate entity
     *
     * @param createDto the DTO to convert
     * @return the entity
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    AIPromptTemplate toEntity(AIPromptTemplateCreateDto createDto);

    /**
     * Update AIPromptTemplate entity with AIPromptTemplateCreateDto
     *
     * @param createDto the DTO with updates
     * @param template the entity to update
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntity(AIPromptTemplateCreateDto createDto, @MappingTarget AIPromptTemplate template);

    /**
     * Convert a list of AIPromptTemplate entities to a list of AIPromptTemplateDtos
     *
     * @param templates the list of entities
     * @return the list of DTOs
     */
    List<AIPromptTemplateDto> toDtoList(List<AIPromptTemplate> templates);

    /**
     * Before mapping, set the template version if not provided
     *
     * @param createDto the DTO to convert
     */
    @BeforeMapping
    default void setDefaultVersion(AIPromptTemplateCreateDto createDto) {
        if (createDto.getVersion() == null || createDto.getVersion().isEmpty()) {
            createDto.setVersion("1.0.0");
        }
    }

    /**
     * Before mapping, set the active flag if not provided
     *
     * @param createDto the DTO to convert
     */
    @BeforeMapping
    default void setDefaultActiveFlag(AIPromptTemplateCreateDto createDto) {
        if (createDto.getIsActive() == null) {
            createDto.setIsActive(true);
        }
    }
}