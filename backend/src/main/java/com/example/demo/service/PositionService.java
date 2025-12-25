package com.example.demo.service;

import com.example.demo.dto.PositionCreateDTO;
import com.example.demo.dto.PositionUpdateDTO;
import com.example.demo.entity.Position;
import com.mybatisflex.core.paginate.Page;

import java.util.List;

public interface PositionService {
    Page<Position> page(String keyword, int pageNumber, int pageSize);

    List<Position> listAll();

    Position create(PositionCreateDTO dto);

    Position update(Long id, PositionUpdateDTO dto);

    void delete(Long id);
}
