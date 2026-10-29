package ZooCapDelivery.ZooCapDelivery.Objects;

import com.mongodb.lang.NonNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "Users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
            @Id
            String Id;

            @Indexed(unique = true)
            @NonNull
             String userName;

             @Indexed(unique = true)
             @NonNull
             String password;

             String email;

             String phNumber;

             String role;

             String returantId;
}
