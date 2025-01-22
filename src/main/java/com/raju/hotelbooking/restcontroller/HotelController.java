package com.raju.hotelbooking.restcontroller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.raju.hotelbooking.entity.Hotel;
import com.raju.hotelbooking.service.HotelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/hotel")
public class HotelController {
    @Autowired
    private HotelService hotelService;

    @GetMapping("/")
    public ResponseEntity<List<Hotel>> getAllHotels(){
        List<Hotel> hotels = hotelService.getAllHotels();

        return ResponseEntity.ok(hotels);
    }


    @PostMapping("/save")
    public ResponseEntity<Map <String, String>> saveHotel(
            @RequestPart(value = "hotel") String hotelJson,
            @RequestParam(value = "image")MultipartFile file
    ) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        Hotel hotel = objectMapper.readValue(hotelJson,Hotel.class);
        try{

            Map<String, String> response = new HashMap<>();
            response.put("Message", "Hotel Add Successfully");
            hotelService.saveHotel(hotel,file);

            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        catch (Exception exception){
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("Message", "Hotel Add Failed");
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Hotel> findHotelById(@PathVariable int id){
        try{
            Hotel hotel = hotelService.findHotelById(id);
            return ResponseEntity.ok(hotel);
        }
        catch (RuntimeException exception){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @GetMapping("/h/search_hotel")
    public ResponseEntity<List<Hotel>>findHotelByLocationName(
            @RequestParam(value = "locationName"
            ) String locationName){

        List<Hotel> hotels = hotelService.findHotelByLocationName(locationName);

        return ResponseEntity.ok(hotels);
    }

    @GetMapping("/h/search_hotel_name")
    public ResponseEntity<Hotel>findHotelByName(
            @RequestParam(value = "name"
            ) String name){

        Hotel hotel = hotelService.findHotelByName(name);

        return ResponseEntity.ok(hotel);
    }

}
