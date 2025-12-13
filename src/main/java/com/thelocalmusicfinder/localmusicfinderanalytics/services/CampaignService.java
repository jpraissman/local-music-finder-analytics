package com.thelocalmusicfinder.localmusicfinderanalytics.services;

import com.thelocalmusicfinder.localmusicfinderanalytics.dto.CampaignResponseDTO;
import com.thelocalmusicfinder.localmusicfinderanalytics.dto.CreateCampaignDTO;
import com.thelocalmusicfinder.localmusicfinderanalytics.dto.GetCampaignDTO;
import com.thelocalmusicfinder.localmusicfinderanalytics.dto.GetLinkResponseDTO;
import com.thelocalmusicfinder.localmusicfinderanalytics.models.Campaign;
import com.thelocalmusicfinder.localmusicfinderanalytics.models.CampaignUserEvent;
import com.thelocalmusicfinder.localmusicfinderanalytics.models.User;
import com.thelocalmusicfinder.localmusicfinderanalytics.repositories.CampaignRepository;
import com.thelocalmusicfinder.localmusicfinderanalytics.repositories.CampaignUserEventRepository;
import com.thelocalmusicfinder.localmusicfinderanalytics.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;
import java.util.UUID;


@Service
@RequiredArgsConstructor
public class CampaignService {
    @Autowired
    private final CampaignRepository campaignRepository;
    @Autowired
    private final CampaignUserEventRepository campaignUserEventRepository;
    @Autowired
    private final UserRepository userRepository;

    @Value("${lmf.frontend-url}")
    private String frontendURL;

    public String createCampaign(CreateCampaignDTO payload) {
        Campaign campaign = new Campaign();

        if(
                payload.getPlatform() == null || payload.getPlatform().isEmpty() ||
                payload.getSubgroup()==null || payload.getSubgroup().isEmpty() ||
                payload.getPostMemo() == null || payload.getPostMemo().isEmpty()
        ) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid payload " + payload);
        }

        campaign.setPlatform(payload.getPlatform());
        campaign.setSubgroup(payload.getSubgroup());
        campaign.setPostUrl(payload.getPostMemo());
        try{
            Campaign createdCampaign = campaignRepository.save(campaign);
            return createdCampaign.getKey().toString();
            } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
        }
    }

    /**
     * fetch link for a campaign, check if userid / user info is passed, if no user exists create new one
     * @param payload browser info + campaign is
     * @return target link and current user id
     */
    public GetLinkResponseDTO getLink(GetCampaignDTO payload){
        UUID user_id;
        if(payload.getUser_id().isEmpty()){
            // create new user
            User u = new User();
            u.setLocation(payload.getLocation());
            u.setOperatingSystem(payload.getOperatingSystem());
            u.setBrowser(payload.getBrowser());
            u.setReferrer(payload.getReferrer());

            User savedUser = userRepository.save(u);
            user_id = savedUser.getId();
        }else{
            user_id = UUID.fromString(payload.getUser_id().get());
        }

        Optional<Campaign> c = campaignRepository.findById(user_id);
        if(c.isPresent()){
            // log event in repo
            return new GetLinkResponseDTO(c.get().getTargetUrl(), user_id);
        }else{
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }
}
