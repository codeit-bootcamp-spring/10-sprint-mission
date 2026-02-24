package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.request.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.service.ReadStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Controller
@ResponseBody
@RequestMapping("/api/readStatuses")
public class ReadStatusController {

  private final ReadStatusService readStatusService;

  //POST /api/readStatuses
  @RequestMapping(method = RequestMethod.POST)
  public ResponseEntity<ReadStatus> create(
      @RequestBody ReadStatusCreateRequest request
  ) {
    ReadStatus created = readStatusService.create(request);
    return ResponseEntity.status(HttpStatus.CREATED).body(created);
  }

  //GET /api/readStatuses?userId={}
  @RequestMapping(method = RequestMethod.GET)
  public ResponseEntity<List<ReadStatus>> findAllByUserID(
      @RequestParam UUID userId
  ) {
    List<ReadStatus> readStatuses = readStatusService.findAllByUserId(userId);
    return ResponseEntity.ok(readStatuses);
  }

  //PATCH /api/readStatuses/{readStatusId}
  @RequestMapping(value = "/{readStatusId}", method = RequestMethod.PATCH)
  public ResponseEntity<ReadStatus> update(
      @PathVariable UUID readStatusId,
      @RequestBody ReadStatusUpdateRequest request
  ) {
    ReadStatus updated = readStatusService.update(readStatusId, request);
    return ResponseEntity.ok(updated);
  }
}

//  private final ReadStatusService readStatusService;
//
//  @RequestMapping(path = "create")
//  public ResponseEntity<ReadStatus> create(@RequestBody ReadStatusCreateRequest request) {
//    ReadStatus createdReadStatus = readStatusService.create(request);
//    return ResponseEntity
//        .status(HttpStatus.CREATED)
//        .body(createdReadStatus);
//  }
//
//  @RequestMapping(path = "update")
//  public ResponseEntity<ReadStatus> update(@RequestParam("readStatusId") UUID readStatusId,
//      @RequestBody ReadStatusUpdateRequest request) {
//    ReadStatus updatedReadStatus = readStatusService.update(readStatusId, request);
//    return ResponseEntity
//        .status(HttpStatus.OK)
//        .body(updatedReadStatus);
//  }
//
//  @RequestMapping(path = "findAllByUserId")
//  public ResponseEntity<List<ReadStatus>> findAllByUserId(@RequestParam("userId") UUID userId) {
//    List<ReadStatus> readStatuses = readStatusService.findAllByUserId(userId);
//    return ResponseEntity
//        .status(HttpStatus.OK)
//        .body(readStatuses);
//  }
//}