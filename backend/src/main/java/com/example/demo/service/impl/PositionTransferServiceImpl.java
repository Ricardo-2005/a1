package com.example.demo.service.impl;

import com.example.demo.common.enums.EmployeeStatus;
import com.example.demo.dto.PositionTransferCreateDTO;
import com.example.demo.entity.Employee;
import com.example.demo.entity.PositionTransfer;
import com.example.demo.mapper.EmployeeMapper;
import com.example.demo.mapper.PositionMapper;
import com.example.demo.mapper.PositionTransferMapper;
import com.example.demo.service.PositionTransferService;
import com.example.demo.vo.PositionTransferVO;
import com.mybatisflex.core.paginate.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Service
public class PositionTransferServiceImpl implements PositionTransferService {
    private final PositionTransferMapper positionTransferMapper;
    private final EmployeeMapper employeeMapper;
    private final PositionMapper positionMapper;

    public PositionTransferServiceImpl(PositionTransferMapper positionTransferMapper,
                                       EmployeeMapper employeeMapper,
                                       PositionMapper positionMapper) {
        this.positionTransferMapper = positionTransferMapper;
        this.employeeMapper = employeeMapper;
        this.positionMapper = positionMapper;
    }

    @Override
    @Transactional
    public PositionTransfer create(PositionTransferCreateDTO dto) {
        Employee employee = employeeMapper.selectOneById(dto.getEmployeeId());
        if (employee == null) {
            throw new IllegalArgumentException("Employee not found");
        }
        if (EmployeeStatus.LEFT.name().equals(employee.getStatus())) {
            throw new IllegalArgumentException("Employee already left");
        }
        if (positionMapper.selectOneById(dto.getToPositionId()) == null) {
            throw new IllegalArgumentException("Position not found");
        }
        PositionTransfer transfer = new PositionTransfer();
        transfer.setEmployeeId(dto.getEmployeeId());
        transfer.setFromPositionId(employee.getPositionId());
        transfer.setToPositionId(dto.getToPositionId());
        transfer.setTransferDate(dto.getTransferDate());
        transfer.setReason(dto.getReason());
        transfer.setCreatedAt(LocalDateTime.now());
        positionTransferMapper.insert(transfer);

        employee.setPositionId(dto.getToPositionId());
        employee.setUpdatedAt(LocalDateTime.now());
        employeeMapper.update(employee);
        return transfer;
    }

    @Override
    public Page<PositionTransferVO> page(Long employeeId, LocalDate startDate, LocalDate endDate,
                                         int pageNumber, int pageSize) {
        long totalRow = positionTransferMapper.countTransfers(employeeId, startDate, endDate);
        Page<PositionTransferVO> page = Page.of(pageNumber, pageSize);
        if (totalRow == 0) {
            page.setRecords(Collections.emptyList());
            page.setTotalRow(0);
            page.setTotalPage(0);
            return page;
        }
        long offset = (long) (pageNumber - 1) * pageSize;
        List<PositionTransferVO> records = positionTransferMapper.selectTransfers(
                employeeId, startDate, endDate, offset, pageSize);
        page.setRecords(records);
        page.setTotalRow(totalRow);
        page.setTotalPage((totalRow + pageSize - 1) / pageSize);
        return page;
    }
}
