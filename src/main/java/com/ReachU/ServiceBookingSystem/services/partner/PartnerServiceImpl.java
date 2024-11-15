package com.ReachU.ServiceBookingSystem.services.partner;

import com.ReachU.ServiceBookingSystem.dto.PartnerDTO;
import com.ReachU.ServiceBookingSystem.entity.Partner;
import com.ReachU.ServiceBookingSystem.exceptions.ResourceNotFoundException;
import com.ReachU.ServiceBookingSystem.repository.PartnerRepository;
import io.jsonwebtoken.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PartnerServiceImpl implements PartnerService {

    private final PartnerRepository partnerRepository;

    @Override
    public PartnerDTO getPartnerById(Long id) {
        Optional<Partner> partnerEntity = partnerRepository.findById(id);
        if (partnerEntity.isPresent()) {
            PartnerDTO partnerDTO = new PartnerDTO();
            partnerDTO.setId(partnerEntity.get().getId());
            partnerDTO.setName(partnerEntity.get().getName());
            partnerDTO.setLastname(partnerEntity.get().getLastname());
            partnerDTO.setEmail(partnerEntity.get().getEmail());
            partnerDTO.setPhone(partnerEntity.get().getPhone());
            partnerDTO.setService(partnerEntity.get().getService());
            partnerDTO.setImg(partnerEntity.get().getImg());
            return partnerDTO;
        } else {
            throw new RuntimeException("Partner not found with ID: " + id);
        }
    }

    @Override
    public PartnerDTO updatePartner(Long id, PartnerDTO partnerDTO, MultipartFile image) throws IOException {
        Partner existingPartner = partnerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Partner not found with ID: " + id));
        existingPartner.setName(partnerDTO.getName());
//        existingPartner.setLastname(partnerDTO.getLastname());
        existingPartner.setEmail(partnerDTO.getEmail());
        existingPartner.setPhone(partnerDTO.getPhone());
        existingPartner.setService(partnerDTO.getService());

        // Handle image file
        if (image != null && !image.isEmpty()) {
            try {
                existingPartner.setImg(image.getBytes()); // Set the image bytes
            } catch (IOException | java.io.IOException e) {
                throw new RuntimeException("Failed to process image file", e);
            }
        }

        Partner updatedPartner = partnerRepository.save(existingPartner);
        // Return the updated PartnerDTO
        return mapEntityToDto(updatedPartner);
    }

    // Helper method to map PartnerEntity to PartnerDTO
    private PartnerDTO mapEntityToDto(Partner partner) {
        PartnerDTO partnerDTO = new PartnerDTO();
        partnerDTO.setId(partner.getId());
        partnerDTO.setName(partner.getName());
//        partnerDTO.setLastname(partnerEntity.getLastname());
        partnerDTO.setEmail(partner.getEmail());
        partnerDTO.setPhone(partner.getPhone());
        partnerDTO.setService(partner.getService());
        partnerDTO.setImg(partner.getImg());

        return partnerDTO;
    }

    @Override
    public List<Partner> getAllPartners() {
        List<Partner> partners = partnerRepository.findAll();
        if (partners == null || partners.isEmpty()) {
            System.out.println("No partners found in database.");
        } else {
            partners.forEach(partner -> System.out.println("Partner: " + partner));
        }
        return partners;
    }

//    @Override
//    public List<PartnerDTO> getAllPartners() {
//        List<PartnerDTO> partners = partnerRepository.findAllWithConnections();
//        if (partners == null || partners.isEmpty()) {
//            System.out.println("No partners found in database.");
//        } else {
//            partners.forEach(partner -> System.out.println("Partner: " + partner));
//        }
//        return partners;
//    }

//    @Override
//    public List<PartnerDTO> getAllPartners() {
//        List<Partner> partners = partnerRepository.findAll(); // Temporarily using findAll
//        if (partners == null || partners.isEmpty()) {
//            System.out.println("No partners found in database.");
//            return Collections.emptyList();
//        }
//
//        partners.forEach(partner -> System.out.println("Fetched Partner: " + partner));
//
//        return partners.stream().map(this::convertToDTO).collect(Collectors.toList());
//    }



    // Conversion helper method
    private PartnerDTO convertToDTO(Partner partner) {
        PartnerDTO dto = new PartnerDTO();
        dto.setId(partner.getId());
        dto.setName(partner.getName());
        dto.setLastname(partner.getLastname());
        dto.setEmail(partner.getEmail());
        dto.setPassword(partner.getPassword());
        dto.setPhone(partner.getPhone());
        dto.setImg(partner.getImg());
        dto.setService(partner.getService());
        dto.setRating(partner.getRating()); // assuming rating is a field in Partner
        return dto;
    }


    @Override
    public Partner blockPartner(Long id) {
        Partner partner = partnerRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
        partner.setBlocked(true);
        partnerRepository.save(partner);
        return partner;
    }

    @Override
    public Partner unblockPartner(Long id) {
        Partner partner = partnerRepository.findById(id).orElseThrow(() -> new RuntimeException("Partner not found"));
        partner.setBlocked(false);
        partnerRepository.save(partner);
        return partner;
    }

    @Override
    public Partner verifyPartner(Long id) {
        Partner partner = partnerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Partner not found with ID: " + id));
        partner.setVerified(true);
        partnerRepository.save(partner);
        return partner;
    }

    @Override
    public Partner rejectPartner(Long partnerId, String rejectionReason) {
        Optional<Partner> partnerOptional = partnerRepository.findById(partnerId);
        if (partnerOptional.isPresent()) {
            Partner partner = partnerOptional.get();
            partner.setRejected(true);
            partner.setRejectionReason(rejectionReason);
            return partnerRepository.save(partner);
        } else {
            throw new ResourceNotFoundException("Partner not found with id: " + partnerId);
        }
    }

    @Override
    public List<PartnerDTO> getPartnersByService(String service) {
        System.out.println("Service being queried: " + service.trim());
        List<Partner> partners = partnerRepository.findByService(service.trim());
         return partners.stream()
                .map(this::mapEntityToDto)
                .collect(Collectors.toList());
    }

}