package ro.siit.logsys.converter;

import org.springframework.stereotype.Component;
import ro.siit.logsys.dto.DestinationDto;
import ro.siit.logsys.entity.DestinationEntity;

import java.util.ArrayList;
import java.util.List;

@Component
public class DestinationConverter {

    public DestinationEntity fromDtoToEntity(DestinationDto destinationDto) {
        DestinationEntity entity = new DestinationEntity();
        entity.setName(destinationDto.getName());
        entity.setDistance(destinationDto.getDistance());
        return entity;
    }

    public DestinationDto fromEntityToDto(DestinationEntity destinationEntity) {
        return DestinationDto.builder()
                .id(destinationEntity.getId())
                .name(destinationEntity.getName())
                .distance(destinationEntity.getDistance())
                .build();
    }

    public List<DestinationEntity> fromDtosToEntities(List<DestinationDto> dtos) {
        List<DestinationEntity> entities = new ArrayList<>();

        dtos.forEach(destinationDto -> {
            DestinationEntity entity = new DestinationEntity();
            entity.setName(destinationDto.getName());
            entity.setDistance(destinationDto.getDistance());
            entities.add(entity);
        });

        return entities;
    }

    public List<DestinationDto> fromEntitiesToDto(List<DestinationEntity> entities) {
        List<DestinationDto> dtos = new ArrayList<>();

        entities.forEach(destinationEntity -> {
            dtos.add(DestinationDto.builder()
                    .id(destinationEntity.getId())
                    .name(destinationEntity.getName())
                    .distance(destinationEntity.getDistance())
                    .build());
        });

        return dtos;
    }

}
