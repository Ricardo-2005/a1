package com.example.demo.service;

import com.example.demo.dto.DepartmentCreateDTO;
import com.example.demo.dto.DepartmentUpdateDTO;
import com.example.demo.entity.Department;
import com.mybatisflex.core.paginate.Page;

import java.util.List;

public interface DepartmentService {
    Page<Department> page(String keyword, int pageNumber, int pageSize);

    List<Department> listAll();

    Department create(DepartmentCreateDTO dto);

    Department update(Long id, DepartmentUpdateDTO dto);

    void delete(Long id);
}
