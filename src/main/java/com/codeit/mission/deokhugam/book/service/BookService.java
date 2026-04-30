package com.codeit.mission.deokhugam.book.service;

import com.codeit.mission.deokhugam.book.dto.request.BookCreateRequest;
import com.codeit.mission.deokhugam.book.dto.response.BookDto;
import com.codeit.mission.deokhugam.book.dto.request.BookSearchConditionDto;
import com.codeit.mission.deokhugam.book.dto.request.BookUpdateRequest;
import com.codeit.mission.deokhugam.book.dto.request.CursorPageRequestDto;
import com.codeit.mission.deokhugam.book.dto.response.CursorPageResponseBookDto;
import com.codeit.mission.deokhugam.book.dto.response.NaverBookDto;
import com.codeit.mission.deokhugam.book.dto.response.NaverResponseDto;
import com.codeit.mission.deokhugam.book.dto.response.OcrResponse;
import com.codeit.mission.deokhugam.book.entity.Book;
import com.codeit.mission.deokhugam.book.entity.BookStatus;
import com.codeit.mission.deokhugam.book.entity.SortDirection;
import com.codeit.mission.deokhugam.book.event.BookDeletedEvent;
import com.codeit.mission.deokhugam.book.exception.BookNotFoundException;
import com.codeit.mission.deokhugam.book.exception.CursorOrAfterFormatNotValidException;
import com.codeit.mission.deokhugam.book.exception.DuplicatedIsbnException;
import com.codeit.mission.deokhugam.book.exception.ExternalApiErrorException;
import com.codeit.mission.deokhugam.book.exception.IllegalLimitException;
import com.codeit.mission.deokhugam.book.exception.InvalidIsbnException;
import com.codeit.mission.deokhugam.book.exception.OcrFailedException;
import com.codeit.mission.deokhugam.book.exception.WrongFileTypeException;
import com.codeit.mission.deokhugam.book.mapper.BookDtoMapper;
import com.codeit.mission.deokhugam.book.repository.BookRepository;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
@Slf4j
public class BookService {

  private final BookRepository bookRepository;
  private final BookImageService bookImageService;
  private final BookDtoMapper bookDtoMapper;
  private final WebClient webClient;
  private final ApplicationEventPublisher eventPublisher;

  private static final String NAVER_BOOK_API_URL = "https://openapi.naver.com/v1/search/book_adv";

  @Value("${naverapi.client-id}")
  private String naverClientId;
  @Value("${naverapi.client-secret}")
  private String naverClientSecret;
  @Value("${ocr.url}")
  private String ocrUrl;
  @Value("${ocr.apikey}")
  private String ocrApiKey;

  //도서 생성 메서드
  @Transactional
  public BookDto createBook(BookCreateRequest request, MultipartFile image) {
    log.info("도서 생성 시작 - isbn={}", request.isbn());

    //ISBN 유효성 검증
    validateIsbn13(request.isbn());
    isExist(request.isbn());
    String imagePath = upload(image);

    Book book = Book.builder()
        .title(request.title())
        .author(request.author())
        .description(request.description())
        .publisher(request.publisher())
        .publishedDate(request.publishedDate())
        .isbn(request.isbn())
        .thumbnailUrl(imagePath)
        .reviewCount(0)
        .rating(0.0)
        .build();

    Book saved = bookRepository.save(book);

    log.info("도서 생성 완료 - id={}, isbn={}", saved.getId(), saved.getIsbn());
    return bookDtoMapper.toDto(saved);
  }

  private String upload(MultipartFile image) {
    //파일이 비지 않았고, 컨텐츠 타입이 image라면 파일 업로드 로직 수행
    if (image != null && !image.isEmpty()) {
      String contentType = image.getContentType();

      if (contentType == null || !contentType.startsWith("image/")) {
        throw new WrongFileTypeException(contentType == null ? "null" : contentType);
      }

      return bookImageService.upload(image);
    }
    return null;
  }

