package org.route;

import lombok.Builder;
import lombok.Setter;


@Builder
public record Customer(String fullName, String phone, String email) {

}
