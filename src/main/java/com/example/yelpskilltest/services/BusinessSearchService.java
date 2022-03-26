
package com.example.yelpskilltest.services;

import org.springframework.http.ResponseEntity;

import com.example.yelpskilltest.dto.BusinessResponse;

public interface BusinessSearchService {

	public ResponseEntity<BusinessResponse> getBusinesses(String location, String latitude, String longitude);

}