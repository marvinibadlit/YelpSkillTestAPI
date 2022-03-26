package com.example.yelpskilltest.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.yelpskilltest.dto.BusinessResponse;
import com.example.yelpskilltest.services.BusinessSearchService;

@RestController
public class BusinessSearchController {

	@Autowired
	private BusinessSearchService businessSearchService;

	@GetMapping("/get-business")
	public ResponseEntity<BusinessResponse> getBusinessesByCriteria(
			@RequestParam(name = "location", required = false, defaultValue = "") String location,
			@RequestParam(name = "longitude", required = false, defaultValue = "") String longitude,
			@RequestParam(name = "latitude", required = false, defaultValue = "") String latitude) {
		return businessSearchService.getBusinesses(location, latitude, longitude);
	}
}
