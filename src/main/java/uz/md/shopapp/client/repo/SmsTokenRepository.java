package uz.md.shopapp.client.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uz.md.shopapp.client.requests.SmsToken;

@Repository
public interface SmsTokenRepository extends JpaRepository<SmsToken, Long> {

}