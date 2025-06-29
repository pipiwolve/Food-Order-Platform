package com.sky.service;

import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.entity.Employee;
import com.sky.result.PageResult;

public interface EmployeeService {

    /**
     * 员工登录
     * @param employeeLoginDTO
     * @return
     */
    Employee login(EmployeeLoginDTO employeeLoginDTO);

    // new employee
    void save(EmployeeDTO employeeDTO);

    // page search
    PageResult pageQuery(EmployeePageQueryDTO employeePageQueryDTO);

    // start or stop
    void startOrStop(Integer status, Long id);
}
