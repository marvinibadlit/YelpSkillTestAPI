package com.example.yelpskilltest.dto;

import java.util.List;

import com.example.yelpskilltest.model.Business;

import lombok.Data;

@Data
public class BusinessResponse {
    private Integer total;
    private List<Business> businesses;
}
