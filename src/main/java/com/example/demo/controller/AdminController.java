package com.example.demo.controller;

import com.example.demo.dto.ReportRequestDto;
import com.example.demo.service.AdminService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admins")
public class AdminController {
    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @PostMapping("/report-users")
    public ResponseEntity<Void> reportUsers(@RequestBody ReportRequestDto reportRequestDto) {
        adminService.reportUsers(reportRequestDto.getUserIds());

        return new ResponseEntity<>(HttpStatus.OK);
    }
}
