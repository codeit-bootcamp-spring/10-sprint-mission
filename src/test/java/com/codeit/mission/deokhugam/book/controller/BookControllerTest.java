package com.codeit.mission.deokhugam.book.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.codeit.mission.deokhugam.book.controller.BookController;
import com.codeit.mission.deokhugam.book.dto.request.BookCreateRequest;
import com.codeit.mission.deokhugam.book.dto.response.BookDto;
import com.codeit.mission.deokhugam.book.dto.request.BookUpdateRequest;
import com.codeit.mission.deokhugam.book.dto.response.CursorPageResponseBookDto;
import com.codeit.mission.deokhugam.book.dto.response.NaverBookDto;
import com.codeit.mission.deokhugam.book.service.BookService;
import com.codeit.mission.deokhugam.book.dto.request.CursorPageRequestDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;


@WebMvcTest(BookController.class)
@AutoConfigureMockMvc(addFilters = false)
class BookControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockBean
  private BookService bookService;

  private UUID bookId;
  private BookDto bookDto;

  @BeforeEach
  void setUp() {
    bookId = UUID.randomUUID();

    bookDto = new BookDto(
        bookId,
        "테스트 도서",
        "테스트 저자",
        "테스트 설명",
        "테스트 출판사",
        LocalDate.of(2025, 1, 1),
        "9781234567890",
        "http://test.com/image.jpg",
        10,
        4.5,
        Instant.now(),
        Instant.now()
    );
  }

  @Test
  @DisplayName("도서 등록 성공")
  void createBook() throws Exception {

    BookCreateRequest request = new BookCreateRequest(
        "테스트 도서",
        "테스트 저자",
        "테스트 설명",
        "테스트 출판사",
        LocalDate.of(2025, 1, 1),
        "9781234567890"
    );

    MockMultipartFile bookData = new MockMultipartFile(
        "bookData",
        "",
        "application/json",
        objectMapper.writeValueAsBytes(request)
    );

    MockMultipartFile thumbnailImage = new MockMultipartFile(
        "thumbnailImage",
        "thumbnail.jpg",
        MediaType.IMAGE_JPEG_VALUE,
        "image".getBytes()
    );

    given(bookService.createBook(any(BookCreateRequest.class), any()))
        .willReturn(bookDto);

    mockMvc.perform(
            multipart("/api/books")
                .file(bookData)
                .file(thumbnailImage)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .with(requestBuilder -> {
                  requestBuilder.setMethod("POST");
                  return requestBuilder;
                })
        )
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").value(bookId.toString()))
        .andExpect(jsonPath("$.title").value("테스트 도서"))
        .andExpect(jsonPath("$.author").value("테스트 저자"))
        .andExpect(jsonPath("$.isbn").value("9781234567890"));

    verify(bookService).createBook(any(BookCreateRequest.class), any());
  }

  @Test
  @DisplayName("ISBN으로 도서 정보 조회 성공")
  void getBookDataWithIsbnWithNaverApi() throws Exception {

    NaverBookDto response = new NaverBookDto(
        "테스트 도서",
        "테스트 저자",
        "테스트 설명",
        "테스트 출판사",
        LocalDate.now(),
        "9781234567890",
        "image".getBytes()
    );

    given(bookService.getBookInfoFromNaverApi(anyString()))
        .willReturn(response);

    mockMvc.perform(
            get("/api/books/info")
                .param("isbn", "9781234567890")
        )
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.title").value("테스트 도서"))
        .andExpect(jsonPath("$.author").value("테스트 저자"))
        .andExpect(jsonPath("$.isbn").value("9781234567890"));

    verify(bookService).getBookInfoFromNaverApi(anyString());
  }

  @Test
  @DisplayName("OCR ISBN 인식 성공")
  void ocrIsbnDetect() throws Exception {

    MockMultipartFile image = new MockMultipartFile(
        "image",
        "test.jpg",
        MediaType.IMAGE_JPEG_VALUE,
        "image".getBytes()
    );

    given(bookService.ocrIsbnDetect(any()))
        .willReturn("9781234567890");

    mockMvc.perform(
            multipart("/api/books/isbn/ocr")
                .file(image)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .with(requestBuilder -> {
                  requestBuilder.setMethod("POST");
                  return requestBuilder;
                })
        )
        .andExpect(status().isOk())
        .andExpect(content().string("9781234567890"));

    verify(bookService).ocrIsbnDetect(any());
  }

  @Test
  @DisplayName("도서 상세 조회 성공")
  void findBook() throws Exception {

    given(bookService.getBookEntityOrThrow(any(UUID.class)))
        .willReturn(bookDto);

    mockMvc.perform(
            get("/api/books/{bookId}", bookId)
        )
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(bookId.toString()))
        .andExpect(jsonPath("$.title").value("테스트 도서"))
        .andExpect(jsonPath("$.publisher").value("테스트 출판사"));

    verify(bookService).getBookEntityOrThrow(any(UUID.class));
  }

  @Test
  @DisplayName("도서 수정 성공")
  void updateBook() throws Exception {

    BookUpdateRequest request = new BookUpdateRequest(
        "수정된 도서",
        "수정된 저자",
        "수정된 설명",
        "수정된 출판사",
        LocalDate.of(2025, 2, 1)
    );

    MockMultipartFile bookData = new MockMultipartFile(
        "bookData",
        "",
        "application/json",
        objectMapper.writeValueAsBytes(request)
    );

    MockMultipartFile thumbnailImage = new MockMultipartFile(
        "thumbnailImage",
        "thumbnail.jpg",
        MediaType.IMAGE_JPEG_VALUE,
        "image".getBytes()
    );

    BookDto updatedBook = new BookDto(
        bookId,
        "수정된 도서",
        "수정된 저자",
        "수정된 설명",
        "수정된 출판사",
        LocalDate.of(2025, 2, 1),
        "9781234567890",
        "http://test.com/image.jpg",
        20,
        5.0,
        Instant.now(),
        Instant.now()
    );

    given(bookService.updateBook(any(UUID.class), any(BookUpdateRequest.class), any()))
        .willReturn(updatedBook);

    mockMvc.perform(
            multipart("/api/books/{bookId}", bookId)
                .file(bookData)
                .file(thumbnailImage)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .with(requestBuilder -> {
                  requestBuilder.setMethod("PATCH");
                  return requestBuilder;
                })
        )
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.title").value("수정된 도서"))
        .andExpect(jsonPath("$.author").value("수정된 저자"))
        .andExpect(jsonPath("$.rating").value(5.0));

    verify(bookService).updateBook(any(UUID.class), any(BookUpdateRequest.class), any());
  }

  @Test
  @DisplayName("도서 논리 삭제 성공")
  void deleteBook() throws Exception {

    willDoNothing().given(bookService).deleteBook(any(UUID.class));

    mockMvc.perform(
            delete("/api/books/{bookId}", bookId)
        )
        .andExpect(status().isNoContent());

    verify(bookService).deleteBook(any(UUID.class));
  }

  @Test
  @DisplayName("도서 물리 삭제 성공")
  void hardDeleteBook() throws Exception {

    willDoNothing().given(bookService).hardDeleteBook(any(UUID.class));

    mockMvc.perform(
            delete("/api/books/{bookId}/hard", bookId)
        )
        .andExpect(status().isNoContent());

    verify(bookService).hardDeleteBook(any(UUID.class));
  }

  @Test
  @DisplayName("도서 목록 조회 성공")
  void getBooks() throws Exception {

    CursorPageResponseBookDto response =
        new CursorPageResponseBookDto(
            List.of(bookDto),
            null,
            null,
            10,
            10L,
            false
        );

    given(bookService.findAllBooks(any(CursorPageRequestDto.class)))
        .willReturn(response);

    mockMvc.perform(
            get("/api/books")
                .param("limit", "10")
        )
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content[0].title").value("테스트 도서"))
        .andExpect(jsonPath("$.content[0].author").value("테스트 저자"));

    verify(bookService).findAllBooks(any(CursorPageRequestDto.class));
  }
}