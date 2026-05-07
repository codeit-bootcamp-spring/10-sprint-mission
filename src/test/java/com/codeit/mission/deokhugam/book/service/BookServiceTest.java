package com.codeit.mission.deokhugam.book.service;

import com.codeit.mission.deokhugam.book.dto.request.BookCreateRequest;
import com.codeit.mission.deokhugam.book.dto.response.BookDto;
import com.codeit.mission.deokhugam.book.dto.request.BookUpdateRequest;
import com.codeit.mission.deokhugam.book.dto.request.CursorPageRequestDto;
import com.codeit.mission.deokhugam.book.dto.response.CursorPageResponseBookDto;
import com.codeit.mission.deokhugam.book.entity.Book;
import com.codeit.mission.deokhugam.book.entity.BookStatus;
import com.codeit.mission.deokhugam.book.entity.SortDirection;
import com.codeit.mission.deokhugam.book.event.BookDeletedEvent;
import com.codeit.mission.deokhugam.book.exception.BookNotFoundException;
import com.codeit.mission.deokhugam.book.exception.CursorOrAfterFormatNotValidException;
import com.codeit.mission.deokhugam.book.exception.DuplicatedIsbnException;
import com.codeit.mission.deokhugam.book.exception.IllegalLimitException;
import com.codeit.mission.deokhugam.book.exception.WrongFileTypeException;
import com.codeit.mission.deokhugam.book.mapper.BookDtoMapper;
import com.codeit.mission.deokhugam.book.repository.BookRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.UUID;
import java.time.Instant;


