package com.example.yelpskilltest.services;

import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gcp.vision.CloudVisionTemplate;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.example.yelpskilltest.dto.FaceDetectionResponse;
import com.example.yelpskilltest.constants.Constants;
import com.example.yelpskilltest.dto.BusinessResponse;
import com.example.yelpskilltest.dto.ReviewResponse;
import com.example.yelpskilltest.model.Business;
import com.example.yelpskilltest.model.Review;
import com.google.cloud.vision.v1.AnnotateImageResponse;
import com.google.cloud.vision.v1.FaceAnnotation;
import com.google.cloud.vision.v1.Feature;

@Service
public class BusinessSearchServiceImpl implements BusinessSearchService {
	private static final Logger log = Logger.getLogger(String.valueOf(BusinessSearchService.class));

	@Autowired
	RestTemplate restTemplate;

	@Autowired
	private ResourceLoader resourceLoader;

	@Autowired
	private CloudVisionTemplate cloudVisionTemplate;

	@Value("${skill.test.API_KEY}")
	private String API_KEY;

	private HttpEntity<String> getHttpEntity() {
		HttpHeaders headers = new HttpHeaders();
		headers.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
		headers.set(HttpHeaders.AUTHORIZATION, "Bearer " + API_KEY);
		return new HttpEntity<>(headers);
	}

	public ResponseEntity<BusinessResponse> getBusinesses(String location, String latitude, String longitude) {
		try {
			MultiValueMap<String, String> parameter = new LinkedMultiValueMap<String, String>();

			if (!location.isEmpty()) {
				parameter.add("location", location);
			}

			if (!latitude.isEmpty()) {
				parameter.add("latitude", latitude);
			}

			if (!longitude.isEmpty()) {
				parameter.add("longitude", longitude);
			}

			UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.fromHttpUrl(Constants.BUSINESS_SEARCH)
					.queryParams(parameter);

			ResponseEntity<BusinessResponse> businessResponseEntity = restTemplate.exchange(
					uriComponentsBuilder.toUriString(), HttpMethod.GET, getHttpEntity(), BusinessResponse.class);

			BusinessResponse businessResponse = businessResponseEntity.getBody();
			List<Business> businessList = businessResponse.getBusinesses().stream().map(business -> {
				List<Review> reviews = getBusinessReviewsById(business.getId());
				business.setReviews(reviews);
				return business;
			}).collect(Collectors.toList());
			businessResponse.setBusinesses(businessList);

			return new ResponseEntity<BusinessResponse>(businessResponse, HttpStatus.OK);
		} catch (final HttpClientErrorException e) {
			log.info(e.getStatusCode().toString() + " " + e.getResponseBodyAsString());
			return new ResponseEntity<BusinessResponse>(new BusinessResponse(), HttpStatus.BAD_REQUEST);
		}
	}

	private List<Review> getBusinessReviewsById(String businessId) {
		try {
			ResponseEntity<ReviewResponse> reviewResponseEntity = restTemplate.exchange(
					Constants.REVIEWS + businessId + "/reviews", HttpMethod.GET, getHttpEntity(), ReviewResponse.class);
			ReviewResponse reviewResponse = reviewResponseEntity.getBody();
			List<Review> reviewList = reviewResponse.getReviews().stream().map(review -> {
				review.setFaceDetectionResponse(getFaceDetection(review.getUser().getImage_url()));
				return review;
			}).collect(Collectors.toList());
			return reviewList;
		} catch (final HttpClientErrorException e) {
			log.info(e.getStatusCode().toString() + " " + e.getResponseBodyAsString());
			return null;
		}
	}

	public FaceDetectionResponse getFaceDetection(String imageUrl) {
		FaceDetectionResponse faceDetectionResponse = new FaceDetectionResponse();

		if (!StringUtils.isBlank(imageUrl)) {
			Resource imageResource = this.resourceLoader.getResource(imageUrl);
			AnnotateImageResponse response = this.cloudVisionTemplate.analyzeImage(imageResource,
					Feature.Type.FACE_DETECTION);

			Optional<FaceAnnotation> faceAnnotation = response.getFaceAnnotationsList().stream().findFirst();
			if (faceAnnotation.isPresent()) {
				faceDetectionResponse.setJoyLikelihood(faceAnnotation.get().getJoyLikelihood().toString());
				faceDetectionResponse.setSorrowLikelihood(faceAnnotation.get().getSorrowLikelihood().toString());
				faceDetectionResponse.setAngerLikelihood(faceAnnotation.get().getAngerLikelihood().toString());
				faceDetectionResponse.setSurpriseLikelihood(faceAnnotation.get().getSurpriseLikelihood().toString());
			}

			return faceDetectionResponse;
		}
		return new FaceDetectionResponse();

	}

}
