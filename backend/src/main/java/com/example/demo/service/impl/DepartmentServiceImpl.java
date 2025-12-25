package com.example.demo.service.impl;

import com.example.demo.dto.DepartmentCreateDTO;
import com.example.demo.dto.DepartmentUpdateDTO;
import com.example.demo.entity.Department;
import com.example.demo.mapper.DepartmentMapper;
import com.example.demo.service.DepartmentService;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class DepartmentServiceImpl implements DepartmentService {
    private final DepartmentMapper departmentMapper;

    public DepartmentServiceImpl(DepartmentMapper departmentMapper) {
        this.departmentMapper = departmentMapper;
    }

    @Override
    public Page<Department> page(String keyword, int pageNumber, int pageSize) {
        QueryWrapper query = QueryWrapper.create().from("department");
        if (keyword != null && !keyword.isBlank()) {
            String like = "%" + keyword + "%";
            query.where("name like ? or code like ?", like, like);
        }
        query.orderBy("id desc");
        return departmentMapper.paginate(Page.of(pageNumber, pageSize), query);
    }

    @Override
    public List<Department> listAll() {
        QueryWrapper query = QueryWrapper.create().from("department").orderBy("id desc");
        return departmentMapper.selectListByQuery(query);
    }

    @Override
    public Department create(DepartmentCreateDTO dto) {
        Department department = new Department();
        department.setName(dto.getName());
        department.setCode(dto.getCode());
        department.setDescription(dto.getDescription());
        department.setCreatedAt(LocalDateTime.now());
        department.setUpdatedAt(LocalDateTime.now());
        departmentMapper.insert(department);
        return department;
    }

    @Override
    public Department update(Long id, DepartmentUpdateDTO dto) {
        Department department = departmentMapper.selectOneById(id);
        if (department == null) {
            throw new IllegalArgumentException("Department not found");
        }
        department.setName(dto.getName());
        department.setCode(dto.getCode());
        department.setDescription(dto.getDescription());
        department.setUpdatedAt(LocalDateTime.now());
        departmentMapper.update(department);
        return department;
    }

    @Override
    public void delete(Long id) {
        departmentMapper.deleteById(id);
    }
}
