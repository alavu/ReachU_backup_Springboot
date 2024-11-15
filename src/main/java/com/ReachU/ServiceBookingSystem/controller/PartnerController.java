package com.ReachU.ServiceBookingSystem.controller;

import com.ReachU.ServiceBookingSystem.dto.PartnerDTO;
import com.ReachU.ServiceBookingSystem.dto.RejectPartnerRequest;
import com.ReachU.ServiceBookingSystem.entity.Partner;
import com.ReachU.ServiceBookingSystem.repository.PartnerRepository;
import com.ReachU.ServiceBookingSystem.services.partner.PartnerEarningService;
import com.ReachU.ServiceBookingSystem.services.partner.PartnerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/partners")
public class PartnerController {

    private final PartnerService partnerService;
    private final PartnerRepository partnerRepository;
    private final PartnerEarningService partnerEarningService;

    @GetMapping("/{id}")
    public ResponseEntity<PartnerDTO> getPartnerById(@PathVariable Long id) {
        PartnerDTO partner = partnerService.getPartnerById(id);
        return ResponseEntity.ok(partner);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updatePartner(
            @PathVariable("id") Long partnerId,
            @ModelAttribute PartnerDTO partnerDTO,
            @RequestParam(value = "image", required = false) MultipartFile image) {

        try {
            PartnerDTO updatedPartner = partnerService.updatePartner(partnerId, partnerDTO, image);
            return new ResponseEntity<>(updatedPartner, HttpStatus.OK);

        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @GetMapping("/list")
    public ResponseEntity<List<Partner>> getAllPartner() {
        List<Partner> partners = partnerService.getAllPartners();
        return ResponseEntity.ok(partners); // Always return 200 with empty list if no partners
    }

    @PutMapping("/block/{id}")
    public ResponseEntity<?>  blockUser(@PathVariable Long id) {
        var partner = partnerService.blockPartner(id);
        if (partner == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }
        System.out.println("Blocked data -> " + partner);
        return ResponseEntity.ok(partner);
    }

    @PutMapping("/unblock/{id}")
    public ResponseEntity<?> unblockUser(@PathVariable Long id) {
        var partner = partnerService.unblockPartner(id);
        if (partner == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }
        System.out.println("unblocked data -> " + partner);
        return ResponseEntity.ok(partner);
    }

    @PutMapping("/verify-partner/{id}")
    public ResponseEntity<?> verifyPartner(@PathVariable Long id){
        try {
            var partner = partnerService.verifyPartner(id);
            if (partner == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Partner not found");
            }
            return ResponseEntity.ok(partner);
        }  catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PutMapping("/reject-partner/{id}")
    public ResponseEntity<Partner> rejectPartner(@PathVariable Long id,
                                                 @RequestBody RejectPartnerRequest request) {
        Partner rejectedPartner = partnerService.rejectPartner(id, request.getRejectionReason());
        return ResponseEntity.ok(rejectedPartner);
    }

    @GetMapping("/byService/{service}")
    public ResponseEntity<List<PartnerDTO>> getPartnersByService(@PathVariable String service) {
        List<PartnerDTO> partners = partnerService.getPartnersByService(service);
        return ResponseEntity.ok(partners);
    }

//    @PutMapping("/partners/{id}/status")
//    public ResponseEntity<?> updatePartnerJobStatus(
//            @PathVariable Long id,
//            @RequestBody Map<String, Object> statusUpdate) {
//        System.out.println("Job status:"+statusUpdate);
//        Optional<Partner> optionalPartner = partnerRepository.findById(id);
//        if (optionalPartner.isPresent()) {
//            Partner partner = optionalPartner.get();
//
//            String jobStatus = (String) statusUpdate.get("jobStatus");
//            LocalDate startDate = statusUpdate.get("startDate") != null ? LocalDate.parse((String) statusUpdate.get("startDate")) : null;
//            LocalDate endDate = statusUpdate.get("endDate") != null ? LocalDate.parse((String) statusUpdate.get("endDate")) : null;
//
//            // Update partner's status and dates based on the jobStatu
//            if ("IN_PROGRESS".equalsIgnoreCase(jobStatus)) {
//                partner.setJobStatus("IN_PROGRESS");
//                partner.setStartDate(startDate);
//                partnerEarningService.updatePartnerEarnings(partner, jobStatus, null);  // No earnings for in-progress jobs
//            } else if ("COMPLETED".equalsIgnoreCase(jobStatus)) {
//                partner.setJobStatus("COMPLETED");
//                partner.setEndDate(endDate);
//                BigDecimal earnings = partnerEarningService.calculateEarnings(partner);  // Add logic to calculate earnings
//                partnerEarningService.updatePartnerEarnings(partner, jobStatus, earnings);
//            } else if ("CANCELLED".equalsIgnoreCase(jobStatus)) {
//                partner.setJobStatus("CANCELLED");
//                partner.setStartDate(null);  // Reset dates if canceled
//                partner.setEndDate(null);
//                partnerEarningService.updatePartnerEarnings(partner, jobStatus, BigDecimal.ZERO);  // No earnings for canceled jobs
//            }
//
//            partnerRepository.save(partner);  // Save changes to the partner status
//            return ResponseEntity.ok("Partner job status updated successfully");
//        } else {
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Partner not found");
//        }
//    }

    @PutMapping("/partners/{id}/status")
    public ResponseEntity<Map<String, String>> updatePartnerJobStatus(
            @PathVariable Long id,
            @RequestBody Map<String, Object> statusUpdate) {
        System.out.println("Job status:" + statusUpdate);

        Optional<Partner> optionalPartner = partnerRepository.findById(id);
        if (optionalPartner.isPresent()) {
            Partner partner = optionalPartner.get();

            String jobStatus = (String) statusUpdate.get("jobStatus");
            LocalDate startDate = statusUpdate.get("startDate") != null ? LocalDate.parse((String) statusUpdate.get("startDate")) : null;
            LocalDate endDate = statusUpdate.get("endDate") != null ? LocalDate.parse((String) statusUpdate.get("endDate")) : null;

            // Update partner's status and dates based on the jobStatus
            if ("IN_PROGRESS".equalsIgnoreCase(jobStatus)) {
                partner.setJobStatus("IN_PROGRESS");
                partner.setStartDate(startDate);
                partnerEarningService.updatePartnerEarnings(partner, jobStatus, null);  // No earnings for in-progress jobs
            } else if ("COMPLETED".equalsIgnoreCase(jobStatus)) {
                partner.setJobStatus("COMPLETED");
                partner.setEndDate(endDate);
                BigDecimal earnings = partnerEarningService.calculateEarnings(partner);  // Logic to calculate earnings
                partnerEarningService.updatePartnerEarnings(partner, jobStatus, earnings);
            } else if ("CANCELLED".equalsIgnoreCase(jobStatus)) {
                partner.setJobStatus("CANCELLED");
                partner.setStartDate(null);  // Reset dates if canceled
                partner.setEndDate(null);
                partnerEarningService.updatePartnerEarnings(partner, jobStatus, BigDecimal.ZERO);  // No earnings for canceled jobs
            } else {
                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("error", "Invalid job status");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
            }

            partnerRepository.save(partner);  // Save changes to the partner status

            Map<String, String> successResponse = new HashMap<>();
            successResponse.put("message", "Partner job status updated successfully");
            return ResponseEntity.ok(successResponse);
        } else {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Partner not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }
    }


    @GetMapping("/partners/{id}/status")
    public ResponseEntity<?> getPartnerJobStatus(@PathVariable Long id) {
        Optional<Partner> optionalPartner = partnerRepository.findById(id);
        if (optionalPartner.isPresent()) {
            Partner partner = optionalPartner.get();
            Map<String, Object> response = new HashMap<>();
            response.put("jobStatus", partner.getJobStatus());
            response.put("startDate", partner.getStartDate());
            response.put("endDate", partner.getEndDate());

            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Partner not found");
        }
    }
}
