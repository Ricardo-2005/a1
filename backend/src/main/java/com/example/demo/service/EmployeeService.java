package com.example.demo.service;

import com.example.demo.dto.EmployeeCreateDTO;
import com.example.demo.dto.EmployeeUpdateDTO;
import com.example.demo.entity.Employee;
import com.example.demo.vo.EmployeeVO;
import com.mybatisflex.core.paginate.Page;

public interface EmployeeService {
    Page<EmployeeVO> page(String keyword, Long departmentId, Long positionId, String status,
                          int pageNumber, int pageSize);

    Employee create(EmployeeCreateDTO dto);

    Employee update(Long id, EmployeeUpdateDTO dto);

    void deleteEmployee(Long id);

    Employee getById(Long id);
}
