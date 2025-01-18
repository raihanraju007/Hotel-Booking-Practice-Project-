package com.raju.hotelbooking.restcontroller;

import com.raju.hotelbooking.entity.Location;
import com.raju.hotelbooking.service.LocationService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/location")
public class LocationController {

    @Autowired
    private LocationService locationService;

    @GetMapping("/")
    public ResponseEntity<List<Location>> getAllLocations(){

        List<Location> locationList = locationService.getAllLocations();

        return ResponseEntity.ok(locationList);
    }

    @PostMapping("/save")
    public ResponseEntity<String> saveLocation(
            @RequestPart Location location,
            @RequestParam(value = "image",required = true) MultipartFile file) throws IOException {
        locationService.saveLocation(location,file);

        return new ResponseEntity<>("Location save successfully", HttpStatus.CREATED);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteLocation(@PathVariable int id){
        try{
            locationService.deleteLocation(id);
            return ResponseEntity.ok("Location with ID "+id+ " has been deleted");
        }
        catch (EntityNotFoundException message){
            return  ResponseEntity.status(HttpStatus.NOT_FOUND).body(message.getMessage());
        }
    }

    @PutMapping("update/{id}")
    public ResponseEntity<Location> updateLocation(@PathVariable int id,@RequestBody Location location){
        Location updateLocation = locationService.updateLocation(id,location);

        return ResponseEntity.ok(updateLocation);
    }



}
