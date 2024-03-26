package com.depromeet.breadmapbackend.domain.review.tag;

import com.depromeet.breadmapbackend.domain.review.tag.dto.TagListResponse;
import com.depromeet.breadmapbackend.domain.review.tag.dto.TagResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ReviewTagServiceImpl implements ReviewTagService {

    private final BakeryTagRepository bakeryTagRepository;
    private final BreadTagRepository breadTagRepository;

    @Override
    public TagListResponse getAllReviewTags() {

        List<BakeryTag> bakeryTags = bakeryTagRepository.findAll();
        List<BreadTag> breadTags = breadTagRepository.findAll();

        List<TagResponse> BakeryTagResponses = bakeryTags.stream()
                .map(bakeryTag -> new TagResponse(
                        bakeryTag.getOrder(),
                        bakeryTag.getReviewTagType().name(),
                        bakeryTag.getDescription())).collect(Collectors.toList());

        List<TagResponse> BreadTagResponses = breadTags.stream()
                .map(breadTag -> new TagResponse(
                        breadTag.getOrder(),
                        breadTag.getReviewTagType().name(),
                        breadTag.getDescription())).collect(Collectors.toList());

        return new TagListResponse(BakeryTagResponses, BreadTagResponses);
    }
}
