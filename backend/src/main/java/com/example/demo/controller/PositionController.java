package com.example.demo.controller;

import com.example.demo.common.Result;
import com.example.demo.dto.PositionCreateDTO;
import com.example.demo.dto.PositionUpdateDTO;
import com.example.demo.entity.Position;
import com.example.demo.service.PositionService;
import com.mybatisflex.core.paginate.Page;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/positions")
public class PositionController {
    private final PositionService positionService;

    public PositionController(PositionService positionService) {
        this.positionService = positionService;
    }

    @GetMapping
    public Result<Page<Position>> page(@RequestParam(required = false) String keyword,
                                       @RequestParam(defaultValue = "1") int page,
                                       @RequestParam(defaultValue = "10") int size) {
        return Result.ok(positionService.page(keyword, page, size));
    }

    @GetMapping("/all")
    public Result<List<Position>> listAll() {
        return Result.ok(positionService.listAll());
    }

    @PostMapping
    public Result<Position> create(@Valid @RequestBody PositionCreateDTO dto) {
        return Result.ok(positionService.create(dto));
    }

    @PutMapping("/{id}")
    public Result<Position> update(@PathVariable Long id,
                                   @Valid @RequestBody PositionUpdateDTO dto) {
        return Result.ok(positionService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        positionService.delete(id);
        return Result.ok();
    }
}
