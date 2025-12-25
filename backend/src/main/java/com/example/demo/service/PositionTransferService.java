package com.example.demo.service;

import com.example.demo.dto.PositionTransferCreateDTO;
import com.example.demo.entity.PositionTransfer;
import com.example.demo.vo.PositionTransferVO;
import com.mybatisflex.core.paginate.Page;

import java.time.LocalDate;

public interface PositionTransferService {
    PositionTransfer create(PositionTransferCreateDTO dto);

    Page<PositionTransferVO> page(Long employeeId, LocalDate startDate, LocalDate endDate,
                                  int pageNumber, int pageSize);
}
