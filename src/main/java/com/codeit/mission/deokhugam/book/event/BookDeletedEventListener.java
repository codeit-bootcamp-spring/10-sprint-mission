package com.codeit.mission.deokhugam.book.event;

import com.codeit.mission.deokhugam.book.service.BookImageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
@Slf4j
public class BookDeletedEventListener {

  private final BookImageService bookImageService;

  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void handle(BookDeletedEvent event) {
    try{
      log.info("이벤트 발행 완료");
      bookImageService.deleteFileByUrl(event.thumbnailUrl());
    }catch (Exception e){
      log.error("도서 썸네일 삭제 실패", e);
    }
  }
}