  //isbn 기반 Naver API 연동 메서드
  public NaverBookDto getBookInfoFromNaverApi(String isbn) {
    log.info("Naver API 호출 - isbn={}", isbn);
    //isbn 유효성 검증
    validateIsbn13(isbn);

    NaverResponseDto response = webClient.get()
        .uri(NAVER_BOOK_API_URL + "?d_isbn=" + isbn)
        .header("X-Naver-Client-Id", naverClientId)
        .header("X-Naver-Client-Secret", naverClientSecret)
        .retrieve()

        .onStatus(
            status -> status.is4xxClientError(),
            res -> res.bodyToMono(String.class)
                .doOnNext(body -> log.warn("Naver API 4xx 오류 - status={}, body={}", res.statusCode(), body))
                .then(Mono.error(new ExternalApiErrorException()))
        )

        .onStatus(
            status -> status.is5xxServerError(),
            res -> res.bodyToMono(String.class)
                .doOnNext(body -> log.error("Naver API 5xx 오류 - status={}, body={}", res.statusCode(), body))
                .then(Mono.error(new ExternalApiErrorException()))
        )

        .bodyToMono(NaverResponseDto.class)

        .doOnNext(res -> {
          int count = (res.items() != null) ? res.items().size() : 0;
          log.info("Naver API 응답 성공 - isbn={}, itemCount={}", isbn, count);
        })

        .doOnError(e -> log.error("Naver API 호출 실패 - isbn={}", isbn, e))

        .block(Duration.ofSeconds(15));

    //응답값이 없으면 예외 처리
    if (response == null || response.items() == null || response.items().isEmpty()) {
      throw new BookNotFoundException();
    }

    // 응답받은 날짜값을 LocalDate로 변환
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
    LocalDate pubdate = LocalDate.parse(response.items().get(0).pubdate(), formatter);

    return NaverBookDto.builder()
        .title(response.items().get(0).title())
        .author(response.items().get(0).author())
        .description(response.items().get(0).description())
        .publisher(response.items().get(0).publisher())
        .publishedDate(pubdate)
        .isbn(response.items().get(0).isbn())
        .thumbnailImage(getBytesInLink(response.items().get(0).image()))
        .build();
  }

  //링크로부터 파일 byte 가져오기
  private byte[] getBytesInLink(String imageUrl) {
    if (imageUrl == null || imageUrl.isBlank()) {
      return null;
    }

    try {
      return webClient.get()
          .uri(imageUrl)
          .retrieve()
          .bodyToMono(byte[].class)
          .block(Duration.ofSeconds(5));
    } catch (Exception e) {
      //이미지 로딩 실패시 null
      return null;
    }
  }

  public String ocrIsbnDetect(MultipartFile image) {

    log.info("OCR 요청 시작 - hasImage={}, size={}",
        image != null,
        image != null ? image.getSize() : 0
    );

    long start = System.currentTimeMillis();

    OcrResponse response = webClient.post()
        .uri(ocrUrl)
        .contentType(MediaType.MULTIPART_FORM_DATA)
        .body(BodyInserters.fromMultipartData("apikey", ocrApiKey)
            .with("language", "eng")
            .with("file", image.getResource()))
        .retrieve()

        .onStatus(
            HttpStatusCode::is4xxClientError,
            res -> res.bodyToMono(String.class)
                .doOnNext(body -> log.warn("OCR API 4xx 오류 - status={}, body={}", res.statusCode(), body))
                .then(Mono.error(new ExternalApiErrorException()))
        )

        .onStatus(
            HttpStatusCode::is5xxServerError,
            res -> res.bodyToMono(String.class)
                .doOnNext(body -> log.error("OCR API 5xx 오류 - status={}, body={}", res.statusCode(), body))
                .then(Mono.error(new ExternalApiErrorException()))
        )

        .bodyToMono(OcrResponse.class)

        .doOnNext(res -> {
          int count = (res.parsedResults() != null) ? res.parsedResults().size() : 0;
          log.info("OCR 응답 수신 - parsedResultsCount={}", count);
        })

        .doOnError(e -> log.error("OCR API 호출 실패", e))

        .block(Duration.ofSeconds(5));

    long duration = System.currentTimeMillis() - start;
    log.info("OCR API 호출 완료 - time={}ms", duration);

    // ===== 응답 검증 =====
    if (response == null || response.parsedResults() == null || response.parsedResults().isEmpty()) {
      log.warn("OCR 응답 비정상 - response or parsedResults 없음");
      throw new ExternalApiErrorException();
    }

    if (response.parsedResults().get(0) == null) {
      log.warn("OCR 결과 첫 요소 null");
      throw new OcrFailedException();
    }

    String parsedText = response.parsedResults().get(0).parsedText();

    if (parsedText == null || parsedText.isBlank()) {
      log.warn("OCR 텍스트 추출 실패 - 빈 문자열");
      throw new OcrFailedException();
    }

    log.debug("OCR 추출 텍스트 길이 - length={}", parsedText.length());

    String isbn = extractIsbn(parsedText);

    log.info("OCR ISBN 추출 성공 - isbn={}", isbn);

    return isbn;
  }

