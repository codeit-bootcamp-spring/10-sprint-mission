package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentDto;
import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentResponse;
import com.sprint.mission.discodeit.entity.BinaryContent;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-05-22T16:33:34+0900",
    comments = "version: 1.5.5.Final, compiler: IncrementalProcessingEnvironment from gradle-language-java-8.14.4.jar, environment: Java 17.0.17 (Azul Systems, Inc.)"
)
@Component
public class BinaryContentMapperImpl implements BinaryContentMapper {

    @Override
    public BinaryContentResponse toResponse(BinaryContent binaryContent) {
        if ( binaryContent == null ) {
            return null;
        }

        UUID id = null;
        Instant createdAt = null;
        String fileName = null;
        long size = 0L;
        String contentType = null;

        id = binaryContent.getId();
        createdAt = binaryContent.getCreatedAt();
        fileName = binaryContent.getFileName();
        size = binaryContent.getSize();
        contentType = binaryContent.getContentType();

        BinaryContentResponse binaryContentResponse = new BinaryContentResponse( id, createdAt, fileName, size, contentType );

        return binaryContentResponse;
    }

    @Override
    public BinaryContentDto toDto(BinaryContent binaryContent) {
        if ( binaryContent == null ) {
            return null;
        }

        UUID id = null;
        String fileName = null;
        long size = 0L;
        String contentType = null;

        id = binaryContent.getId();
        fileName = binaryContent.getFileName();
        size = binaryContent.getSize();
        contentType = binaryContent.getContentType();

        BinaryContentDto binaryContentDto = new BinaryContentDto( id, fileName, size, contentType );

        return binaryContentDto;
    }

    @Override
    public List<BinaryContentDto> toDtoList(List<BinaryContent> binaryContents) {
        if ( binaryContents == null ) {
            return null;
        }

        List<BinaryContentDto> list = new ArrayList<BinaryContentDto>( binaryContents.size() );
        for ( BinaryContent binaryContent : binaryContents ) {
            list.add( toDto( binaryContent ) );
        }

        return list;
    }

    @Override
    public BinaryContentDto toDto(BinaryContentResponse response) {
        if ( response == null ) {
            return null;
        }

        UUID id = null;
        String fileName = null;
        long size = 0L;
        String contentType = null;

        id = response.id();
        fileName = response.fileName();
        size = response.size();
        contentType = response.contentType();

        BinaryContentDto binaryContentDto = new BinaryContentDto( id, fileName, size, contentType );

        return binaryContentDto;
    }
}
