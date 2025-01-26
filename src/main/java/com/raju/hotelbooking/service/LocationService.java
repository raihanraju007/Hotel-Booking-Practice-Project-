package com.raju.hotelbooking.service;

import com.raju.hotelbooking.entity.Location;
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

public class LocationService {
    @Autowired
    private LocationRepository locationRepository;

    @Value("src/main/resources/static/images")
    private String uploadDir;

    public List<Location> getAllLocations(){
        return locationRepository.findAll();
    }

    public void saveLocation(Location location, MultipartFile imageFile) throws IOException {

        if (imageFile!=null && !imageFile.isEmpty()){
            String imageFileName = saveImage(imageFile,location);
            location.setImage(imageFileName);
        }

        locationRepository.save(location);

    }

    public Location getLocationById(int id){

        return locationRepository.findById(id)
                .orElseThrow(()->new EntityNotFoundException("Location not found with Id: "+id));

    }

    public  void deleteLocation(int id){
        if( ! locationRepository.existsById(id)){
            throw new EntityNotFoundException("Location not found with Id: "+id);
        }
        locationRepository.deleteById(id);
    }

    public Location updateLocation(int id, Location location, MultipartFile image) throws IOException {
        Location existingLocation = locationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Location not found with Id: "+id));
        if (location.getName() !=null){
            existingLocation.setName(location.getName());
        }
        // update image
        if (image !=null && !image.isEmpty()){
            String fileName = saveImage(image,existingLocation);
            existingLocation.setImage(fileName);
        }


        return locationRepository.save(existingLocation);
    }

    public void updateLocation(Location location){

        locationRepository.save(location);
    }


    private String saveImage(MultipartFile file, Location location) throws IOException {
        Path uploadPath = Paths.get(uploadDir+"/locations");
        if(! Files.exists(uploadPath)){
            Files.createDirectories(uploadPath);
        }
        String fileName = location.getName()+"_"+ UUID.randomUUID().toString();
        Path filePath=uploadPath.resolve(fileName);
        Files.copy(file.getInputStream(),filePath);

        return  fileName;
    }
}
