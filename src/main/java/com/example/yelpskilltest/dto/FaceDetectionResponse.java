package com.example.yelpskilltest.dto;
import lombok.Data;

@Data
public class FaceDetectionResponse {
	private String joyLikelihood;
	private String sorrowLikelihood;
	private String angerLikelihood;
	private String surpriseLikelihood;
}
