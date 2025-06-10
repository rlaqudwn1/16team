package com.example.backend.domain.news.service;

import com.example.backend.domain.news.entity.News;
import com.example.backend.domain.news.entity.NewsRequestDto;
import com.example.backend.domain.news.repository.NewsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class NewsService {

    private final NewsRepository newsRepository;

    public News save(NewsRequestDto dto) {
        if (!newsRepository.existsByLink(dto.getLink())) {
            News news = News.builder()
                    .title(dto.getTitle())
                    .link(dto.getLink())
                    .source(dto.getSource())
                    .publishedAt(dto.getPublishedAt())
                    .snippet(dto.getSnippet())
                    .content(dto.getContent())
                    .gptSummary(dto.getGptSummary())
                    .thumbnail(dto.getThumbnail())
                    .build();

            return newsRepository.save(news);
        }
        return null; // 중복일 경우 저장하지 않음
    }

    public void saveAllIndividually(List<NewsRequestDto> dtos) {
        for (NewsRequestDto dto : dtos) {
            try {
                // ✅ 본문 길이 제한 (1000자 이내)
                String truncatedContent = dto.getContent();
                if (truncatedContent.length() > 1000) {
                    truncatedContent = truncatedContent.substring(0, 1000);
                }

                // ✅ GPT 요약
                String summary = summarizeWithGpt(truncatedContent);
                dto.setGptSummary(summary);

                // ✅ 저장
                save(dto);

                // ✅ 속도 제한 (1초 간격)
                Thread.sleep(1000);

            } catch (Exception e) {
                log.error("뉴스 저장 실패: {}", dto.getTitle(), e);
            }
        }
    }



    public List<News> saveAll(List<NewsRequestDto> dtos) {
        List<News> savedNewsList = new ArrayList<>();

        log.info("뉴스 저장 요청 수신: 총 {}건", dtos.size());

        for (NewsRequestDto dto : dtos) {
            try {
                String content = dto.getContent();

                if (content == null || content.isBlank()) {
                    log.warn("본문이 비어 있어 저장하지 않음. 제목: {}", dto.getTitle());
                    continue;
                }

                if (content.length() > 1000) {
                    log.info("본문 1000자 초과. 제목: {} → 잘라서 처리", dto.getTitle());
                    content = content.substring(0, 1000);
                }

                String summary = summarizeWithGpt(content);
                log.info("GPT 요약 완료. 제목: {}", dto.getTitle());

                dto.setGptSummary(summary);

                News saved = save(dto);
                log.info("뉴스 저장 완료. ID: {}, 제목: {}", saved.getId(), saved.getTitle());

                savedNewsList.add(saved);

                Thread.sleep(1000); // 속도 제한

            } catch (Exception e) {
                log.error("뉴스 저장 중 예외 발생. 제목: {}", dto.getTitle(), e);
            }
        }

        log.info("뉴스 저장 완료: 총 {}건 저장됨", savedNewsList.size());
        return savedNewsList;
    }

    private String summarizeWithGpt(String content) {
        String apiKey = System.getenv("OPENAI_API_KEY");
        String endpoint = "https://api.openai.com/v1/chat/completions";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        Map<String, Object> message = Map.of(
                "role", "user",
                "content", "다음 뉴스 내용을 경제 전문가 관점에서 3문장으로 요약해줘. \n" +
                        "시장 영향, 정책 시사점, 투자자에게 중요한 내용을 중심으로.\n:\n\n" + content
        );

        Map<String, Object> request = Map.of(
                "model", "gpt-3.5-turbo",
                "messages", List.of(message)
        );

        RestTemplate restTemplate = new RestTemplate();
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(request, headers);

        ResponseEntity<Map> response = restTemplate.postForEntity(endpoint, entity, Map.class);
        Map<?, ?> messageResponse = (Map<?, ?>) ((List<?>) response.getBody().get("choices")).get(0);
        Map<?, ?> innerMessage = (Map<?, ?>) messageResponse.get("message");

        return (String) innerMessage.get("content");
    }


}
