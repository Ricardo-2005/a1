package com.example.demo.vo;

public class LoginUserInfo {
    private Long id;
    private String username;
    private String roleCode;
    private Integer roleLevel;
    private String roleName;
    private Long employeeId;

    public LoginUserInfo() {
    }

    public LoginUserInfo(Long id, String username, String roleCode, Integer roleLevel, String roleName, Long employeeId) {
        this.id = id;
        this.username = username;
        this.roleCode = roleCode;
        this.roleLevel = roleLevel;
        this.roleName = roleName;
        this.employeeId = employeeId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRoleCode() {
        return roleCode;
    }

    public void setRoleCode(String roleCode) {
        this.roleCode = roleCode;
    }

    public Integer getRoleLevel() {
        return roleLevel;
    }

    public void setRoleLevel(Integer roleLevel) {
        this.roleLevel = roleLevel;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public Long getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Long employeeId) {
        this.employeeId = employeeId;
    }
}
