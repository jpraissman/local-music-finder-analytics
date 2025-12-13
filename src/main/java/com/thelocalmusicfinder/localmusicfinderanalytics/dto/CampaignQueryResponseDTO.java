package com.thelocalmusicfinder.localmusicfinderanalytics.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CampaignQueryResponseDTO {
    private int totalUsers;
    private int totalUniqueUsers;
    private Platform[] platforms;
}

class Platform{
    private String platformName;
    private int totalUsers;
    private int totalUniqueUsers;
   private Subgroup[] subgroups;
}

class Subgroup{
    private String subgroupName;
    private int totalUsers;
    private int totalUniqueUsers;
    private PostMemo[] paths;
}

class PostMemo{
    Path[] path;
    int totalUsers;
    int totalUniqueUsers;
}

class Path{
    int totalUsers;
    int totalUniqueUsers;
}