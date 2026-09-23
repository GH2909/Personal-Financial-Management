package Personal.Finance.Manager.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserResponse {

    private Long userId;
    private String fullname;
    private String email;
}