  private String extractIsbn(String text) {

    log.debug("ISBN 추출 시작 - textLength={}", text != null ? text.length() : 0);

    // 1. 라인 분리
    List<String> lines = Arrays.asList(text.split("\n"));
    log.debug("라인 분리 완료 - lineCount={}", lines.size());

    // 2. ISBN 포함 라인 필터링
    List<String> candidateLines = lines.stream()
        .filter(line -> line.toUpperCase().contains("ISBN"))
        .toList();

    log.debug("ISBN 포함 라인 수 - {}", candidateLines.size());

    // fallback: 없으면 전체 라인 사용
    if (candidateLines.isEmpty()) {
      log.debug("ISBN 키워드 없음 - 전체 라인 사용");
      candidateLines = lines;
    }

    // 3. 정규식
    Pattern pattern = Pattern.compile("(97[89][- ]?\\d{1,5}[- ]?\\d+[- ]?\\d+[- ]?\\d)");

    int lineIndex = 0;

    for (String line : candidateLines) {

      lineIndex++;

      // 너무 긴 로그 방지
      log.debug("라인 검사 - index={}, length={}", lineIndex, line.length());

      // 4. 라인 단위 OCR 보정
      String normalizedLine = line
          .replace("O", "0").replace("o", "0")
          .replace("I", "1").replace("i", "1")
          .replace("S", "5").replace("s", "5");

      Matcher matcher = pattern.matcher(normalizedLine);

      while (matcher.find()) {
        String raw = matcher.group();

        log.debug("정규식 매칭 발견 - rawLength={}", raw.length());

        // 5. 숫자만 남기기
        String isbn = raw.replaceAll("[^0-9X]", "");
        log.debug("정제된 ISBN 후보 생성");

        // 6. 검증
        if (isbn.length() == 13) {
          boolean valid = isValidIsbn13(isbn);
          log.debug("ISBN 검증 - isbn={}, valid={}", isbn, valid);

          if (valid) {
            log.info("ISBN 추출 성공 - {}", isbn);
            return isbn;
          }
        } else {
          log.debug("ISBN 길이 불일치 - {}", isbn);
        }
      }
    }

    log.warn("ISBN 추출 실패 - 모든 후보 소진");
    throw new OcrFailedException();
  }
  //유효한지 여부 확인하고 예외 던지는 메서드
  private void validateIsbn13(String isbn) {
    if (!isValidIsbn13(isbn)) {
      throw new InvalidIsbnException(isbn);
    }
  }

  //유효성을 실제로 확인하는 메서드
  private boolean isValidIsbn13(String isbn) {
    if (isbn == null) {
      return false;
    }

    // 13자리 숫자인지 확인
    if (!isbn.matches("\\d{13}")) {
      return false;
    }

    int sum = 0;

    for (int i = 0; i < 12; i++) {
      int digit = isbn.charAt(i) - '0';

      // 짝수/홀수 위치에 따라 가중치 적용
      sum += (i % 2 == 0) ? digit : digit * 3;
    }

    // 체크섬 계산
    int checkDigit = (10 - (sum % 10)) % 10;

    // 마지막 자리와 비교
    return checkDigit == (isbn.charAt(12) - '0');
  }

  //책 상세 정보 조회 메서드
  public BookDto getBookEntityOrThrow(UUID id) {
    Book book = bookRepository.findById(id).orElseThrow(BookNotFoundException::new);

    if (isDeleted(book)) {
      throw new BookNotFoundException();
    }

    return bookDtoMapper.toDto(book);
  }

  //책 정보 수정 메서드
  @Transactional
  public BookDto updateBook(UUID id, BookUpdateRequest request, MultipartFile image) {
    log.info("도서 수정 시작 - id={}", id);

    Book book = bookRepository.findById(id).orElseThrow(BookNotFoundException::new);

    if (isDeleted(book)) {
      throw new BookNotFoundException();
    }

    book.setTitle(request.title());
    book.setAuthor(request.author());
    book.setDescription(request.description());
    book.setPublisher(request.publisher());
    book.setPublishedDate(request.publishedDate());

    if (image != null && !image.isEmpty()) {
      log.debug("이미지 교체 발생 - id={}", id);
      String newUrl = upload(image); // 먼저 업로드
      bookImageService.deleteFileByUrl(book.getThumbnailUrl()); // 그 다음 삭제
      book.setThumbnailUrl(newUrl);
    }

    bookRepository.save(book);

    return bookDtoMapper.toDto(book);
  }

