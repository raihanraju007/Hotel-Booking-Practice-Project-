package com.raju.hotelbooking.service;

import com.raju.hotelbooking.entity.Hotel;
import com.raju.hotelbooking.entity.Location;
import com.raju.hotelbooking.repository.HotelRepository;
import com.raju.hotelbooking.repository.LocationRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Service
public class HotelService {
    @Autowired
    private HotelRepository hotelRepository;

    @Autowired
    private LocationRepository locationRepository;

    @Value("src/main/resources/static/images")
    private String uploadDir;

    public List<Hotel> getAllHotels(){

        return hotelRepository.findAll();
    }

    public void saveHotel(Hotel hotel, MultipartFile imageFile) throws IOException {

        if (imageFile!=null && !imageFile.isEmpty()){
            String imageFileName = saveImage(imageFile,hotel);
            hotel.setImage(imageFileName);
        }
        Location location = locationRepository.findById(hotel.getLocation().getId())
                .orElseThrow(()->new EntityNotFoundException("Location not found with Id: "+ hotel.getLocation().getId()));

        hotel.setLocation(location);
        hotelRepository.save(hotel);

    }

    public Hotel findHotelById(int id){
        return hotelRepository.findById(id)
                .orElseThrow(()->new EntityNotFoundException("Hotel not found with Id: " +id));
    }

    public Hotel findHotelByName(String name){
        return hotelRepository.findByName(name)
                .orElseThrow(()->new EntityNotFoundException("Hotel not found with name: " +name));
    }

    public void updateHotel(int id,Hotel hotel, MultipartFile image) throws IOException {

        Hotel existingHotel = hotelRepository.findById(id)
                .orElseThrow(()->new EntityNotFoundException("Hotel not found with Id: " +id));

        existingHotel.setName(hotel.getName());
        existingHotel.setAddress((hotel.getAddress()));
        existingHotel.setRating(hotel.getRating());
        existingHotel.setMaximumPrice(hotel.getMaximumPrice());
        existingHotel.setMinimumPrice(hotel.getMaximumPrice());

        // update location
        Location location = locationRepository.findById(hotel.getLocation().getId())
                .orElseThrow(()->new EntityNotFoundException("Location not found with Id: "+ hotel.getLocation().getId()));

        existingHotel.setLocation(location);

        // update image
        if (image !=null && !image.isEmpty()){
            String fileName = saveImage(image,existingHotel);
            existingHotel.setImage(fileName);
        }

    }

    public List<Hotel> findHotelByLocationName(String locationName){
        return hotelRepository.findHotelByLocationName(locationName);
    }



    private String saveImage(MultipartFile file, Hotel hotel) throws IOException {
        Path uploadPath = Paths.get(uploadDir+"/hotels");
        if(! Files.exists(uploadPath)){
            Files.createDirectories(uploadPath);
        }
        String fileName = hotel.getName()+"_"+ UUID.randomUUID().toString();
        Path filePath=uploadPath.resolve(fileName);
        Files.copy(file.getInputStream(),filePath);

        return  fileName;
    }
}
