package com.ReachU.ServiceBookingSystem.services.partner;

import com.ReachU.ServiceBookingSystem.dto.PartnerDTO;
import com.ReachU.ServiceBookingSystem.entity.Partner;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;

public interface PartnerService {
    PartnerDTO getPartnerById(Long id);
    PartnerDTO updatePartner(Long id, PartnerDTO partnerDTO, MultipartFile image);
    List<Partner> getAllPartners();
    Partner blockPartner(Long id);
    Partner unblockPartner(Long id);
    Partner verifyPartner(Long id);
    Partner rejectPartner(Long id, String rejectionReason);
    List<PartnerDTO> getPartnersByService(String service);
}