  //책 데이터 논리 삭제 메서드
  @Transactional
  public void deleteBook(UUID id) {

    log.info("도서 논리 삭제 요청 - id={}", id);

    Book book = bookRepository.findById(id)
        .orElseThrow(() -> {
          log.warn("도서 논리 삭제 실패 - 존재하지 않음 id={}", id);
          return new BookNotFoundException();
        });

    if (isDeleted(book)) {
      log.warn("도서 논리 삭제 실패 - 이미 삭제된 상태 id={}", id);
      throw new BookNotFoundException();
    }

    log.debug("도서 상태 확인 - id={}, status={}", id, book.getBookStatus());

    book.delete();
    bookRepository.save(book);

    log.info("도서 논리 삭제 완료 - id={}", id);
  }

  //도서 데이터 물리 삭제 메서드
  @Transactional
  public void hardDeleteBook(UUID id) {

    log.info("도서 물리 삭제 요청 - id={}", id);

    Book book = bookRepository.findById(id)
        .orElseThrow(() -> {
          log.warn("도서 물리 삭제 실패 - 존재하지 않음 id={}", id);
          return new BookNotFoundException();
        });

    if (isDeleted(book)) {
      log.warn("도서 물리 삭제 실패 - 이미 삭제된 상태 id={}", id);
      throw new BookNotFoundException();
    }

    String thumbnailUrl = book.getThumbnailUrl();

    log.debug("삭제 대상 도서 - id={}, thumbnailUrl={}", id, thumbnailUrl);

    bookRepository.delete(book);

    log.info("도서 DB 물리 삭제 완료 - id={}", id);

    eventPublisher.publishEvent(
        new BookDeletedEvent(thumbnailUrl)
    );

    log.info("도서 삭제 이벤트 발행 완료 - id={}, thumbnailUrl={}", id, thumbnailUrl);
  }

  public CursorPageResponseBookDto findAllBooks(
      CursorPageRequestDto request
  ) {
    String keyword = request.keyword();
    String orderBy = request.orderBy();
    SortDirection direction = request.direction();
    String cursor = request.cursor();
    String after = request.after();
    int limit = request.limit();

    if (limit <= 0) {
      throw new IllegalLimitException();
    }

    Instant afterValue = parseAfter(after);
    Object cursorValue = parseCursor(orderBy, cursor);

    List<Book> books = bookRepository.findAllByCursor(
        new BookSearchConditionDto(
            keyword,
            orderBy,
            direction,
            cursorValue,
            afterValue,
            limit + 1
        )
    );

    boolean hasNext = books.size() > limit;
    if (hasNext) {
      books = books.subList(0, limit);
    }

    List<BookDto> content = books.stream()
        .map(bookDtoMapper::toDto)
        .toList();

    String nextCursor = null;
    Instant nextAfter = null;

    if (hasNext && !books.isEmpty()) {
      Book last = books.get(books.size() - 1);
      nextCursor = extractCursor(last, orderBy);
      nextAfter = last.getCreatedAt();
    }

    Long totalElements = bookRepository.countByCondition(keyword); // 검색 조건 반영

    return new CursorPageResponseBookDto(
        content,
        nextCursor,
        nextAfter,
        content.size(),
        totalElements,
        hasNext
    );
  }

  private Object parseCursor(String orderBy, String cursor) {
    if (cursor == null || cursor.isBlank()) {
      return null;
    }

    try {
      return switch (orderBy) {
        case "title" -> cursor;
        case "publishedDate" -> LocalDate.parse(cursor);
        case "rating" -> Double.parseDouble(cursor);
        case "reviewCount" -> Integer.parseInt(cursor);
        default -> throw new IllegalArgumentException("Invalid orderBy");
      };
    } catch (NumberFormatException | java.time.format.DateTimeParseException e) {
      throw new CursorOrAfterFormatNotValidException(); // 또는 적절한 커스텀 예외
    }
  }

  private Instant parseAfter(String after) {
    if (after == null || after.isBlank()) {
      return null;
    }
    return Instant.parse(after);
  }

  private String extractCursor(Book book, String orderBy) {
    return switch (orderBy) {
      case "title" -> book.getTitle();
      case "publishedDate" -> book.getPublishedDate().toString();
      case "rating" -> String.valueOf(book.getRating());
      case "reviewCount" -> String.valueOf(book.getReviewCount());
      default -> throw new IllegalArgumentException("Invalid orderBy: " + orderBy);
    };
  }

  private boolean isDeleted(Book book) {
    return book.getBookStatus() == BookStatus.DELETED;
  }

  private void isExist(String isbn) {
    if (bookRepository.existsByIsbn(isbn)) {
      throw new DuplicatedIsbnException(isbn);
    }
  }
}