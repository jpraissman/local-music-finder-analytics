package com.thelocalmusicfinder.localmusicfinderanalytics.dto.user;

import lombok.*;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@Getter
@NoArgsConstructor
public class CreateUserDTO {
    protected String location;
    protected String browser;
    protected String referrer;
    protected String operatingSystem;
}
