package com.webapp.webapp.controller;

import com.timgroup.statsd.StatsDClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/")
public class HealthController {

    Logger logger = LoggerFactory.getLogger(HealthController.class);

    @Autowired
    StatsDClient statsDClient;

    @GetMapping
    public ResponseEntity<String> checkHealth() { 

        logger.info("Health Check Success");
        return ResponseEntity.ok().body("Health Check Success");
    }

}
