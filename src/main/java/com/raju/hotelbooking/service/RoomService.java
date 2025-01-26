package com.raju.hotelbooking.service;


import com.raju.hotelbooking.entity.Hotel;
import com.raju.hotelbooking.entity.Location;
import com.raju.hotelbooking.entity.Room;
import com.raju.hotelbooking.repository.HotelRepository;
import com.raju.hotelbooking.repository.RoomRepository;
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
public class RoomService {
    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private HotelRepository hotelRepository;

    @Value("src/main/resources/static/images")
    private String uploadDir;

    public List<Room> getAllRooms() {

        return roomRepository.findAll();
    }

    public void saveRoom(Room room, MultipartFile imageFile) throws IOException {

        if (imageFile != null && !imageFile.isEmpty()) {
            String imageFileName = saveImage(imageFile, room);
            room.setImage(imageFileName);
        }
        roomRepository.save(room);
    }

    public Room getRoomById(int id) {
        return roomRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Room not found with Id: " + id));
    }

    public void deleteRoom(int id) {
        if (!roomRepository.existsById(id)) {
            throw new EntityNotFoundException("Room not found with Id: " + id);
        }
        roomRepository.deleteById(id);
    }

    public Room updateRoom(int id, Room room, MultipartFile image) throws IOException {
        Room existingRoom = roomRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Room not found with Id: " + id));
        existingRoom.setName(room.getName());
        existingRoom.setPrice((room.getPrice()));
        existingRoom.setArea(room.getArea());
        existingRoom.setAdultNo(room.getAdultNo());
        existingRoom.setChildNo(room.getChildNo());

        // update location
        Hotel hotel = hotelRepository.findById(room.getHotel().getId())
                .orElseThrow(() -> new EntityNotFoundException("Hotel not found with Id: " + room.getHotel().getId()));

        existingRoom.setHotel(hotel);

        // update image
        if (image != null && !image.isEmpty()) {
            String fileName = saveImage(image, existingRoom);
            existingRoom.setImage(fileName);
        }
        return roomRepository.save(existingRoom);
    }

    public List<Room> findRoomByHotelName(String hotelName) {
        return roomRepository.findRoomByHotelName(hotelName);
    }

    public List<Room> findRoomByHotelId(int hotelId) {
        return roomRepository.findRoomByHotelId(hotelId);
    }

    private String saveImage(MultipartFile file, Room room) throws IOException {
        Path uploadPath = Paths.get(uploadDir + "/rooms");
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }
        String fileName = room.getName() + "_" + UUID.randomUUID().toString();
        Path filePath = uploadPath.resolve(fileName);
        Files.copy(file.getInputStream(), filePath);

        return fileName;
    }
}
