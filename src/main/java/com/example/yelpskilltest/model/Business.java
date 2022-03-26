package com.example.yelpskilltest.model;

import java.util.List;

import lombok.Data;

@Data
public class Business {
	 private String id;
	 private String name;
	 private String rating;
	 private Object categories;
	 private Object location;
	 private Object coordinates;
	 private String price;
	 private List<Review> reviews;
}
