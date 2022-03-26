package com.example.yelpskilltest.model;

import com.example.yelpskilltest.dto.FaceDetectionResponse;

import lombok.Data;

@Data
public class Review {
	private String text;
	private User user;
	private FaceDetectionResponse faceDetectionResponse;
}
