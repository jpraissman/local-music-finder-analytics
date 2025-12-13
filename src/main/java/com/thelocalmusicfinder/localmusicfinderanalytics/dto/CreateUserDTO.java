package com.thelocalmusicfinder.localmusicfinderanalytics.dto;

import lombok.*;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@Getter
public class CreateUserDTO {
    protected String location;
    protected String browser;
    protected String referrer;
    protected String operatingSystem;
}
