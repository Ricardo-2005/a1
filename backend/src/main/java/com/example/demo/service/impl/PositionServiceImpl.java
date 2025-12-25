package com.example.demo.service.impl;

import com.example.demo.dto.PositionCreateDTO;
import com.example.demo.dto.PositionUpdateDTO;
import com.example.demo.entity.Position;
import com.example.demo.mapper.PositionMapper;
import com.example.demo.service.PositionService;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PositionServiceImpl implements PositionService {
    private final PositionMapper positionMapper;

    public PositionServiceImpl(PositionMapper positionMapper) {
        this.positionMapper = positionMapper;
    }

    @Override
    public Page<Position> page(String keyword, int pageNumber, int pageSize) {
        QueryWrapper query = QueryWrapper.create().from("job_position");
        if (keyword != null && !keyword.isBlank()) {
            String like = "%" + keyword + "%";
            query.where("name like ? or code like ?", like, like);
        }
        query.orderBy("id desc");
        return positionMapper.paginate(Page.of(pageNumber, pageSize), query);
    }

    @Override
    public List<Position> listAll() {
        QueryWrapper query = QueryWrapper.create().from("job_position").orderBy("id desc");
        return positionMapper.selectListByQuery(query);
    }

    @Override
    public Position create(PositionCreateDTO dto) {
        Position position = new Position();
        position.setName(dto.getName());
        position.setCode(dto.getCode());
        position.setDescription(dto.getDescription());
        position.setCreatedAt(LocalDateTime.now());
        position.setUpdatedAt(LocalDateTime.now());
        positionMapper.insert(position);
        return position;
    }

    @Override
    public Position update(Long id, PositionUpdateDTO dto) {
        Position position = positionMapper.selectOneById(id);
        if (position == null) {
            throw new IllegalArgumentException("Position not found");
        }
        position.setName(dto.getName());
        position.setCode(dto.getCode());
        position.setDescription(dto.getDescription());
        position.setUpdatedAt(LocalDateTime.now());
        positionMapper.update(position);
        return position;
    }

    @Override
    public void delete(Long id) {
        positionMapper.deleteById(id);
    }
}