import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {

    @Mock
    private BookDtoMapper bookDtoMapper;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private BookImageService bookImageService;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private BookService bookService;

    @Test
    @DisplayName("이미지 없이 도서를 생성하면 업로드 없이 저장된다")
    void createBookSuccessWithoutImage() {
        // given
        BookCreateRequest request = new BookCreateRequest(
                "제목", "저자", "설명", "출판사",
                LocalDate.now(), "9788996724155"
        );

        Book savedBook = Book.builder()
                .title("제목")
                .thumbnailUrl(null)
                .build();

        BookDto expectedDto = new BookDto(
                UUID.randomUUID(),
                "제목", "저자", "설명", "출판사",
                LocalDate.now(), "9788996724155", null, 0, 0.0,
                Instant.now(), Instant.now()
        );

        when(bookRepository.save(any(Book.class))).thenReturn(savedBook);
        when(bookDtoMapper.toDto(savedBook)).thenReturn(expectedDto);

        // when
        BookDto result = bookService.createBook(request, null);

        // then
        ArgumentCaptor<Book> captor = ArgumentCaptor.forClass(Book.class);
        verify(bookRepository).save(captor.capture());

        Book saved = captor.getValue();

        assertThat(saved.getTitle()).isEqualTo("제목");
        assertThat(saved.getAuthor()).isEqualTo("저자");
        assertThat(saved.getDescription()).isEqualTo("설명");
        assertThat(saved.getPublisher()).isEqualTo("출판사");
        assertThat(saved.getPublishedDate()).isEqualTo(request.publishedDate());
        assertThat(saved.getIsbn()).isEqualTo("9788996724155");
        assertThat(saved.getThumbnailUrl()).isNull();
        assertThat(saved.getReviewCount()).isEqualTo(0);
        assertThat(saved.getRating()).isEqualTo(0.0);

        verify(bookImageService, never()).upload(any());
        verify(bookDtoMapper).toDto(savedBook);

        assertThat(result).isSameAs(expectedDto);
    }

    @Test
    @DisplayName("이미지와 함께 도서를 생성하면 업로드 후 저장된다")
    void createBookSuccessWithImage() {
        // given
        BookCreateRequest request = new BookCreateRequest(
                "제목", "저자", "설명", "출판사",
                LocalDate.now(), "9788996724155"
        );

        MultipartFile image = mock(MultipartFile.class);
        when(image.getContentType()).thenReturn("image/png");
        when(image.isEmpty()).thenReturn(false);

        String imageUrl = "https://image.url/test.png";

        Book savedBook = Book.builder()
                .title("제목")
                .thumbnailUrl(imageUrl)
                .build();

        BookDto expectedDto = new BookDto(
                UUID.randomUUID(),
                "제목", "저자", "설명", "출판사",
                LocalDate.now(), "9788996724155", imageUrl, 0, 0.0,
                Instant.now(), Instant.now()
        );

        when(bookImageService.upload(image)).thenReturn(imageUrl);
        when(bookRepository.save(any(Book.class))).thenReturn(savedBook);
        when(bookDtoMapper.toDto(savedBook)).thenReturn(expectedDto);

        // when
        BookDto result = bookService.createBook(request, image);

        // then
        ArgumentCaptor<Book> captor = ArgumentCaptor.forClass(Book.class);
        verify(bookRepository).save(captor.capture());

        Book saved = captor.getValue();

        assertThat(saved.getThumbnailUrl()).isEqualTo(imageUrl);
        assertThat(saved.getReviewCount()).isEqualTo(0);
        assertThat(saved.getRating()).isEqualTo(0.0);

        verify(bookImageService).upload(image);
        verify(bookDtoMapper).toDto(savedBook);

        assertThat(result).isSameAs(expectedDto);
    }

    @Test
    @DisplayName("이미지 업로드에 실패하면 도서 생성도 실패한다")
    void createBookFailWhenImageUploadFails() {
        // given
        BookCreateRequest request = new BookCreateRequest(
                "제목", "저자", "설명", "출판사",
                LocalDate.now(), "9788996724155"
        );

        MultipartFile image = mock(MultipartFile.class);

        //when & then
        when(image.isEmpty()).thenReturn(false);
        when(image.getContentType()).thenReturn("image/jpeg");

        when(bookImageService.upload(image))
                .thenThrow(new RuntimeException("upload fail"));

        assertThatThrownBy(() -> bookService.createBook(request, image))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("upload fail");

        verify(bookRepository, never()).save(any());
        verify(bookDtoMapper, never()).toDto(any());
    }

    @Test
    @DisplayName("이미지 타입이 아니면 예외가 발생한다")
    void createBookFailWhenWrongFileType() {
        // given
        BookCreateRequest request = new BookCreateRequest(
                "제목", "저자", "설명", "출판사",
                LocalDate.now(), "9788996724155"
        );

        MultipartFile image = mock(MultipartFile.class);
        when(image.isEmpty()).thenReturn(false);
        when(image.getContentType()).thenReturn("text/plain");

        // when & then
        assertThatThrownBy(() -> bookService.createBook(request, image))
                .isInstanceOf(WrongFileTypeException.class);

        verify(bookImageService, never()).upload(any());
        verify(bookRepository, never()).save(any());
    }

    @Test
    @DisplayName("DB 저장에 실패하면 예외가 발생한다")
    void createBookFailWhenRepositoryFails() {
        // given
        BookCreateRequest request = new BookCreateRequest(
                "제목", "저자", "설명", "출판사",
                LocalDate.now(), "9788996724155"
        );

        when(bookRepository.save(any(Book.class)))
                .thenThrow(new RuntimeException("DB 오류"));

        // when & then
        assertThatThrownBy(() -> bookService.createBook(request, null))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("DB 오류");

        verify(bookDtoMapper, never()).toDto(any());
    }

    @Test
    @DisplayName("api에서 반환된 텍스트를 정제해 isbn 값만 내보낸다: isbn 문구가 있을 때")
    void extractIsbnFromApiText_withIsbn() {
        //given
        String apiTest = "010-0000-0000\r\n0503405-5000\r\nubmedia@naver.com\r\nISBN 978-89-967241-5-5 (93010)\r\n";

        //when & then
        String result = ReflectionTestUtils.invokeMethod(bookService, "extractIsbn", apiTest);

        //then
        assertThat(result).isEqualTo("9788996724155");
    }

    @Test
    @DisplayName("api에서 반환된 텍스트를 정제해 isbn 값만 내보낸다: isbn 문구가 없을 때")
    void extractIsbnFromApiText_withoutIsbn(){
        //given
        String apiTest = "010-0000-0000\r\n0503405-5000\r\nubmedia@naver.com\r\n978-89-967241-5-5 (93010)\r\n";

        //when & then
        String result = ReflectionTestUtils.invokeMethod(bookService, "extractIsbn", apiTest);

        //then
        assertThat(result).isEqualTo("9788996724155");
    }

    @Test
    @DisplayName("올바른 isbn이 투입될 경우 검증에 성공한다.")
    void isbnValidation_success() {
        //given
        String isbn = "9788996724155";

        //when
        Boolean result = ReflectionTestUtils.invokeMethod(bookService, "isValidIsbn13", isbn);

        //then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("잘못된 isbn이 투입될 경우 검증에 실패한다.")
    void isbnValidation_failure() {
        //given
        String isbn = "1234567891011";

        //when
        Boolean result = ReflectionTestUtils.invokeMethod(bookService, "isValidIsbn13", isbn);

        //then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("중복 ISBN이면 생성 실패")
    void createBook_duplicate_isbn() {
        BookCreateRequest request = new BookCreateRequest(
            "제목",
            "저자",
            "설명",
            "출판사",
            LocalDate.now(),
            "9788996724155"
        );

        when(bookRepository.existsByIsbn("9788996724155"))
            .thenReturn(true);

        assertThatThrownBy(() ->
            bookService.createBook(request, null)
        ).isInstanceOf(DuplicatedIsbnException.class);
    }

    @Test
    @DisplayName("이미지를 수정하면 기존 이미지는 삭제되고 새 이미지가 업로드된다")
    void updateBookWithImage() {
        // given
        UUID id = UUID.randomUUID();

        Book book = Book.builder()
            .thumbnailUrl("old-url")
            .build();

        BookUpdateRequest request = new BookUpdateRequest(
            "title", "author", "desc", "publisher", LocalDate.now()
        );

        MultipartFile image = mock(MultipartFile.class);

        when(image.isEmpty()).thenReturn(false);
        when(image.getContentType()).thenReturn("image/png");

        when(bookRepository.findById(id)).thenReturn(Optional.of(book));
        when(bookImageService.upload(image)).thenReturn("new-url");

        // when
        bookService.updateBook(id, request, image);

        // then
        verify(bookImageService).deleteFileByUrl("old-url");
        verify(bookImageService).upload(image);

        assertThat(book.getThumbnailUrl()).isEqualTo("new-url");
    }

    @Test
    @DisplayName("수정 실패 - 도서 없음")
    void updateBook_notFound() {
        UUID id = UUID.randomUUID();

        when(bookRepository.findById(id))
            .thenReturn(Optional.empty());

        BookUpdateRequest request =
            new BookUpdateRequest(
                "t", "a", "d", "p", LocalDate.now()
            );

        assertThatThrownBy(() ->
            bookService.updateBook(id, request, null)
        ).isInstanceOf(BookNotFoundException.class);
    }

    @Test
    @DisplayName("삭제된 도서는 수정 불가")
    void updateBook_deleted() {
        UUID id = UUID.randomUUID();

        Book book = mock(Book.class);

        when(bookRepository.findById(id))
            .thenReturn(Optional.of(book));

        when(book.getBookStatus())
            .thenReturn(BookStatus.DELETED);

        BookUpdateRequest request =
            new BookUpdateRequest(
                "t", "a", "d", "p", LocalDate.now()
            );

        assertThatThrownBy(() ->
            bookService.updateBook(id, request, null)
        ).isInstanceOf(BookNotFoundException.class);
    }

    @Test
    @DisplayName("이미지 업로드 성공")
    void upload_success() {
        MultipartFile image = mock(MultipartFile.class);

        when(image.isEmpty()).thenReturn(false);
        when(image.getContentType()).thenReturn("image/jpeg");
        when(bookImageService.upload(image)).thenReturn("url");

        String result = ReflectionTestUtils.invokeMethod(bookService, "upload", image);

        assertThat(result).isEqualTo("url");
    }

    @Test
    @DisplayName("이미지 타입이 아니면 예외")
    void upload_fail_wrong_type() {
        MultipartFile image = mock(MultipartFile.class);

        when(image.isEmpty()).thenReturn(false);
        when(image.getContentType()).thenReturn("text/plain");

        assertThatThrownBy(() ->
            ReflectionTestUtils.invokeMethod(bookService, "upload", image)
        ).isInstanceOf(WrongFileTypeException.class);
    }

    @Test
    @DisplayName("이미지가 없으면 null 반환")
    void upload_null() {
        String result = ReflectionTestUtils.invokeMethod(bookService, "upload", (MultipartFile) null);

        assertThat(result).isNull();
    }

    @Test
    @DisplayName("도서를 논리 삭제하면 상태가 DELETED로 변경된다")
    void deleteBookSuccess() {
        // given
        UUID id = UUID.randomUUID();

        Book book = mock(Book.class);

        when(bookRepository.findById(id)).thenReturn(Optional.of(book));
        when(book.getBookStatus()).thenReturn(BookStatus.ACTIVE);

        // when
        bookService.deleteBook(id);

        // then
        verify(book).delete(); // 상태 변경
        verify(bookRepository).save(book);
    }

    @Test
    @DisplayName("이미 삭제된 도서를 논리 삭제하면 예외 발생")
    void deleteBookFailWhenAlreadyDeleted() {
        UUID id = UUID.randomUUID();

        Book book = mock(Book.class);
        when(bookRepository.findById(id)).thenReturn(Optional.of(book));
        when(book.getBookStatus()).thenReturn(BookStatus.DELETED);

        assertThatThrownBy(() -> bookService.deleteBook(id))
            .isInstanceOf(BookNotFoundException.class);

        verify(bookRepository, never()).save(any());
    }

    @Test
    @DisplayName("존재하지 않는 도서를 삭제하면 예외 발생")
    void deleteBookFailWhenNotFound() {
        UUID id = UUID.randomUUID();

        when(bookRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookService.deleteBook(id))
            .isInstanceOf(BookNotFoundException.class);
    }

    @Test
    @DisplayName("도서를 물리 삭제하면 도서 물리 삭제 이벤트가 발행된다")
    void hardDeleteBookSuccess() {
        // given
        UUID id = UUID.randomUUID();

        Book book = mock(Book.class);
        when(book.getBookStatus()).thenReturn(BookStatus.ACTIVE);
        when(book.getThumbnailUrl()).thenReturn("url");

        when(bookRepository.findById(id)).thenReturn(Optional.of(book));

        // when
        bookService.hardDeleteBook(id);

        // then
        verify(bookRepository).delete(book);

        ArgumentCaptor<BookDeletedEvent> captor =
            ArgumentCaptor.forClass(BookDeletedEvent.class);

        verify(eventPublisher).publishEvent(captor.capture());

        assertThat(captor.getValue().thumbnailUrl()).isEqualTo("url");
    }

    @Test
    @DisplayName("이미 삭제된 도서를 물리 삭제하면 예외 발생")
    void hardDeleteBookFailWhenDeleted() {
        UUID id = UUID.randomUUID();

        Book book = mock(Book.class);
        when(book.getBookStatus()).thenReturn(BookStatus.DELETED);
        when(bookRepository.findById(id)).thenReturn(Optional.of(book));

        assertThatThrownBy(() -> bookService.hardDeleteBook(id))
            .isInstanceOf(BookNotFoundException.class);

        verify(bookRepository, never()).delete(any());
        verify(eventPublisher, never()).publishEvent(any());
    }

    @Test
    @DisplayName("정상: 커서 기반 도서 목록 조회 - hasNext=true")
    void findAllBooks_success_hasNext_true() {
        // given
        CursorPageRequestDto request = new CursorPageRequestDto(
            "java",
            "title",
            SortDirection.ASC,
            null,
            null,
            2
        );

        Book book1 = createBook("A");
        Book book2 = createBook("B");
        Book book3 = createBook("C"); // limit + 1

        List<Book> books = List.of(book1, book2, book3);

        when(bookRepository.findAllByCursor(any()))
            .thenReturn(books);

        when(bookRepository.countByCondition("java"))
            .thenReturn(3L);

        when(bookDtoMapper.toDto(any()))
            .thenAnswer(invocation -> {
                Book b = invocation.getArgument(0);
                return new BookDto(
                    UUID.randomUUID(),
                    b.getTitle(),
                    b.getAuthor(),
                    b.getDescription(),
                    b.getPublisher(),
                    b.getPublishedDate(),
                    b.getIsbn(),
                    b.getThumbnailUrl(),
                    b.getReviewCount(),
                    b.getRating(),
                    b.getCreatedAt(),
                    b.getUpdatedAt()
                );
            });

        // when
        CursorPageResponseBookDto response = bookService.findAllBooks(request);

        // then
        assertThat(response.content()).hasSize(2);
        assertThat(response.hasNext()).isTrue();
        assertThat(response.nextCursor()).isEqualTo("B");
        assertThat(response.nextAfter()).isEqualTo(book2.getCreatedAt());
        assertThat(response.totalElements()).isEqualTo(3L);
    }

    @Test
    @DisplayName("정상: 커서 기반 도서 목록 조회 - hasNext=false")
    void findAllBooks_success_hasNext_false() {
        // given
        CursorPageRequestDto request = new CursorPageRequestDto(
            null,
            "title",
            SortDirection.ASC,
            null,
            null,
            2
        );

        Book book1 = createBook("A");
        Book book2 = createBook("B");

        List<Book> books = List.of(book1, book2);

        when(bookRepository.findAllByCursor(any()))
            .thenReturn(books);

        when(bookRepository.countByCondition(null))
            .thenReturn(2L);

        when(bookDtoMapper.toDto(any()))
            .thenAnswer(invocation -> {
                Book b = invocation.getArgument(0);
                return new BookDto(
                    UUID.randomUUID(),
                    b.getTitle(),
                    b.getAuthor(),
                    b.getDescription(),
                    b.getPublisher(),
                    b.getPublishedDate(),
                    b.getIsbn(),
                    b.getThumbnailUrl(),
                    b.getReviewCount(),
                    b.getRating(),
                    b.getCreatedAt(),
                    b.getUpdatedAt()
                );
            });

        // when
        CursorPageResponseBookDto response = bookService.findAllBooks(request);

        // then
        assertThat(response.content()).hasSize(2);
        assertThat(response.hasNext()).isFalse();
        assertThat(response.nextCursor()).isNull();
        assertThat(response.nextAfter()).isNull();
    }

    @Test
    @DisplayName("예외: limit이 0 이하이면 예외 발생")
    void findAllBooks_invalid_limit() {
        // given
        CursorPageRequestDto request = new CursorPageRequestDto(
            null,
            "title",
            SortDirection.ASC,
            null,
            null,
            0
        );

        // when & then
        assertThatThrownBy(() -> bookService.findAllBooks(request))
            .isInstanceOf(IllegalLimitException.class);
    }

    @Test
    @DisplayName("예외: 잘못된 cursor 포맷")
    void findAllBooks_invalid_cursor_format() {
        // given
        CursorPageRequestDto request = new CursorPageRequestDto(
            null,
            "reviewCount",
            SortDirection.ASC,
            "not-a-number",
            null,
            2
        );

        // when & then
        assertThatThrownBy(() -> bookService.findAllBooks(request))
            .isInstanceOf(CursorOrAfterFormatNotValidException.class);
    }

    @Test
    @DisplayName("예외: 잘못된 orderBy")
    void findAllBooks_invalid_orderBy() {
        // given
        CursorPageRequestDto request = new CursorPageRequestDto(
            null,
            "invalid",
            SortDirection.ASC,
            "abc",
            null,
            2
        );

        // when & then
        assertThatThrownBy(() -> bookService.findAllBooks(request))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void parseCursor_rating() {
        Object result = ReflectionTestUtils.invokeMethod(
            bookService,
            "parseCursor",
            "rating",
            "4.5"
        );

        assertThat(result).isEqualTo(4.5);
    }

    @Test
    void parseCursor_publishedDate() {
        Object result = ReflectionTestUtils.invokeMethod(
            bookService,
            "parseCursor",
            "publishedDate",
            "2025-01-01"
        );

        assertThat(result)
            .isEqualTo(LocalDate.parse("2025-01-01"));
    }

    @Test
    void parseCursor_title() {
        Object result = ReflectionTestUtils.invokeMethod(
            bookService,
            "parseCursor",
            "title",
            "java"
        );

        assertThat(result).isEqualTo("java");
    }

    @Test
    void parseAfter_success() {
        Instant now = Instant.now();

        Instant result = ReflectionTestUtils.invokeMethod(
            bookService,
            "parseAfter",
            now.toString()
        );

        assertThat(result).isEqualTo(now);
    }

    @Test
    void parseAfter_null() {
        Instant result = ReflectionTestUtils.invokeMethod(
            bookService,
            "parseAfter",
            (String) null
        );

        assertThat(result).isNull();
    }

    @Test
    void extractCursor_title() {
        Book book = createBook("Java");

        String result = ReflectionTestUtils.invokeMethod(
            bookService,
            "extractCursor",
            book,
            "title"
        );

        assertThat(result).isEqualTo("Java");
    }


    @Test
    @DisplayName("도서 상세 조회 성공")
    void getBookEntity_success() {
        UUID id = UUID.randomUUID();

        Book book = createBook("title");
        BookDto dto = mock(BookDto.class);

        when(bookRepository.findById(id)).thenReturn(Optional.of(book));
        when(bookDtoMapper.toDto(book)).thenReturn(dto);

        BookDto result = bookService.getBookEntityOrThrow(id);

        assertThat(result).isEqualTo(dto);
    }

    @Test
    @DisplayName("삭제된 도서는 조회 실패")
    void getBookEntity_deleted() {
        UUID id = UUID.randomUUID();

        Book book = mock(Book.class);

        when(bookRepository.findById(id)).thenReturn(Optional.of(book));
        when(book.getBookStatus()).thenReturn(BookStatus.DELETED);

        assertThatThrownBy(() ->
            bookService.getBookEntityOrThrow(id)
        ).isInstanceOf(BookNotFoundException.class);
    }



    // ------------------------
    // 헬퍼 메서드
    // ------------------------
    private Book createBook(String title) {
        Book book = Book.builder()
            .title(title)
            .author("author")
            .description("desc")
            .publisher("pub")
            .publishedDate(LocalDate.now())
            .isbn("9788996724155")
            .thumbnailUrl("url")
            .reviewCount(4)
            .rating(4.0)
            .build();

        ReflectionTestUtils.setField(book, "createdAt", Instant.now());

        return book;
    }
}
