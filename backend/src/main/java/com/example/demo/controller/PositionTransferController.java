package com.example.demo.controller;

import com.example.demo.common.Result;
import com.example.demo.dto.PositionTransferCreateDTO;
import com.example.demo.entity.PositionTransfer;
import com.example.demo.service.PositionTransferService;
import com.example.demo.vo.PositionTransferVO;
import com.mybatisflex.core.paginate.Page;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/transfers")
public class PositionTransferController {
    private final PositionTransferService positionTransferService;

    public PositionTransferController(PositionTransferService positionTransferService) {
        this.positionTransferService = positionTransferService;
    }

    @PostMapping
    public Result<PositionTransfer> create(@Valid @RequestBody PositionTransferCreateDTO dto) {
        return Result.ok(positionTransferService.create(dto));
    }

    @GetMapping
    public Result<Page<PositionTransferVO>> page(@RequestParam(required = false) Long employeeId,
                                                 @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                                                 @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
                                                 @RequestParam(defaultValue = "1") int page,
                                                 @RequestParam(defaultValue = "10") int size) {
        return Result.ok(positionTransferService.page(employeeId, startDate, endDate, page, size));
    }
}
