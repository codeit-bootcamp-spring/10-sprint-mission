package com.codeit.mission.deokhugam.book.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.codeit.mission.deokhugam.book.dto.request.BookSearchConditionDto;
import com.codeit.mission.deokhugam.book.entity.Book;
import com.codeit.mission.deokhugam.book.entity.BookStatus;
import com.codeit.mission.deokhugam.config.QuerydslConfig;
import com.codeit.mission.deokhugam.book.entity.SortDirection;
import jakarta.persistence.EntityManager;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@DataJpaTest
@Import(QuerydslConfig.class)
@EnableJpaAuditing
class BookRepositoryImplTest {

  @Autowired
  private BookRepository bookRepository;

  @Autowired
  private EntityManager em;

  @BeforeEach
  void clear() {
    bookRepository.deleteAll();
  }

  @Test
  @DisplayName("키워드로 도서를 검색할 수 있다")
  void findAllByCursor_withKeyword() {
    // given
    Book javaBook = createBook(
        "Java Programming",
        "Kim",
        "1111",
        4.5,
        10,
        LocalDate.of(2024, 1, 1)
    );

    createBook(
        "Spring Boot",
        "Lee",
        "2222",
        4.0,
        5,
        LocalDate.of(2023, 1, 1)
    );

    em.flush();
    em.clear();

    BookSearchConditionDto condition = new BookSearchConditionDto(
        "Java",
        "title",
        SortDirection.ASC,
        null,
        null,
        10
    );

    // when
    List<Book> result = bookRepository.findAllByCursor(condition);

    // then
    assertThat(result).hasSize(1);
    assertThat(result.get(0).getTitle()).isEqualTo(javaBook.getTitle());
  }

  @Test
  @DisplayName("삭제된 도서는 조회되지 않는다")
  void findAllByCursor_excludeDeletedBook() {
    // given
    Book deletedBook = createBook(
        "Deleted Book",
        "Kim",
        "1111",
        4.0,
        3,
        LocalDate.of(2024, 1, 1)
    );

    deletedBook.delete();

    createBook(
        "Normal Book",
        "Lee",
        "2222",
        5.0,
        10,
        LocalDate.of(2023, 1, 1)
    );

    em.flush();
    em.clear();

    BookSearchConditionDto condition = new BookSearchConditionDto(
        null,
        "title",
        SortDirection.ASC,
        null,
        null,
        10
    );

    // when
    List<Book> result = bookRepository.findAllByCursor(condition);

    // then
    assertThat(result)
        .extracting(Book::getTitle)
        .doesNotContain("Deleted Book");
  }

  @Test
  @DisplayName("title ASC 기준 커서 페이지네이션이 동작한다")
  void findAllByCursor_titleAscCursorPaging() {
    // given
    createBook(
        "Apple",
        "A",
        "1111",
        4.0,
        1,
        LocalDate.of(2024, 1, 1)
    );

    Book banana = createBook(
        "Banana",
        "B",
        "2222",
        4.0,
        1,
        LocalDate.of(2024, 1, 2)
    );

    createBook(
        "Carrot",
        "C",
        "3333",
        4.0,
        1,
        LocalDate.of(2024, 1, 3)
    );

    em.flush();
    em.clear();

    BookSearchConditionDto condition = new BookSearchConditionDto(
        null,
        "title",
        SortDirection.ASC,
        banana.getTitle(),
        banana.getCreatedAt(),
        10
    );

    // when
    List<Book> result = bookRepository.findAllByCursor(condition);

    // then
    assertThat(result).hasSize(1);
    assertThat(result.get(0).getTitle()).isEqualTo("Carrot");
  }

  @Test
  @DisplayName("rating DESC 기준 커서 페이지네이션이 동작한다")
  void findAllByCursor_ratingDescCursorPaging() {
    // given
    createBook(
        "Book1",
        "A",
        "1111",
        5.0,
        1,
        LocalDate.of(2024, 1, 1)
    );

    Book middle = createBook(
        "Book2",
        "B",
        "2222",
        4.0,
        1,
        LocalDate.of(2024, 1, 2)
    );

    createBook(
        "Book3",
        "C",
        "3333",
        3.0,
        1,
        LocalDate.of(2024, 1, 3)
    );

    em.flush();
    em.clear();

    BookSearchConditionDto condition = new BookSearchConditionDto(
        null,
        "rating",
        SortDirection.DESC,
        4.0,
        middle.getCreatedAt(),
        10
    );

    // when
    List<Book> result = bookRepository.findAllByCursor(condition);

    // then
    assertThat(result).hasSize(1);
    assertThat(result.get(0).getRating()).isEqualTo(3.0);
  }

  @Test
  @DisplayName("잘못된 orderBy 입력 시 예외가 발생한다")
  void findAllByCursor_invalidOrderBy() {

    BookSearchConditionDto condition = new BookSearchConditionDto(
        null,
        "invalid",
        SortDirection.ASC,
        null,
        null,
        10
    );

    assertThrows(
        InvalidDataAccessApiUsageException.class,
        () -> bookRepository.findAllByCursor(condition)
    );
  }

  @Test
  @DisplayName("검색 조건에 맞는 도서 개수를 조회한다")
  void countByCondition() {
    // given
    createBook(
        "Java Basic",
        "Kim",
        "1111",
        4.0,
        1,
        LocalDate.of(2024, 1, 1)
    );

    createBook(
        "Java Advanced",
        "Lee",
        "2222",
        5.0,
        1,
        LocalDate.of(2024, 1, 1)
    );

    createBook(
        "Spring",
        "Park",
        "3333",
        5.0,
        1,
        LocalDate.of(2024, 1, 1)
    );

    em.flush();
    em.clear();

    // when
    long count = bookRepository.countByCondition("Java");

    // then
    assertThat(count).isEqualTo(2);
  }

  private Book createBook(
      String title,
      String author,
      String isbn,
      Double rating,
      Integer reviewCount,
      LocalDate publishedDate
  ) {
    Book book = Book.builder()
        .title(title)
        .author(author)
        .description("description")
        .isbn(isbn)
        .publisher("publisher")
        .publishedDate(publishedDate)
        .rating(rating)
        .reviewCount(reviewCount)
        .build();

    return bookRepository.save(book);
  }
}