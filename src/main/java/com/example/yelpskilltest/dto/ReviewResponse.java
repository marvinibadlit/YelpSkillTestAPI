package com.example.yelpskilltest.dto;

import java.util.List;

import com.example.yelpskilltest.model.Review;

import lombok.Data;

@Data
public class ReviewResponse{
	private List<Review> reviews;
}
